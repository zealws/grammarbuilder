package skyql.main;

public class Main {
	
	public static void main(String[] args) {
		String query = "select x,y,z from table_name where x = y and y = z";
		Parser parser = new Parser();
		System.out.println(parser.parse(query));
	}

}
