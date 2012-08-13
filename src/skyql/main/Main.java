package skyql.main;

import java.io.InputStreamReader;

import skyql.builders.Creator;
import skyql.builders.CreatorStream;
import skyql.query.Query;

public class Main {
	
	public static void main(String[] args) throws Exception {
		CreatorStream stream = new CreatorStream(new InputStreamReader(System.in));
		while(stream.hasMoreTokens()) {
			System.out.println(Creator.read(stream,Query.class));
		}
	}

}
