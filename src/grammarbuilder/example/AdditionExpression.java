package grammarbuilder.example;

import grammarbuilder.Parsable;
import grammarbuilder.Symbol;

@Parsable
public class AdditionExpression extends Expression {

	@Symbol(suffix = "+")
	private String lhs;

	@Symbol
	private String rhs;

}
