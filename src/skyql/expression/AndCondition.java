package skyql.expression;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import skyql.main.ParserStream;
import skyql.main.Util;

public class AndCondition {
	
	private List<Condition> clauses;
	
	public AndCondition(List<Condition> clauses) {
		this.clauses = clauses;
	}
	
	@Override
	public String toString() {
		return Util.join(clauses, " and ");
	}
	
	public static AndCondition read(ParserStream stream) throws IOException {
		List<Condition> results = new LinkedList<Condition>();
		boolean cont = true;
		while(cont) {
			results.add(Condition.read(stream));
			cont = stream.compareAndDiscardIfEq("and", false);
		}
		return new AndCondition(results);
	}
}
