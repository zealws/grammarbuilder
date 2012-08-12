package skyql.main;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.List;

import org.junit.Test;

public class ParserTest {
	
	@Test
	public void testSimpleQueries() {
		final List<String> queries =
				Arrays.asList(
						"select x, y, z from t",
						"sElEcT * fRoM t",
						"select x,y,z from table_name order by x, y , z"
						);
		final List<String> results =
				Arrays.asList(
						"SELECT [x, y, z] FROM 't'",
						"SELECT [*] FROM 't'",
						"SELECT [x, y, z] FROM 'table_name' ORDER BY [x, y, z]"
						);
		Parser parser = new Parser();
		for(int i = 0; i < queries.size(); i++) {
			assertEquals(results.get(i),parser.parse(queries.get(i)).toString());
		}
	}

}
