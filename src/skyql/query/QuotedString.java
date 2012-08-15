package skyql.query;

import skyql.main.BuildableClass.Buildable;
import skyql.main.TokenField.Token;

@Buildable(prefix="'",suffix="'")
public class QuotedString extends Value {

	@Token
	private String value;
	
	public QuotedString(String value) {
		this.value = value;
	}
	
	@Override
	public String toString() {
		return "'" + value + "'";
	}
}
