package grammarbuilder;

import java.lang.reflect.Field;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.List;

public class ClassAccessor {

	private Class<?> clazz;
	protected Object instance;

	public ClassAccessor(Class<?> clazz) {
		this(clazz, null);
	}

	public ClassAccessor(Class<?> clazz, Object instance) {
		this.clazz = clazz;
		this.instance = instance;
	}

	public ClassAccessor(Object instance) {
		this(instance.getClass(), instance);
	}

	public String getFieldTypeName(String name) {
		return getRawField(name).getType().getSimpleName();
	}

	protected Field getRawField(String name) {
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

	public ClassAccessor getField(String name) {
		if (instance == null)
			return new ClassAccessor(getRawField(name).getType());
		else {
			try {
				Field f = getRawField(name);
				return new ClassAccessor(f.getType(), f.get(instance));
			} catch (Exception e) {
				throw new RuntimeException("Could not create instance of " + clazz.getSimpleName(), e);
			}
		}
	}

	public void setField(String name, Object value) {
		if (instance == null)
			throw new RuntimeException("No instance initialized. Create ClassAccessor with instance instead of class.");
		Field f = getRawField(name);
		try {
			f.setAccessible(true);
			f.set(instance, value);
		} catch (IllegalArgumentException e) {
			throw new RuntimeException("Could not set field " + name + " of instance of " + clazz.getSimpleName(), e);
		} catch (IllegalAccessException e) {
			throw new RuntimeException("Could not set field " + name + " of instance of " + clazz.getSimpleName(), e);
		}
	}

	public List<ClassAccessor> getFieldTypes() {
		List<ClassAccessor> results = new LinkedList<ClassAccessor>();
		for (Field f : clazz.getDeclaredFields()) {
			results.add(new ClassAccessor(f.getType()));
		}
		return results;
	}

	public Hashtable<String, ClassAccessor> getFields() {
		Hashtable<String, ClassAccessor> results = new Hashtable<String, ClassAccessor>();
		for (Field f : clazz.getDeclaredFields()) {
			results.put(f.getName(), new ClassAccessor(f.getType()));
		}
		return results;
	}

	public Object getInstance() {
		if (this.instance == null) {
			try {
				this.instance = clazz.newInstance();
			} catch (Exception e) {
				throw new RuntimeException("Could not create instance of " + clazz.getSimpleName(), e);
			}
		}
		return this.instance;
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

	@Override
	public String toString() {
		return String.format("ClassAccessor<%s>", getName());
	}
}
