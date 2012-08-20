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

package com.zealjagannatha.grammarbuilder.sample.expression;

import java.io.File;
import java.io.IOException;

import com.zealjagannatha.grammarbuilder.Buildable;
import com.zealjagannatha.grammarbuilder.grammar.formatter.FileFormatWriter;
import com.zealjagannatha.grammarbuilder.grammar.formatter.HtmlFormatter;

@Buildable(prefix="(",suffix=")",resolvers={AdditionExpression.class,SubtractionExpression.class})
public abstract class Expression {

    public static void main(String[] args) throws IOException {
        FileFormatWriter.writeFile(new File("/home/zeal/public_html/grammarbuilder/expression/Grammar.htm"), new HtmlFormatter(), Expression.class);
    }

}
