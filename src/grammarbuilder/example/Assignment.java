package grammarbuilder.example;

import grammarbuilder.Parsable;
import grammarbuilder.Symbol;

@Parsable
public class Assignment extends Statement {

	@Symbol(suffix = "=")
	private String name;

	@Symbol
	private Expression expr;

}
