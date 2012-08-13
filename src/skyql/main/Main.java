package skyql.main;

import skyql.query.Query;


public class Main {
	
	public static void main(String[] args) throws Exception {
		//System.out.println(Creator.read(new CreatorStream(new InputStreamReader(System.in)), Query.class));
		System.out.println(Creator.generateGrammar(Query.class));
	}

}
