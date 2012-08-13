package skyql.query;

import skyql.main.Creator.Buildable;
import skyql.main.Creator.Token;

@Buildable
public class TableName extends Value {
	
	@Token
	private Identifier name;
	
	public TableName(Identifier name) {
		this.name = name;
	}

	@Override
	public String toString() {
		return name.toString();
	}

}