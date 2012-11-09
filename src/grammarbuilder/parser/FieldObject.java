package grammarbuilder.parser;

import grammarbuilder.AnnotatedClassAccessor;
import grammarbuilder.Symbol;

public class FieldObject extends ParseObject {

	private AnnotatedClassAccessor listSubtype;
	private boolean optional;
	private String padding;

	public FieldObject(String name, AnnotatedClassAccessor type, Symbol sym) {
		super(type);
		this.prefix = sym.prefix();
		this.suffix = sym.suffix();
		this.listSubtype = new AnnotatedClassAccessor(sym.subtype());
		this.optional = sym.optional();
		this.ignoreCase = sym.ignoreCase();
		this.padding = sym.padding();
	}

	public AnnotatedClassAccessor getListSubtype() {
		return listSubtype;
	}

	public boolean isOptional() {
		return optional;
	}

	public String getPadding() {
		return padding;
	}

	public boolean isPrimitive() {
		return type.isPrimitive();
	}
}
