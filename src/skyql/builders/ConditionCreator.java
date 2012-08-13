package skyql.builders;

import java.util.Arrays;

import skyql.query.ComparisonCondition;
import skyql.query.Condition;
import skyql.query.Expression;
import skyql.query.NotCondition;
import skyql.query.ParenCondition;

public class ConditionCreator extends Creator<Condition> {

	public ConditionCreator(CreatorStream stream) {
		super(stream);
	}

	@Override
	public Condition read() throws Exception {
		if(stream.compareAndDiscardIfEq("not", true)) {
			return new NotCondition(read(stream,Condition.class));
		} else if(stream.compareAndDiscardIfEq("(", false)) {
			Expression expr = read(stream,Expression.class);
			stream.assertEqualsAndDiscard(")", false);
			return new ParenCondition(expr);
		} else {
			String column = stream.assertMatchesAndReturn("\\w+");
			String operation = stream.assertContainsAndReturn(Arrays.asList("=","!=","<>",">","<",">=","<="));
			String value = stream.nextToken(); // TODO check this
			return new ComparisonCondition(column, operation, value);
		}
	}

}
