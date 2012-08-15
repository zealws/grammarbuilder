package skyql.main;

import static skyql.main.Parser.nt;
import static skyql.main.Parser.sc;
import static skyql.main.Parser.lt;
import static skyql.main.Parser.t;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Field;
import java.util.LinkedList;
import java.util.List;

public class TokenField implements AnnotatedDeclaration {
	
	@Retention(RetentionPolicy.RUNTIME)
	@Target(ElementType.FIELD)
	public @interface Token {
		int position() default 0;
		String[] prefix() default {};
		String[] suffix() default {};
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

	@Override
	public Object read(CreatorStream stream) throws IOException {
		boolean use = true;
		Object result = null;
		if(token.optional())
			use = stream.compareAndDiscardIfEq(token.prefix(), token.ignoreCase());
		else 
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

	@Override
	public String generateGrammar() {
		StringBuilder grammar = new StringBuilder();
		if(token.optional()) {
			grammar.append(sc("[")+" ");
		}
		for(String pre : token.prefix())
			grammar.append(lt(pre)+" ");
		basicString(grammar);
		for(String suf : token.suffix())
			grammar.append(lt(suf)+" ");
		if(token.optional()) {
			grammar.append(" "+sc("]")+" ");
		}
		return grammar.toString();
	}

	private void basicString(StringBuilder grammar) {
		if(getType().isAssignableFrom(List.class)){
			grammar.append("List<");
			if(getListSubtype() != String.class)
				grammar.append(nt(getListSubtype().getSimpleName()));
			else
				grammar.append(t("String"));
			grammar.append(",");
			grammar.append(lt(token.padding()));
			grammar.append("> ");
		} else if(getType() == String.class) {
			if(token.matches().equals("")) {
				grammar.append(t("String "));
			} else
				grammar.append(t("String(")+lt(token.matches())+t(")"));
				
		}
		else
			grammar.append(nt(getType().getSimpleName())+" ");
	}
	
	public String getName() {
		return field.getName();
	}
	
	@Override
	public String toString() {
		return field.getType().getSimpleName();
	}
	
	@SuppressWarnings("unchecked")
	private static <K> List<K> readList(String padding, CreatorStream stream, Class<K> clazz) throws IOException {
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

	@Override
	public List<AnnotatedDeclaration> getSubdeclarations() {
		List<AnnotatedDeclaration> results = new LinkedList<AnnotatedDeclaration>();
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

}
