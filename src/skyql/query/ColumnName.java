package skyql.query;

import skyql.main.BuildableClass.Buildable;
import skyql.main.TokenField.Token;

@Buildable
public class ColumnName extends Value {
	
	@Token
	private Identifier name;
	
	public ColumnName(Identifier name) {
		this.name = name;
	}

	@Override
	public String toString() {
		return name.toString();
	}

}
