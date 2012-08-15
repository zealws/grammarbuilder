package skyql.query;

import skyql.main.BuildableClass.Buildable;
import skyql.main.TokenField.Token;

@Buildable(prefix="not")
public class NotCondition extends Condition {
	
	@Token
	public Condition condition;
	
	public NotCondition(Condition condition) {
		this.condition = condition;
	}

	@Override
	public String toString() {
		return "not " + condition.toString();
	}

}
