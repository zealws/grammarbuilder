package skyql.query;

import skyql.main.BuildableClass.Buildable;

@Buildable(resolvers={QuotedString.class,ColumnName.class})
public abstract class Value {
	
	@Override
	public abstract String toString();

}
