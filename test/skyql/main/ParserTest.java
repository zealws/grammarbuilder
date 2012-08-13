package skyql.main;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.List;

import org.junit.Test;

import skyql.builders.QueryCreator;
import skyql.query.Query;

public class ParserTest {
	
	@Test
	public void testSimpleQueries() throws Exception {
		final List<String> queries =
				Arrays.asList(
						"select x, y, z from t;",
						"sElEcT * fRoM t;",
						"select x,y,z from table_name order by x, y , z;",
						"select x,y,z from table_name where x = y and y = z;",
						"select x,y,z from table_name where x = y or y = z;"
						);
		final List<String> results =
				Arrays.asList(
						"SELECT [x, y, z] FROM 't'",
						"SELECT [*] FROM 't'",
						"SELECT [x, y, z] FROM 'table_name' ORDER BY [x, y, z]",
						"SELECT [x, y, z] FROM 'table_name' WHERE x = y AND y = z",
						"SELECT [x, y, z] FROM 'table_name' WHERE x = y OR y = z"
						);
		for(int i = 0; i < queries.size(); i++) {
			assertEquals(results.get(i),QueryCreator.read(queries.get(i),Query.class).toString());
		}
	}

}
