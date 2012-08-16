package skyql.query;



import com.zealjagannatha.parsebuilder.BuildableClass.Buildable;
import com.zealjagannatha.parsebuilder.TokenField.Token;

@Buildable
public class ComparisonCondition extends Condition {
	
	@Token(position=0)
	private ColumnName column;
	
	@Token(position=1)
	private String op;
	
	@Token(position=2)
	private Value value;
	
	public ComparisonCondition(ColumnName column, String op, Value value) {
		this.column = column;
		this.op = op;
		this.value = value;
	}

	@Override
	public String toString() {
		return String.format("%s %s %s",column,op,value);
	}

}
