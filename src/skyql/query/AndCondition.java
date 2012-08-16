package skyql.query;



import java.util.List;

import com.zealjagannatha.parsebuilder.Util;
import com.zealjagannatha.parsebuilder.BuildableClass.Buildable;
import com.zealjagannatha.parsebuilder.TokenField.Token;


@Buildable
public class AndCondition {
	
	@Token(padding="and",subtype=Condition.class)
	private List<Condition> clauses;
	
	public AndCondition(List<Condition> clauses) {
		this.clauses = clauses;
	}
	
	@Override
	public String toString() {
		return Util.join(clauses, " AND ");
	}
}
