package grammarbuilder.example;

import grammarbuilder.Parsable;
import grammarbuilder.Symbol;

import java.util.List;

@Parsable
public class Program {

	@Symbol(subtype = Statement.class, padding = ";")
	private List<Statement> statements;

	public List<Statement> getStatements() {
		return statements;
	}

}
