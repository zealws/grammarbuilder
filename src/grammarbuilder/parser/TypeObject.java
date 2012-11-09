package grammarbuilder.parser;

import grammarbuilder.AnnotatedClassAccessor;
import grammarbuilder.OrderedHash;
import grammarbuilder.Parsable;

import java.util.List;

public class TypeObject extends ParseObject {

	protected Class<?>[] resolvers;
	protected OrderedHash<String, FieldObject> fields = new OrderedHash<String, FieldObject>();

	public TypeObject(AnnotatedClassAccessor accessor) {
		super(accessor);
		Parsable build = accessor.getBuildable();
		this.resolvers = build.resolvers();
		this.prefix = build.prefix();
		this.suffix = build.suffix();
		this.ignoreCase = build.ignoreCase();
	}

	public void setResolver(Class<?>[] resolvers) {
		this.resolvers = resolvers;
	}

	public Class<?>[] getResolvers() {
		return resolvers;
	}

	public void addField(FieldObject field) {
		fields.put(field.getName(), field);
	}

	public FieldObject getField(String name) {
		return fields.get(name);
	}

	public boolean hasField(String name) {
		return fields.containsKey(name);
	}

	public FieldObject getField(int pos) {
		return fields.get(pos);
	}

	public int getNumFields() {
		return fields.size();
	}

	public List<String> getFields() {
		return fields.getKeys();
	}

	@Override
	public String toString() {
		return String.format("TypeObject<%s>", getName());
	}
}
