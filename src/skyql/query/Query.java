package skyql.query;

import com.zealjagannatha.parsebuilder.BuildableClass.Buildable;

@Buildable(resolvers={SelectQuery.class},suffix=";")
public abstract class Query {
	
	@Override
	public abstract String toString();

}
