package skyql.query;

import java.util.List;

import skyql.main.BuildableClass.Buildable;
import skyql.main.TokenField.Token;
import skyql.main.Util;

@Buildable
public class ColumnList {

	@Token(subtype=ColumnName.class)
	public List<ColumnName> names;
	
	public ColumnList(List<ColumnName> names) {
		this.names = names;
	}
	
	@Override
	public String toString() {
		return "[" + Util.join(names, ", ") + "]";
	}
	
}
