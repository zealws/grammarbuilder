package grammarbuilder;

import java.lang.reflect.Field;
import java.util.Hashtable;
import java.util.List;

public class AnnotatedClassAccessor extends ClassAccessor {

	private static final Class<?>[] primitiveTypes = { String.class, long.class, int.class, double.class, List.class };

	public AnnotatedClassAccessor(Class<?> clazz) {
		this(clazz, null);
	}

	public AnnotatedClassAccessor(Class<?> clazz, Object instance) {
		super(clazz, instance);
	}

	public AnnotatedClassAccessor(Object instance) {
		this(instance.getClass(), instance);
	}

	public boolean isBuildable() {
		return getClazz().getAnnotation(Parsable.class) != null;
	}

	public Parsable getBuildable() {
		return getClazz().getAnnotation(Parsable.class);
	}

	public boolean isSymbol(String fieldName) {
		return getRawField(fieldName).getAnnotation(Symbol.class) != null;
	}

	public Symbol getSymbol(String fieldName) {
		return getRawField(fieldName).getAnnotation(Symbol.class);
	}

	@Override
	public AnnotatedClassAccessor getField(String name) {
		if (instance == null)
			return new AnnotatedClassAccessor(getRawField(name).getType());
		else {
			try {
				Field f = getRawField(name);
				return new AnnotatedClassAccessor(f.getType(), f.get(instance));
			} catch (Exception e) {
				throw new RuntimeException("Could not create instance of " + getClazz().getSimpleName(), e);
			}
		}
	}

	public Hashtable<String, AnnotatedClassAccessor> getAnnotatedFields() {
		Hashtable<String, AnnotatedClassAccessor> results = new Hashtable<String, AnnotatedClassAccessor>();
		for (Field f : getClazz().getDeclaredFields()) {
			results.put(f.getName(), new AnnotatedClassAccessor(f.getType()));
		}
		return results;
	}

	public boolean isPrimitive() {
		for (Class<?> primitive : primitiveTypes) {
			if (primitive == getClazz())
				return true;
		}
		return false;
	}

}
