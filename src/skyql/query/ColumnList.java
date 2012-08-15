package skyql.query;

import java.util.List;

import com.zealjagannatha.parsebuilder.Util;
import com.zealjagannatha.parsebuilder.BuildableClass.Buildable;
import com.zealjagannatha.parsebuilder.TokenField.Token;


@Buildable
public class ColumnList {

	@Token(subtype=ColumnName.class)
	public List<ColumnName> names;
	
	public ColumnList(List<ColumnName> names) {
		this.names = names;
	}
	
	@Override
	public String toString() {
		return "[" + Util.join(names, ", ") + "]";
	}
	
}
