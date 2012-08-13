package skyql.builders;

import java.util.List;

import skyql.query.Expression;
import skyql.query.SelectQuery;

public class SelectQueryCreator extends Creator<SelectQuery> {

	public SelectQueryCreator(CreatorStream stream) {
		super(stream);
	}

	@Override
	public SelectQuery read() throws Exception {
		List<String> columns = readList(stream,String.class);
		// from clause
		stream.assertEqualsAndDiscard("from",true);
		String table = stream.assertMatchesAndReturn("\\w+");
		// Check for where
		Expression expression = null;
		if(stream.compareAndDiscardIfEq("where",true))
			expression = read(stream,Expression.class);
		// Check for order
		List<String> orderings = null;
		if(stream.compareAndDiscardIfEq("order",true)) {
			stream.assertEqualsAndDiscard("by",true);
			orderings = readList(stream,String.class);
		}
		return new SelectQuery(columns,table,expression,orderings);
	}

}
