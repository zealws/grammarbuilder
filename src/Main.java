

import java.io.File;

import skyql.query.Query;

import com.zealjagannatha.parsebuilder.Parser;
import com.zealjagannatha.parsebuilder.grammar.formatter.EbnfFormatter;
import com.zealjagannatha.parsebuilder.grammar.formatter.FileFormatWriter;
import com.zealjagannatha.parsebuilder.grammar.formatter.GrammarFormatter;
import com.zealjagannatha.parsebuilder.grammar.formatter.HtmlFormatter;

public class Main {
	public static void main(String[] args) throws Exception {
		FileFormatWriter.writeFile(new File("/Users/zeal/Desktop/SkyQL/grammar.txt"),new GrammarFormatter());
		FileFormatWriter.writeFile(new File("/Users/zeal/Desktop/SkyQL/grammar.htm"),new HtmlFormatter());
		FileFormatWriter.writeFile(new File("/Users/zeal/Desktop/SkyQL/grammar.ebnf"),new EbnfFormatter());
		
		System.out.println(Parser.parse("select * from table order by x asc, y desc;", Query.class));
	}
}
