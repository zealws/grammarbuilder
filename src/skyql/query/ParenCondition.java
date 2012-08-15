package skyql.query;

import skyql.main.BuildableClass.Buildable;
import skyql.main.TokenField.Token;

@Buildable(prefix="(",suffix=")")
public class ParenCondition extends Condition {
	
	@Token
	private Expression expression;
	
	public ParenCondition(Expression expression) {
		this.expression = expression;
	}

	@Override
	public String toString() {
		return "(" + expression.toString() + ")";
	}

}
