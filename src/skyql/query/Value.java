package skyql.query;

import skyql.main.Creator.Buildable;

@Buildable(resolvers={QuotedString.class,ColumnName.class})
public abstract class Value {
	
	@Override
	public abstract String toString();

}
