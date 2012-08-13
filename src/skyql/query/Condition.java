package skyql.query;

import skyql.main.Creator.Buildable;

@Buildable(resolvers={NotCondition.class,ParenCondition.class,ComparisonCondition.class})
public abstract class Condition {
	@Override
	public abstract String toString();

}
