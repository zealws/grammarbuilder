package skyql.expression;

public class ParenCondition extends Condition {
	
	private Expression expression;
	
	public ParenCondition(Expression expression) {
		this.expression = expression;
	}

	@Override
	public String toString() {
		return "(" + expression.toString() + ")";
	}

}
