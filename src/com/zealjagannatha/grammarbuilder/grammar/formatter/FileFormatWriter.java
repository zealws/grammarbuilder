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

package com.zealjagannatha.grammarbuilder.grammar.formatter;

import com.zealjagannatha.grammarbuilder.Parser;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class FileFormatWriter {

    public static <T,V> void writeFile(File output, GrammarFormatter<V> formatter, Class<T> toUse) throws IOException {
        FileWriter writer = new FileWriter(output);
        writer.write(formatter.formatGrammar(Parser.generateActualGrammar(toUse)).toString());
        writer.close();
    }

}
