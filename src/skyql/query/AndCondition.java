package skyql.query;

import java.util.List;

import skyql.main.Util;

public class AndCondition {
	
	private List<Condition> clauses;
	
	public AndCondition(List<Condition> clauses) {
		this.clauses = clauses;
	}
	
	@Override
	public String toString() {
		return Util.join(clauses, " AND ");
	}
}
