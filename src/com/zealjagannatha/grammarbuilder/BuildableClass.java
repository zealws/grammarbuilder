package com.zealjagannatha.grammarbuilder;

//Copyright 2012 Zeal Jagannatha
//
//Licensed under the Apache License, Version 2.0 (the "License");
//you may not use this file except in compliance with the License.
//You may obtain a copy of the License at
//
//  http://www.apache.org/licenses/LICENSE-2.0
//
//Unless required by applicable law or agreed to in writing, software
//distributed under the License is distributed on an "AS IS" BASIS,
//WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
//See the License for the specific language governing permissions and
//limitations under the License.

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Vector;

import com.zealjagannatha.grammarbuilder.ParserLookaheadStream.LookaheadEndOfStream;
import com.zealjagannatha.grammarbuilder.grammar.Literal;
import com.zealjagannatha.grammarbuilder.grammar.NonTerminal;
import com.zealjagannatha.grammarbuilder.grammar.ProductionRhs;
import com.zealjagannatha.grammarbuilder.grammar.RhsValue;


public class BuildableClass {

    private Buildable build;
	private Class<?> clazz;

	public BuildableClass(Class<?> clazz) {
		this.clazz = clazz;
		Annotation preBuildable = clazz.getAnnotation(Buildable.class);
		if(!(preBuildable instanceof Buildable))
			throw new ParseException("Attempt to create BuildableClass from non-buildable object: "+clazz.getSimpleName());
		build = (Buildable) preBuildable;
	}

	public List<TokenField> getAnnotatedFields() {
		List<Field> prelim = new Vector<Field>();
		for(Field field : clazz.getDeclaredFields()) {
			Annotation an = field.getAnnotation(Token.class);
			if(an instanceof Token) {
				prelim.add(field);
			}
		}
		
		List<TokenField> results = new LinkedList<TokenField>();
		for(int i = 0; i < prelim.size(); i++) {
			for(Field field : prelim) {
				Token an = field.getAnnotation(Token.class);
				if(an.position() == i)
					results.add(new TokenField(field));
			}
		}
		return results;
	}

	private String[] prefix() {
		return build.prefix();
	}

	private boolean ignoreCase() {
		return build.ignoreCase();
	}

	public Object read(ParserStream stream) throws IOException {
		//System.out.println("Reading "+getName());
		Object result;
		stream.assertEqualsAndDiscard(build.prefix(), build.ignoreCase());
		
		if(resolverClass()) {
			List<BuildableClass> resolvers = getResolvers();
			BuildableClass match = findResolver(stream, resolvers);
			result = match.read(stream);
		} else {
			List<TokenField> fields = getAnnotatedFields();
			Object[] params = readFields(stream, fields);
			//System.out.println("["+Util.join(getFieldTypes(fields),",")+"]");
			Constructor<?> ctor;
			try {
				ctor = clazz.getConstructor(getFieldTypes(fields));
			} catch (NoSuchMethodException e) {
				throw new ParseException(String.format("Constructor %s(%s) must exist.",getName(),Util.join(fields,",")),e);
			} catch (SecurityException e) {
				throw new ParseException(String.format("Constructor %s(%s) must exist.",getName(),Util.join(fields,",")),e);
			}
			try {
				result = ctor.newInstance(params);
			} catch (IllegalArgumentException e) {
				Class<?>[] types = getFieldTypes(fields);
				for(int i = 0; i < params.length; i++) {
					if(params[i].getClass() != types[i])
						throw new RuntimeException(String.format("Mismatched type: %s in place of %s",params[i].getClass(),types[i]));
				}
				throw new RuntimeException("Mismatched type... Couldn't find error type.");
			} catch (Exception e) {
				throw new RuntimeException(String.format("Could not initialize %s with constructor.",getName()));
			}
		}
		stream.assertEqualsAndDiscard(build.suffix(), build.ignoreCase());
		return result;
	}

	private Class<?>[] getFieldTypes(List<TokenField> fields) {
		Class<?>[] types = new Class<?>[fields.size()];
		for(int i = 0; i < fields.size(); i++) {
			types[i] = fields.get(i).getType();
		}
		return types;
	}

	private Object[] readFields(ParserStream stream, List<TokenField> fields) throws IOException {
		List<Object> results = new ArrayList<Object>(fields.size());
		for(TokenField field : fields) {
			results.add(field.read(stream));
		}
		return results.toArray();
	}
	
	private String[] suffix() {
		return build.suffix();
	}

	public BuildableClass findResolver(ParserStream stream, List<BuildableClass> resolvers) throws IOException {
		String next = stream.peekToken();
		BuildableClass match = null;
		BuildableClass def = null;
		for(BuildableClass resolver : resolvers) {
			if(resolver.prefix().length == 0) {
				if(def == null) {
					def = resolver;
				} else {
					throw new RuntimeException(String.format("Conflicting default resolvers for type %s: %s and %s",getName(),resolver.getName(),def.getName()));
				}
			} else {
				boolean matches = false;
				matches = stream.compareAndPassIfEq(resolver.prefix(), resolver.ignoreCase());
				if(matches) {
					match = resolver;
					break;
				}
			}
		}
		if(match == null && def == null)
			throw new RuntimeException("No appropriate resolvers for class "+clazz.getSimpleName()+" on "+next);
		else if(match == null && def != null)
			match = def;
		//System.out.println("    Using resolver "+match.getSimpleName());
		return match;
	}
	
	public boolean resolverClass() {
		return build.resolvers().length != 0;
	}
	
	public List<BuildableClass> getResolvers() {
		Class<?>[] resolvers = build.resolvers();
		List<BuildableClass> buildables = new LinkedList<BuildableClass>();
		for(Class<?> resolver : resolvers) {
			if(clazz.isAssignableFrom(resolver))
				buildables.add(new BuildableClass(resolver));
			else
				throw new RuntimeException("Resolver "+resolver.getSimpleName()+" must extend "+getName());
		}
		return buildables;
	}

	public String getName() {
		return clazz.getSimpleName();
	}
	
	@Override
	public String toString() {
		return getName();
	}

	public List<BuildableClass> getSubdeclarations() {
		List<BuildableClass> results = new LinkedList<BuildableClass>();
		if(resolverClass()) {
			for(BuildableClass c : getResolvers())
				results.add(c);
		} else {
			for(TokenField f : getAnnotatedFields())
				results.addAll(f.getSubdeclarations());
		}
		return results;
	}
	
	@Override
	public boolean equals(Object other) {
		if(!(other instanceof BuildableClass))
			return false;
		BuildableClass o = (BuildableClass) other;
		return clazz.equals(o.clazz);
	}

	public void nextToken(ParserLookaheadStream stream) throws IOException {
		//System.out.println("Next token for "+getName());
		stream.assertEqualsAndDiscard(build.prefix(), build.ignoreCase());
		
		if(resolverClass()) {
			List<BuildableClass> resolvers = getResolvers();
			BuildableClass match = nextTokenResolver(stream, resolvers);
			match.nextToken(stream);
		} else {
			List<TokenField> fields = getAnnotatedFields();
			nextTokenFields(stream, fields);
		}
		stream.assertEqualsAndDiscard(build.suffix(), build.ignoreCase());
	}

	private BuildableClass nextTokenResolver(ParserLookaheadStream stream,
			List<BuildableClass> resolvers) throws IOException {
		BuildableClass match = null;
		BuildableClass def = null;
		for(BuildableClass resolver : resolvers) {
			if(resolver.prefix().length == 0) {
				if(def == null) {
					def = resolver;
				} else {
					throw new RuntimeException(String.format("Conflicting default resolvers for type %s: %s and %s",getName(),resolver.getName(),def.getName()));
				}
			} else {
				boolean matches = false;
				matches = stream.compareAndPassIfEq(resolver.prefix(), resolver.ignoreCase());
				if(matches) {
					match = resolver;
					break;
				}
			}
		}
		if(match == null && def == null) {
			for(BuildableClass c : resolvers) {
				if(c.prefix().length > 0)
					stream.setNextToken(c.prefix()[0]);
			}
			throw new LookaheadEndOfStream();
		}
		else if(match == null && def != null)
			match = def;
		//System.out.println("    Using resolver "+match.getSimpleName());
		return match;
	}

	private void nextTokenFields(ParserLookaheadStream stream,
			List<TokenField> fields) throws IOException {
		for(TokenField field : fields) {
			field.nextToken(stream);
		}
		return;
	}

	public List<ProductionRhs> generateProductions() {
		List<ProductionRhs> results = new LinkedList<ProductionRhs>();
		if(resolverClass()) {
			for(BuildableClass r : getResolvers()) {
				LinkedList<RhsValue> values = new LinkedList<RhsValue>();
				for(String pre : prefix())
					values.add(new Literal(pre));
				values.add(new NonTerminal(r.getName()));
				for(String suf : suffix())
					values.add(new Literal(suf));
				results.add(new ProductionRhs(values));
			}
		} else {
			List<RhsValue> values = new LinkedList<RhsValue>();
			for(String pre : prefix())
				values.add(new Literal(pre));
			for(TokenField f : getAnnotatedFields())
				values.addAll(f.getRhsValues());
			for(String suf : suffix())
				values.add(new Literal(suf));
			results.add(new ProductionRhs(values));
		}
		return results;
	}

}
