package skyql.query;

import java.util.List;

import skyql.main.BuildableClass.Buildable;
import skyql.main.TokenField.Token;
import skyql.main.Util;

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
