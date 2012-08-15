package skyql.query;

import skyql.main.BuildableClass.Buildable;

@Buildable(resolvers={SelectQuery.class},suffix=";")
public abstract class Query {
	
	@Override
	public abstract String toString();

}
