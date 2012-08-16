package skyql.query;

import com.zealjagannatha.parsebuilder.BuildableClass.Buildable;
import com.zealjagannatha.parsebuilder.TokenField.Token;

@Buildable(prefix="\"",suffix="\"")
public class DoubleQuotedString extends QuotedString {

	@Token
	private String value;
	
	public DoubleQuotedString(String value) {
		this.value = value;
	}
	
	@Override
	public String toString() {
		return "'" + value + "'";
	}

}
