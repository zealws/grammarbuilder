package skyql.query;

import java.io.File;
import java.io.FileWriter;

import com.zealjagannatha.parsebuilder.Parser;

public class Main {
	public static void main(String[] args) throws Exception {
		File outFile = new File("/Users/zeal/Desktop","grammar.htm");
		System.out.println(Parser.generateActualGrammar(Query.class));
		//System.out.println(Parser.parse("select x from y;", Query.class));
		//System.out.println(Parser.getNextToken("select ", Query.class));
	}
}
