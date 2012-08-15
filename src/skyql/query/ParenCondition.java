package skyql.query;

import com.zealjagannatha.parsebuilder.BuildableClass.Buildable;
import com.zealjagannatha.parsebuilder.TokenField.Token;

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
