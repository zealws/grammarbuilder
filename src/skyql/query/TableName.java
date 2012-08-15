package skyql.query;

import com.zealjagannatha.parsebuilder.BuildableClass.Buildable;
import com.zealjagannatha.parsebuilder.TokenField.Token;

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