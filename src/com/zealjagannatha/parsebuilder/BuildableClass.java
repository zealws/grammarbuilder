package com.zealjagannatha.parsebuilder;

import static com.zealjagannatha.parsebuilder.Parser.lhs;
import static com.zealjagannatha.parsebuilder.Parser.lt;
import static com.zealjagannatha.parsebuilder.Parser.nt;
import static com.zealjagannatha.parsebuilder.Parser.sc;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Vector;

import com.zealjagannatha.parsebuilder.TokenField.Token;


public class BuildableClass implements AnnotatedDeclaration {
	
	@Retention(RetentionPolicy.RUNTIME)
	@Target(ElementType.TYPE)
	public @interface Buildable {
		String[] prefix() default {};
		String[] suffix() default {};
		boolean ignoreCase() default true;
		Class<?>[] resolvers() default {};
	}

	private Buildable build;
	private Class<?> clazz;

	public BuildableClass(Class<?> clazz) {
		this.clazz = clazz;
		Annotation preBuildable = clazz.getAnnotation(Buildable.class);
		if(!(preBuildable instanceof Buildable))
			throw new ParseException("Attempt to create BuildableClass from non-buildable object: "+clazz.getSimpleName());
		build = (BuildableClass.Buildable) preBuildable;
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

	@Override
	public Object read(ParserStream stream) throws IOException {
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
			} catch (SecurityException | NoSuchMethodException e) {
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

	@Override
	public String generateGrammar() {
		StringBuilder grammar = new StringBuilder();
		grammar.append(lhs(getName()));
		grammar.append(" "+sc(":=")+Parser.nl());
		if(resolverClass()) {
			boolean first = true;
			for(BuildableClass resolver : getResolvers()) {
				if(!first) {
					grammar.append(sc("|")+Parser.nl());
				}
				first = false;
				grammar.append(Parser.tab());
				for(String pre : prefix())
					grammar.append(lt(pre)+" ");
				grammar.append(nt(resolver.getName()));
				grammar.append(" ");
				for(String suf : suffix())
					grammar.append(lt(suf)+" ");
			}
			grammar.append(Parser.nl());
		} else {
			grammar.append(Parser.tab());
			for(String pre : prefix())
				grammar.append(lt(pre)+" ");
			for(TokenField field : getAnnotatedFields()) {
				grammar.append(field.generateGrammar());
			}
			for(String suf : suffix())
				grammar.append(lt(suf)+" ");
			grammar.append(Parser.nl());
		}
		return grammar.toString();
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

	private String getName() {
		return clazz.getSimpleName();
	}
	
	@Override
	public String toString() {
		return getName();
	}

	@Override
	public List<AnnotatedDeclaration> getSubdeclarations() {
		List<AnnotatedDeclaration> results = new LinkedList<AnnotatedDeclaration>();
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

}
