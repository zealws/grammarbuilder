package skyql.query;

import java.util.List;

import skyql.main.BuildableClass.Buildable;
import skyql.main.TokenField.Token;
import skyql.main.Util;

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
