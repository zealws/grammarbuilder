package skyql.query;

import java.util.List;

import skyql.main.Util;
import skyql.main.Creator.Buildable;
import skyql.main.Creator.Token;

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
