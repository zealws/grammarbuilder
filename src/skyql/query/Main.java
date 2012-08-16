package skyql.query;

import java.io.File;
import java.io.FileWriter;

import com.zealjagannatha.parsebuilder.Parser;
import com.zealjagannatha.parsebuilder.grammar.HtmlGrammarFormatter;

public class Main {
	public static void main(String[] args) throws Exception {
		FileWriter writer = new FileWriter(new File("/home/zeal/","grammar.htm"));
		String res = new HtmlGrammarFormatter().formatGrammar(Parser.generateActualGrammar(Query.class));
		System.out.println(res);
		writer.write(res);
		writer.close();
	}
}
