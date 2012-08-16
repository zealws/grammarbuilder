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
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import com.zealjagannatha.grammarbuilder.ParserLookaheadStream.LookaheadEndOfStream;
import com.zealjagannatha.parsebuilder.grammar.ListSymbol;
import com.zealjagannatha.parsebuilder.grammar.Literal;
import com.zealjagannatha.parsebuilder.grammar.NonTerminal;
import com.zealjagannatha.parsebuilder.grammar.OptionalRhsValue;
import com.zealjagannatha.parsebuilder.grammar.RhsValue;
import com.zealjagannatha.parsebuilder.grammar.Symbol;

public class TokenField {
	
	@Retention(RetentionPolicy.RUNTIME)
	@Target(ElementType.FIELD)
	public @interface Token {
		int position() default 0;
		String[] prefix() default {};
		String[] suffix() default {};
		String[] either() default {};
		boolean optional() default false;
		boolean ignoreCase() default true;
		Class<?> subtype() default Object.class;
		String padding() default ",";
		boolean greedy() default false;
		String matches() default "";
	}
	
	private Token token;
	private Field field;
	
	public TokenField(Field field) {
		this.field = field;
		Annotation preToken = field.getAnnotation(Token.class);
		if(!(preToken instanceof Token))
			throw new RuntimeException("Attempt to create TokenField from non-token field: "+field.getName());
		token = (Token) preToken;
	}
	
	public boolean isList() {
		return field.getType().isAssignableFrom(List.class);
	}
	
	public Class<?> getListSubtype() {
		if(token.subtype() == Object.class)
			 throw new IllegalStateException("Template type without subtype parameter: "+getName());
		 return token.subtype();
	}
	
	public Class<?> getType() {
		return field.getType();
	}
	
	public boolean terminal() {
		return field.getType() == String.class;
	}

	public Object read(ParserStream stream) throws IOException {
		//System.out.println("Reading "+getName());
		boolean use = true;
		Object result = null;
		if(token.optional()) {
			if(token.either().length != 0)
				use = stream.compareContainsAndIgnore(token.either(), token.ignoreCase());
			else if(token.prefix().length != 0)
				use = stream.compareAndDiscardIfEq(token.prefix(), token.ignoreCase());
			else
				throw new ParseException("Optional field but no way to determine it: "+getName());
		} else 
			stream.assertEqualsAndDiscard(token.prefix(), token.ignoreCase());
		if(use) {
			if(isList())
				result = readList(token.padding(),stream,token.subtype());
			else if(field.getType() == String.class)
				result = stream.nextToken();
			else
				result = new BuildableClass(field.getType()).read(stream);
			stream.assertEqualsAndDiscard(token.suffix(), token.ignoreCase());
			return result;
		}
		else
			return null;
	}
	
	public String getName() {
		return field.getName();
	}
	
	@Override
	public String toString() {
		return field.getType().getSimpleName();
	}
	
	@SuppressWarnings("unchecked")
	private static <K> List<K> readList(String padding, ParserStream stream, Class<K> clazz) throws IOException {
		List<K> results = new LinkedList<K>();
		boolean cont = true;
		while(cont) {
			if(clazz == String.class)
				results.add((K) stream.nextToken());
			else
				results.add((K) new BuildableClass(clazz).read(stream));
			cont = stream.compareAndDiscardIfEq(padding, false);
		}
		return results;
	}

	public List<BuildableClass> getSubdeclarations() {
		List<BuildableClass> results = new LinkedList<BuildableClass>();
		if(isList() && getListSubtype() != String.class)
			results.add(new BuildableClass(getListSubtype()));
		else if(!isList() && getType() != String.class)
			results.add(new BuildableClass(getType()));
		return results;
	}
	
	private Token getToken() {
		return token;
	}
	
	private Field getField() {
		return field;
	}
	
	@Override
	public boolean equals(Object other) {
		if(!(other instanceof TokenField))
			return false;
		TokenField o = (TokenField) other;
		return token == o.getToken() && field == o.getField();
	}

	public void nextToken(ParserLookaheadStream stream2) throws IOException {
		//System.out.println("Next token for field "+field.getName());
		ParserLookaheadStream stream;
		if(token.optional())
			stream = stream2.clone();
		else
			stream = stream2;
		stream.assertEqualsAndDiscard(token.prefix(), token.ignoreCase());
		if(isList()) {
			nextTokenList(token.padding(),stream,token.subtype());
		}
		else if(field.getType() == String.class) {
			if(stream.nextToken() == null) {
				stream.setNextToken(field.getName());
				throw new LookaheadEndOfStream();
			}
		}
		else
			new BuildableClass(field.getType()).nextToken(stream);
		stream.assertEqualsAndDiscard(token.suffix(), token.ignoreCase());
		return;
	}

	private <K> void nextTokenList(String padding, ParserLookaheadStream stream,
			Class<K> clazz) throws IOException {
		boolean cont = true;
		while(cont) {
			if(clazz == String.class) {
				if(stream.nextToken() == null) {
					stream.setNextToken(field.getName());
					throw new LookaheadEndOfStream();
				}
			}
			else
				new BuildableClass(clazz).nextToken(stream);
			cont = stream.compareAndDiscardIfEq(padding, false);
		}
		return;
	}

	public List<RhsValue> getRhsValues() {
		if(token.optional()) {
			List<Symbol> values = new LinkedList<Symbol>();
			for(String pre : token.prefix())
				values.add(new Literal(pre));
			if(isList()){
				values.add(new ListSymbol(new NonTerminal(getListSubtype().getSimpleName()),new Literal(token.padding())));
			} else {
				values.add(new NonTerminal(getType().getSimpleName()));
			}
			for(String suf : token.suffix())
				values.add(new Literal(suf));
			return Arrays.asList((RhsValue) new OptionalRhsValue(values));
		} else {
			List<RhsValue> values = new LinkedList<RhsValue>();
			for(String pre : token.prefix())
				values.add(new Literal(pre));
			if(isList()){
				values.add(new ListSymbol(new NonTerminal(getListSubtype().getSimpleName()),new Literal(token.padding())));
			} else {
				values.add(new NonTerminal(getType().getSimpleName()));
			}
			for(String suf : token.suffix())
				values.add(new Literal(suf));
			return values;
		}
	}

}
