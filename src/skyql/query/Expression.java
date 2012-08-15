package skyql.query;

import java.util.List;

import com.zealjagannatha.parsebuilder.Util;
import com.zealjagannatha.parsebuilder.BuildableClass.Buildable;
import com.zealjagannatha.parsebuilder.TokenField.Token;


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
