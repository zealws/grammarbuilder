package src.com.zealjagannatha.grammarbuilder.grammar.formatter;

//Copyright 2012 Zeal Jagannatha
//
//Licensed under the Apache License, Version 2.0 (the "License");
//you may not use this file except in compliance with the License.
//You may obtain a copy of the License at
//
//  http://www.apache.org/licenses/LICENSE-2.0
//
//Unless required by applicable law or agreed to in writing, software
//distributed under the License is distributed on an "AS IS" BASIS,
//WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
//See the License for the specific language governing permissions and
//limitations under the License.

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import skyql.query.Query;

import com.zealjagannatha.grammarbuilder.Parser;

public class FileFormatWriter {
	
	public static void writeFile(File output, GrammarFormatter formatter) throws IOException {
		FileWriter writer = new FileWriter(output);
		writer.write(formatter.formatGrammar(Parser.generateActualGrammar(Query.class)));
		writer.close();
	}

}
