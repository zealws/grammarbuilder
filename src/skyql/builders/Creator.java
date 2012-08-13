package skyql.builders;

import java.io.IOException;
import java.io.StringReader;
import java.lang.reflect.Constructor;
import java.util.LinkedList;
import java.util.List;


public abstract class Creator<T> {
	
	protected CreatorStream stream;
	
	public Creator(CreatorStream stream) {
		this.stream = stream;
	}
	
	public abstract T read() throws Exception;
	
	@SuppressWarnings("unchecked")
	private static <K> Class<? extends Creator<K>> getBuilder(Class<K> clazz) {
		try {
			return (Class<? extends Creator<K>>) Class.forName("skyql.builders."+clazz.getSimpleName()+"Creator");
		} catch (ClassNotFoundException e) {
			throw new RuntimeException("Could not find builder class for "+clazz.getSimpleName(),e);
		}
	}
	
	private static <K> K readWith(CreatorStream stream, Class<? extends Creator<K>> clazz) throws Exception {
		Creator<K> reader;
		Constructor<? extends Creator<K>> ct = clazz.getConstructor(CreatorStream.class);
		reader = (Creator<K>) ct.newInstance(stream);
		return reader.read();
	}
	
	public static <K> K read(CreatorStream stream, Class<K> clazz) throws Exception {
		return readWith(stream, getBuilder(clazz));
	}
	
	public static <K> K read(String toParse, Class<K> clazz) throws Exception {
		return readWith(new CreatorStream(new StringReader(toParse)), getBuilder(clazz));
	}
	
	@SuppressWarnings("unchecked")
	protected static <K> List<K> readList(CreatorStream stream, Class<K> clazz) throws Exception {
		if(clazz == String.class)
			return (List<K>) readStringList(stream);
		List<K> results = new LinkedList<K>();
		boolean cont = true;
		while(cont) {
			results.add(read(stream,clazz));
			cont = stream.compareAndDiscardIfEq(",", false);
		}
		return results;
	}
	
	private static List<String> readStringList(CreatorStream stream) throws IOException {
		List<String> results = new LinkedList<String>();
		boolean cont = true;
		while(cont) {
			results.add(stream.nextToken());
			cont = stream.compareAndDiscardIfEq(",", false);
		}
		return results;
	}
}
