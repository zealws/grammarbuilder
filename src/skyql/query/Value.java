package skyql.query;

import com.zealjagannatha.parsebuilder.BuildableClass.Buildable;

@Buildable(resolvers={QuotedString.class,ColumnName.class})
public abstract class Value {
	
	@Override
	public abstract String toString();

}
