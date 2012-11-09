package grammarbuilder.parser;

import grammarbuilder.AnnotatedClassAccessor;

import java.util.Hashtable;
import java.util.LinkedList;
import java.util.List;

public class ParseTree {

	private Hashtable<String, TypeObject> types = new Hashtable<String, TypeObject>();

	public void addType(Class<?> clazz) {
		if (types.containsKey(clazz.getSimpleName()))
			return;
		AnnotatedClassAccessor accessor = new AnnotatedClassAccessor(clazz);
		if (!accessor.isBuildable())
			throw new RuntimeException("Cannot add non-buildable type " + clazz.getSimpleName() + " to ParseTree.");
		TypeObject type = new TypeObject(accessor);
		Hashtable<String, AnnotatedClassAccessor> fields = accessor.getAnnotatedFields();
		List<Class<?>> toAdd = new LinkedList<Class<?>>();
		for (Class<?> resolver : type.getResolvers())
			toAdd.add(resolver);
		for (String fieldName : fields.keySet()) {
			if (accessor.isSymbol(fieldName)) {
				FieldObject field = new FieldObject(fieldName, fields.get(fieldName), accessor.getSymbol(fieldName));
				field.setName(fieldName);
				type.addField(field);
				if (!fields.get(fieldName).isPrimitive())
					toAdd.add(fields.get(fieldName).getClazz());
			}
		}
		types.put(type.getName(), type);
		for (Class<?> adding : toAdd) {
			addType(adding);
		}
	}

	public boolean containsType(String name) {
		return types.containsKey(name);
	}

	public TypeObject getType(String name) {
		return types.get(name);
	}

}
