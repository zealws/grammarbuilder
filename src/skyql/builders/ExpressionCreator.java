package skyql.builders;

import java.util.LinkedList;
import java.util.List;

import skyql.query.AndCondition;
import skyql.query.Expression;

public class ExpressionCreator extends Creator<Expression> {

	public ExpressionCreator(CreatorStream stream) {
		super(stream);
	}

	@Override
	public Expression read() throws Exception {
		List<AndCondition> results = new LinkedList<AndCondition>();
		boolean cont = true;
		while(cont) {
			results.add(read(stream,AndCondition.class));
			cont = stream.compareAndDiscardIfEq("or", false);
		}
		return new Expression(results);
	}

}
