package grammarbuilder;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.LinkedList;
import java.util.List;

public class ClassAccessor<T> {

	private Class<T> clazz;

	public ClassAccessor(Class<T> clazz) {
		this.clazz = clazz;
	}

	public String getFieldTypeName(String name) {
		return getRawField(name).getType().getSimpleName();
	}

	Field getRawField(String name) {
		try {
			return clazz.getDeclaredField(name);
		} catch (Exception e) {
			throw new RuntimeException("Could not find field " + name + " of class " + clazz.getSimpleName(), e);
		}
	}

	public boolean hasField(String name) {
		for (Field f : clazz.getDeclaredFields()) {
			if (f.getName().equals(name))
				return true;
		}
		return false;
	}

	@SuppressWarnings("unchecked")
	public <F> ClassAccessor<F> getField(String name) {
		try {
			return new ClassAccessor<F>((Class<F>) getRawField(name).getType());
		} catch (Exception e) {
			throw new RuntimeException("Could not create instance of " + clazz.getSimpleName(), e);
		}
	}

	public <F> ClassAccessor<F> getField(String name, Class<F> type) {
		if (getRawField(name).getType() != type)
			throw new RuntimeException(String.format("Invalid type %s when fetching field %s", type.getSimpleName(), name));
		else {
			try {
				return new ClassAccessor<F>(type);
			} catch (Exception e) {
				throw new RuntimeException("Could not create instance of " + clazz.getSimpleName(), e);
			}
		}
	}

	public List<String> getFieldNames() {
		List<String> results = new LinkedList<String>();
		for (Field f : clazz.getDeclaredFields())
			results.add(f.getName());
		return results;
	}

	public <F> void setField(T object, String name, F value) {
		try {
			Field f = getRawField(name);
			f.setAccessible(true);
			f.set(object, value);
		} catch (Exception e) {
			throw new RuntimeException("Could not set field " + name + " of instance of " + clazz.getSimpleName(), e);
		}
	}

	public T getInstance() {
		try {
			return clazz.newInstance();
		} catch (Exception e) {
			throw new RuntimeException("Could not create instance of " + clazz.getSimpleName(), e);
		}
	}

	public Class<?> getClazz() {
		return clazz;
	}

	public String getName() {
		return clazz.getSimpleName();
	}

	public boolean typeEquals(Class<?> clazz) {
		return this.clazz.equals(clazz);
	}

	public boolean hasAnnotation(Class<? extends Annotation> clazz) {
		return this.clazz.getAnnotation(clazz) != null;
	}

	public boolean hasAnnotatedField(String name, Class<? extends Annotation> clazz) {
		return hasField(name) && (getRawField(name).getAnnotation(clazz) != null);
	}

	public boolean subclassOf(ClassAccessor<?> maybeSup) {
		return maybeSup.clazz.isAssignableFrom(clazz);
	}

	public boolean superclassOf(ClassAccessor<?> maybeSub) {
		return clazz.isAssignableFrom(maybeSub.clazz);
	}

	public <A extends Annotation> A getAnnotation(Class<A> type) {
		return clazz.getAnnotation(type);
	}

	public <X> boolean subclassOf(Class<X> clazz) {
		return subclassOf(new ClassAccessor<X>(clazz));
	}

	public <A extends Annotation> A getFieldAnnotation(String fieldName, Class<A> type) {
		return getRawField(fieldName).getAnnotation(type);
	}
}
