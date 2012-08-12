package skyql.expression;

import java.io.IOException;
import java.util.Arrays;

import skyql.main.ParserStream;

public abstract class Condition {
	
	@Override
	public abstract String toString();
	
	public static Condition read(ParserStream stream) throws IOException {
		if(stream.compareAndDiscardIfEq("not", true)) {
			return new NotCondition(read(stream));
		} else if(stream.compareAndDiscardIfEq("(", false)) {
			Expression expr = Expression.read(stream);
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
