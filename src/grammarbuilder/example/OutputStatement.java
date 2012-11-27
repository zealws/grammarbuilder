package grammarbuilder.example;

import grammarbuilder.Parsable;
import grammarbuilder.Symbol;

@Parsable(prefix = "output")
public class OutputStatement extends Statement {

	@Symbol
	private Expression expr;

}
