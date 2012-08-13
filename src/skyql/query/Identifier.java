package skyql.query;

import skyql.main.Creator.Buildable;
import skyql.main.Creator.Token;

@Buildable(matches="\\w+")
public class Identifier {
	
	@Token
	private String name;
	
	public Identifier(String name) {
		this.name = name;
	}

	@Override
	public String toString() {
		return name;
	}

}
