package grammarbuilder.parser;

import grammarbuilder.AnnotatedClassAccessor;

public abstract class ParseObject {

	protected String[] prefix;
	protected String[] suffix;
	protected String name;
	protected AnnotatedClassAccessor type;
	protected boolean ignoreCase;

	protected ParseObject(AnnotatedClassAccessor type) {
		this(type.getName(), type);
	}

	protected ParseObject(String name, AnnotatedClassAccessor type) {
		this.name = name;
		this.type = type;
	}

	public String[] getPrefix() {
		return prefix;
	}

	public void setPrefix(String... prefix) {
		this.prefix = prefix;
	}

	public String[] getSuffix() {
		return suffix;
	}

	public void setSuffix(String... suffix) {
		this.suffix = suffix;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public AnnotatedClassAccessor getType() {
		return type;
	}

	public boolean isIgnoreCase() {
		return ignoreCase;
	}

}
