package skyql.query;

public class NotCondition extends Condition {
	
	public Condition condition;
	
	public NotCondition(Condition condition) {
		this.condition = condition;
	}

	@Override
	public String toString() {
		return "not " + condition.toString();
	}

}
