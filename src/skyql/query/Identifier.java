package skyql.query;

import com.zealjagannatha.parsebuilder.BuildableClass.Buildable;
import com.zealjagannatha.parsebuilder.TokenField.Token;

@Buildable
public class Identifier {
	
	@Token(matches="\\w+")
	private String name;
	
	public Identifier(String name) {
		this.name = name;
	}

	@Override
	public String toString() {
		return name;
	}

}
