package skyql.query;



import com.zealjagannatha.parsebuilder.BuildableClass.Buildable;
import com.zealjagannatha.parsebuilder.TokenField.Token;

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
