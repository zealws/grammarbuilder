package skyql.query;

import com.zealjagannatha.parsebuilder.BuildableClass.Buildable;
import com.zealjagannatha.parsebuilder.TokenField.Token;

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
