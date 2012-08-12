package skyql.main;

import java.io.IOException;
import java.io.StringReader;
import java.util.LinkedList;
import java.util.List;

import skyql.expression.Expression;

public class Parser {
	
	private ParserStream stream;
	
	public Query parse(String toParse) {
		Query result;
		stream = new ParserStream(new StringReader(toParse));
		try {
			String token = stream.nextToken();
			if(token.equalsIgnoreCase("select"))
				result = readSelectQuery();
			else
				throw new RuntimeException("invalid query type: "+token);
		} catch (Exception e) {
			throw new RuntimeException("Could not parse stream.",e);
		}
		if(stream.hasMoreTokens()) {
			throw new RuntimeException("Did not consume all input. Remaining tokens: "+stream.allTokens().toString());
		}
		return result;
	}
	
	public SelectQuery readSelectQuery() throws IOException {
		List<String> columns = readCommaSeparatedList();
		// from clause
		stream.assertEqualsAndDiscard("from",true);
		String table = stream.assertMatchesAndReturn("\\w+");
		// Check for where
		Expression expression = null;
		if(stream.compareAndDiscardIfEq("where",true))
			expression = Expression.read(stream);
		// Check for order
		List<String> orderings = null;
		if(stream.compareAndDiscardIfEq("order",true)) {
			stream.assertEqualsAndDiscard("by",true);
			orderings = readCommaSeparatedList();
		}
		return new SelectQuery(columns,table,expression,orderings);
	}

	private List<String> readCommaSeparatedList() throws IOException {
		List<String> results = new LinkedList<String>();
		boolean cont = true;
		while(cont) {
			results.add(stream.nextToken());
			cont = stream.compareAndDiscardIfEq(",", false);
		}
		return results;
	}

}
