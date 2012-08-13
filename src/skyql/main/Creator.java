package skyql.main;

import java.io.StringReader;
import java.lang.annotation.Annotation;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.List;
import java.util.Vector;


public abstract class Creator<T> {
	
	private static final String tab = "    ";
	
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
	}
	
	@Retention(RetentionPolicy.RUNTIME)
	@Target(ElementType.TYPE)
	public @interface Buildable {
		String[] prefix() default {};
		String[] suffix() default {};
		boolean ignoreCase() default true;
		// For use with stubs
		Class<?>[] resolvers() default {};
		String matches() default "";
	}
	
	@SuppressWarnings("unchecked")
	public static <K> K read(CreatorStream stream, Class<K> toRead) throws Exception {
		if(toRead == String.class)
			return (K) stream.nextToken();
		K result;
		//System.out.println("Reading "+toRead.getSimpleName());
		Annotation preBuildable = toRead.getAnnotation(Buildable.class);
		if(!(preBuildable instanceof Buildable))
			throw new RuntimeException("Cannot read non-buildable object: "+toRead.getSimpleName());
		Buildable build = (Buildable) preBuildable;
		
		// Ditch the prefixes
		for(String prefix : build.prefix()) {
			stream.assertEqualsAndDiscard(prefix, build.ignoreCase());
		}
		
		if(build.resolvers().length != 0) {
			//System.out.println("    Stub found, looking for resolvers.");
			Class<? extends K>[] resolvers = (Class<? extends K>[]) build.resolvers();
			String next = stream.peekToken();
			Class<? extends K> match = null;
			for(Class<? extends K> resolver : resolvers) {
				Annotation preBuildable2 = resolver.getAnnotation(Buildable.class);
				if(!(preBuildable instanceof Buildable))
					throw new RuntimeException("Cannot use non-buildable resolver "+resolver.getSimpleName()
							+" for class "+toRead.getSimpleName());
				Buildable resolveBuild = (Buildable) preBuildable2;
				boolean matches = false;
				if(resolveBuild.matches().equals(""))
					matches = stream.compareAndPassIfEq(resolveBuild.prefix(), resolveBuild.ignoreCase());
				else
					matches = stream.compareMatchesAndPass(resolveBuild.matches());
				if(matches) {
					match = resolver;
					break;
				}
			}
			if(match == null)
				throw new RuntimeException("No appropriate resolvers for class "+toRead.getSimpleName()+" on "+next);
			//System.out.println("    Using resolver "+match.getSimpleName());
			result = read(stream,match);
		} else {
			// Read the fields
			Field[] fields = getProperFields(toRead);
			//System.out.println("["+Util.join(getFieldTypes(fields),",")+"]");
			Constructor<K> ctor = toRead.getConstructor(getFieldTypes(fields));
			Object[] params = new Object[fields.length];
			for(int i = 0; i < fields.length; i++) {
				params[i] = readField(stream,fields[i]);
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
			}
		}
		stream.assertEqualsAndDiscard(build.suffix(), build.ignoreCase());
		return result;
	}
	
	private static Hashtable<String,String> grammars = new Hashtable<String,String>();
	
	@SuppressWarnings("unchecked")
	public static <K> String generateGrammar(Class<K> clazz) {
		StringBuilder result = new StringBuilder();
		HashSet<Class<?>> allClasses = new HashSet<Class<?>>();
		allClasses.add(clazz);
		int oldSize = 0;
		while(oldSize != allClasses.size()) {
			oldSize = allClasses.size();
			for(int i = 0; i < oldSize; i++) {
				Class<?> c = (Class<?>) allClasses.toArray()[i];
				generateRawGrammar(c,allClasses);
			}
		}
		for(Class<?> c : allClasses) {
			result.append(generateRawGrammar(c,allClasses));
		}
		return result.toString();
	}

	private static <K> String generateRawGrammar(Class<K> clazz, HashSet<Class<?>> toGenerate) {
		if(grammars.get(clazz.getSimpleName()) != null)
			return grammars.get(clazz.getSimpleName());
		StringBuilder result = new StringBuilder();
		Annotation preBuildable = clazz.getAnnotation(Buildable.class);
		if(!(preBuildable instanceof Buildable))
			return "";
		Buildable build = (Buildable) preBuildable;
		result.append(clazz.getSimpleName());
		result.append(" :=\n");
		if(build.resolvers().length != 0) {
			boolean first = true;
			for(Class<?> resolver : build.resolvers()) {
				if(!first) {
					result.append("|\n");
				}
				first = false;
				result.append(tab);
				for(String pre : build.prefix()) {
					result.append("\"");
					result.append(pre);
					result.append("\"");
					result.append(" ");
				}
				toGenerate.add(resolver);
				result.append(resolver.getSimpleName());
				result.append(" ");
				for(String pre : build.suffix()) {
					result.append("\"");
					result.append(pre);
					result.append("\"");
					result.append(" ");
				}
			}
			result.append("\n");
		} else {
			result.append(tab);
			for(String pre : build.prefix()) {
				result.append("\"");
				result.append(pre);
				result.append("\"");
				result.append(" ");
			}
			Field[] fields = getProperFields(clazz);
			for(Field field : fields) {
				Token token = field.getAnnotation(Token.class);
				if(token.subtype() == Object.class)
					toGenerate.add(field.getType());
				else
					toGenerate.add(token.subtype());
				result.append(generateGrammar(field,toGenerate));
				result.append(" ");
			}
			for(String pre : build.suffix()) {
				result.append("\"");
				result.append(pre);
				result.append("\"");
				result.append(" ");
			}
		}
		result.append("\n\n");
		return result.toString();
	}
	
	private static Object generateGrammar(Field field, HashSet<Class<?>> toGenerate) {
		Token token = field.getAnnotation(Token.class);
		StringBuilder result = new StringBuilder();
		if(token.optional())
			result.append("[ ");
		for(String pre : token.prefix()) {
			result.append("\"");
			result.append(pre);
			result.append("\"");
			result.append(" ");
		}
		if(token.subtype() != Object.class) {
			result.append("List<");
			result.append(token.subtype().getSimpleName());
			result.append(">");
			toGenerate.add(token.subtype());
		}
		else {
			result.append(field.getType().getSimpleName());
			toGenerate.add(field.getType());
		}
		for(String suf : token.suffix()) {
			result.append("\"");
			result.append(suf);
			result.append("\"");
			result.append(" ");
		}
		if(token.optional())
			result.append(" ]");
		return result.toString();
	}

	private static Class<?>[] getFieldTypes(Field[] fields) {
		Class<?>[] types = new Class<?>[fields.length];
		for(int i = 0; i < fields.length; i++)
			types[i] = fields[i].getType();
		return types;
	}

	private static Object readField(CreatorStream stream, Field field) throws Exception {
		Token token = field.getAnnotation(Token.class);
		boolean use = true;
		if(token.optional())
			use = stream.compareAndDiscardIfEq(token.prefix(), token.ignoreCase());
		else 
			stream.assertEqualsAndDiscard(token.prefix(), token.ignoreCase());
		if(use) {
			Object result;
			if(token.subtype() != Object.class)
				result = readList(token.padding(),stream,token.subtype());
			else
				result = read(stream,field.getType());
			stream.assertEqualsAndDiscard(token.suffix(), token.ignoreCase());
			return result;
		}
		else
			return null;
	}

	private static <K> Field[] getProperFields(Class<K> toRead) {
		List<Field> prelim = new Vector<Field>();
		for(Field field : toRead.getDeclaredFields()) {
			Annotation an = field.getAnnotation(Token.class);
			if(an instanceof Token) {
				prelim.add(field);
			}
		}
		
		Field[] results = new Field[prelim.size()]; 
		for(int i = 0; i < prelim.size(); i++) {
			for(Field field : prelim) {
				Token an = field.getAnnotation(Token.class);
				if(an.position() == i)
					results[i] = field;
			}
		}
		return results;
	}
	
	public static <K> K read(String toParse, Class<K> clazz) throws Exception {
		return read(new CreatorStream(new StringReader(toParse)), clazz);
	}
	
	private static <K> List<K> readList(String padding, CreatorStream stream, Class<K> clazz) throws Exception {
		List<K> results = new LinkedList<K>();
		boolean cont = true;
		while(cont) {
			results.add(read(stream,clazz));
			cont = stream.compareAndDiscardIfEq(padding, false);
		}
		return results;
	}
}
