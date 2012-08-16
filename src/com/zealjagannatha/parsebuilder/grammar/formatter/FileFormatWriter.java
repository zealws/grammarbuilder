package com.zealjagannatha.parsebuilder.grammar.formatter;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import skyql.query.Query;

import com.zealjagannatha.parsebuilder.Parser;

public class FileFormatWriter {
	
	public static void writeFile(File output, GrammarFormatter formatter) throws IOException {
		FileWriter writer = new FileWriter(output);
		writer.write(formatter.formatGrammar(Parser.generateActualGrammar(Query.class)));
		writer.close();
	}

}
