/*
 * Copyright 2012 Zeal Jagannatha
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package grammarbuilder.sample.json;

import grammarbuilder.Parsable;
import grammarbuilder.Symbol;
import grammarbuilder.TokenStream.Behavior;
import grammarbuilder.Util;
import grammarbuilder.parser.Parser;

import java.io.IOException;
import java.util.List;

@Parsable(prefix = "{", suffix = "}")
public class JsonHash implements JsonValue {

	@Symbol(subtype = JsonPair.class)
	private List<JsonPair> pairs;

	@Override
	public String toString() {
		return "{ " + Util.join(pairs, ", ") + "} ";
	}

	public static void main(String[] args) throws IOException {
		// FileFormatWriter.writeFile(new
		// File("/home/zeal/public_html/grammar.htm"), new HtmlFormatter(),
		// JsonHash.class);
		// FileFormatWriter.writeFile(new
		// File("/home/zeal/public_html/grammar.txt"), new JsonFormatter(),
		// JsonHash.class);
		Parser parser = new Parser();
		parser.specialChar('{', Behavior.Keep);
		parser.specialChar('}', Behavior.Keep);
		parser.specialChar(':', Behavior.Keep);
		parser.specialChar('[', Behavior.Keep);
		parser.specialChar(']', Behavior.Keep);
		parser.specialChar(',', Behavior.Keep);
		parser.specialChar('\'', Behavior.Keep);
		parser.setRootClass(JsonHash.class);
		System.out.println(parser.parse("{ 'a' : 5 }"));
	}
}
