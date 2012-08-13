package skyql.query;

import java.util.List;

import skyql.main.Util;
import skyql.main.Creator.Buildable;
import skyql.main.Creator.Token;

@Buildable
public class AndCondition {
	
	@Token(padding="and",subtype=Condition.class)
	private List<Condition> clauses;
	
	public AndCondition(List<Condition> clauses) {
		this.clauses = clauses;
	}
	
	@Override
	public String toString() {
		return Util.join(clauses, " AND ");
	}
}
