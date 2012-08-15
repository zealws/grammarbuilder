package skyql.query;

import com.zealjagannatha.parsebuilder.BuildableClass.Buildable;
import com.zealjagannatha.parsebuilder.TokenField.Token;

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
