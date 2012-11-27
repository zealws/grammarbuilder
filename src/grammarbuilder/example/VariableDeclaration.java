package grammarbuilder.example;

import grammarbuilder.Parsable;
import grammarbuilder.Symbol;

@Parsable(prefix = "var")
public class VariableDeclaration extends Statement {

	@Symbol
	private String name;

	public String getName() {
		return name;
	}

}
