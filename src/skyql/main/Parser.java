package skyql.main;

import java.io.StringReader;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Vector;

import skyql.main.TokenField.Token;


public abstract class Parser<T> {
	
	private static final String tab = "    ";
	private static Hashtable<String,String> grammars = new Hashtable<String,String>();
	
	@SuppressWarnings("unchecked")
	public static <K> K read(CreatorStream stream, Class<K> toRead) throws Exception {
		BuildableClass clazz = new BuildableClass(toRead);
		return (K) clazz.read(stream);
	}
	
	public static <K> K read(String toParse, Class<K> clazz) throws Exception {
		return read(new CreatorStream(new StringReader(toParse)), clazz);
	}
	
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
		Annotation preBuildable = clazz.getAnnotation(BuildableClass.Buildable.class);
		if(!(preBuildable instanceof BuildableClass.Buildable))
			return "";
		BuildableClass.Buildable build = (BuildableClass.Buildable) preBuildable;
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
	
	static <K> Field[] getProperFields(Class<K> toRead) {
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
	
	
}
