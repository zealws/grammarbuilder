package skyql.query;

import com.zealjagannatha.parsebuilder.BuildableClass.Buildable;

@Buildable(resolvers={SingleQuotedString.class,ColumnName.class})
public abstract class Value {
	
	@Override
	public abstract String toString();

}
