package com.zealjagannatha.parsebuilder;

import skyql.query.Query;


public class Main {
	
	public static void main(String[] args) throws Exception {
		System.out.println(Parser.read(new CreatorStream(new InputStreamReader(System.in)), Query.class));
	}

}
