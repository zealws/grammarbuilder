package skyql.main;

import java.util.List;

import skyql.expression.Expression;

public class SelectQuery implements Query {
	
	private List<String> columns;
	private Expression expression;
	private String tableName;
	private List<String> orderings;
	
	public SelectQuery(List<String> columnList, String tableName, Expression expression, List<String> orderings) {
		if(columnList == null)
			throw new IllegalStateException("Column list cannot be null.");
		if(tableName == null)
			throw new IllegalStateException("Table name cannot be null.");
		this.tableName = tableName;
		this.columns = columnList;
		this.expression = expression;
		this.orderings = orderings;
	}

	@Override
	public String toString() {
		StringBuilder result = new StringBuilder();
		result.append("SELECT "+columns.toString());
		result.append(" FROM '"+tableName+"'");
		if(expression != null)
			result.append(" WHERE "+expression.toString());
		if(orderings != null)
			result.append(" ORDER BY "+orderings.toString());
		return result.toString();
	}

	@Override
	public QueryResult Evaluate() {
		return null;
	}

}
