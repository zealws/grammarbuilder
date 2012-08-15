package skyql.main;

import skyql.query.Query;


public class Main {
	
	public static void main(String[] args) throws Exception {
		//System.out.println(Creator.read("select * from asset where id = 5;", Query.class));
		System.out.println(Parser.generateGrammar(Query.class));
	}

}
