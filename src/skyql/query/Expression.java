package skyql.query;

import java.util.List;

import skyql.main.Util;

public class Expression {
	
	private List<AndCondition> clauses;
	
	public Expression(List<AndCondition> clauses) {
		this.clauses = clauses;
	}
	
	@Override
	public String toString() {
		return Util.join(clauses, " OR ");
	}

}
