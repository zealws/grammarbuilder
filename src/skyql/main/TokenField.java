package skyql.main;

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
		return "";
	}
	
	public String getName() {
		return field.getName();
	}
	
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

}
