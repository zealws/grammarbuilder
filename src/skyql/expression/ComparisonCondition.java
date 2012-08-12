package skyql.expression;

public class ComparisonCondition extends Condition {
	
	private String column;
	private String op;
	private String value;
	
	public ComparisonCondition(String column, String op, String value) {
		this.column = column;
		this.op = op;
		this.value = value;
	}

	@Override
	public String toString() {
		return String.format("%s %s %s",column,op,value);
	}

}
