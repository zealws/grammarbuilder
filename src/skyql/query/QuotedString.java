package skyql.query;

import com.zealjagannatha.parsebuilder.BuildableClass.Buildable;

@Buildable(resolvers={DoubleQuotedString.class,SingleQuotedString.class})
public abstract class QuotedString extends Value {
	
}
