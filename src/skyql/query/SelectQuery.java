package skyql.query;

import java.util.List;

import skyql.main.BuildableClass.Buildable;
import skyql.main.TokenField.Token;

@Buildable(prefix="select")
public class SelectQuery extends Query {
	
	@Token(position=0)
	private ColumnList columns;
	
	@Token(position=1,prefix="from")
	private TableName tableName;
	
	@Token(position=2,optional=true,prefix="where")
	private Expression expression;
	
	@Token(position=3,optional=true,prefix={"order","by"},subtype=String.class)
	private List<String> orderings;
	
	public SelectQuery(ColumnList columns, TableName tableName, Expression expression, List<String> orderings) {
		if(columns == null)
			throw new IllegalStateException("Column list cannot be null.");
		if(tableName == null)
			throw new IllegalStateException("Table name cannot be null.");
		this.tableName = tableName;
		this.columns = columns;
		this.expression = expression;
		this.orderings = orderings;
	}

	@Override
	public String toString() {
		StringBuilder result = new StringBuilder();
		result.append(String.format("SELECT %s FROM '%s'",columns.toString(),tableName));
		if(expression != null)
			result.append(" WHERE "+expression.toString());
		if(orderings != null)
			result.append(" ORDER BY "+orderings.toString());
		return result.toString();
	}

}
