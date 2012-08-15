package skyql.query;

import skyql.main.BuildableClass.Buildable;
import skyql.main.TokenField.Token;

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
