package skyql.builders;

import skyql.query.Query;
import skyql.query.SelectQuery;

public class QueryCreator extends Creator<Query> {
	
	public QueryCreator(CreatorStream stream) {
		super(stream);
	}

	@Override
	public Query read() throws Exception {
		Query result;
		try {
			String token = stream.nextToken();
			if(token.equalsIgnoreCase("select")) {
				result = read(stream,SelectQuery.class);
			}
			else
				throw new RuntimeException("invalid query type: "+token);
		} catch (Exception e) {
			throw new RuntimeException("Could not parse stream.",e);
		}
		stream.assertEqualsAndDiscard(";", false);
		return result;
	}
	
	

}
