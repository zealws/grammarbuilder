package skyql.expression;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import skyql.main.ParserStream;
import skyql.main.Util;

public class Expression {
	
	private List<AndCondition> clauses;
	
	public Expression(List<AndCondition> clauses) {
		this.clauses = clauses;
	}
	
	@Override
	public String toString() {
		return Util.join(clauses, " or ");
	}
	
	public static Expression read(ParserStream stream) throws IOException {
		List<AndCondition> results = new LinkedList<AndCondition>();
		boolean cont = true;
		while(cont) {
			results.add(AndCondition.read(stream));
			cont = stream.compareAndDiscardIfEq("or", false);
		}
		return new Expression(results);
	}

}
