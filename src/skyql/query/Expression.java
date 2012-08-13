package skyql.query;

import java.util.List;

import skyql.main.Util;
import skyql.main.Creator.Buildable;
import skyql.main.Creator.Token;

@Buildable
public class Expression {
	
	@Token(padding="or",subtype=AndCondition.class)
	private List<AndCondition> clauses;
	
	public Expression(List<AndCondition> clauses) {
		this.clauses = clauses;
	}
	
	@Override
	public String toString() {
		return Util.join(clauses, " OR ");
	}

}
