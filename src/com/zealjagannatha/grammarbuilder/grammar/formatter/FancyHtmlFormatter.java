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

import com.zealjagannatha.grammarbuilder.grammar.*;

public class FancyHtmlFormatter extends HtmlFormatter {

    @Override
    public String formatGrammar(Grammar g) {
        StringBuilder result = new StringBuilder();
        result.append("<div style=\"padding:10px;font-family:'Courier New',Courier,monospace;color:#272822;\">");
        for (Production p : g.getProductions()) {
            result.append(formatProduction(p));
            result.append("<br/>");
        }
        result.append("</div>");
        return result.toString();
    }

    @Override
    protected String formatLiteral(Literal sym) {
        return String.format("\"<span style='color:#A6E22E;'>%s</span>\"", sym.getValue());
    }

    @Override
    protected String formatNonTerm(NonTerminal nt) {
        return String.format("<span style='color:orange;'>%s</span>", nt.toString());
    }

    @Override
    protected String formatListSymbol(ListSymbol sym) {
        return String.format("<span style='color:#D197D9;'>List&lt;</span>%s,%s<span style='color:#D197D9;'>&gt;</span>", formatSymbol(sym.getType()), formatSymbol(sym.getDelimiter()));
    }

    @Override
    protected String formatLhs(ProductionLhs lhs) {
        return String.format("<span style=\"color:#FF4466;\">%s</span>", lhs.toString());
    }
}
