package com.zealjagannatha.grammarbuilder.grammar.formatter;

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

import com.zealjagannatha.grammarbuilder.grammar.Grammar;
import com.zealjagannatha.grammarbuilder.grammar.ListSymbol;
import com.zealjagannatha.grammarbuilder.grammar.Literal;
import com.zealjagannatha.grammarbuilder.grammar.NonTerminal;
import com.zealjagannatha.grammarbuilder.grammar.OptionalRhsValue;
import com.zealjagannatha.grammarbuilder.grammar.Production;
import com.zealjagannatha.grammarbuilder.grammar.ProductionLhs;
import com.zealjagannatha.grammarbuilder.grammar.ProductionRhs;
import com.zealjagannatha.grammarbuilder.grammar.RhsValue;
import com.zealjagannatha.grammarbuilder.grammar.Symbol;

public class GrammarFormatter {

    public String formatGrammar(Grammar g) {
        StringBuilder result = new StringBuilder();
        for (Production p : g.getProductions()) {
            result.append(formatProduction(p));
            result.append("\n");
        }
        return result.toString();
    }

    protected String formatProduction(Production p) {
        StringBuilder result = new StringBuilder();
        result.append(formatLhs(p.getLhs()));
        result.append(" :=\n    ");
        boolean first = true;
        for (ProductionRhs rhs : p.getRhss()) {
            if (!first)
                result.append(" |\n    ");
            first = false;
            result.append(formatRhs(rhs));
        }
        result.append("\n");
        return result.toString();
    }

    protected String formatRhs(ProductionRhs rhs) {
        StringBuilder result = new StringBuilder();
        for (RhsValue value : rhs.getSymbols()) {
            result.append(formatRhsValue(value));
            result.append(" ");
        }
        return result.toString();
    }

    protected String formatRhsValue(RhsValue value) {
        if (value instanceof OptionalRhsValue)
            return formatOptionalValue((OptionalRhsValue) value);
        if (value instanceof Symbol)
            return formatSymbol((Symbol) value);
        else
            return value.toString();
    }

    protected String formatOptionalValue(OptionalRhsValue val) {
        StringBuilder result = new StringBuilder();
        result.append("[ ");
        for (Symbol sym : val.getSymbols()) {
            result.append(formatSymbol(sym));
            result.append(" ");
        }
        result.append("]");
        return result.toString();
    }

    protected String formatSymbol(Symbol sym) {
        if (sym instanceof Literal)
            return formatLiteral((Literal) sym);
        if (sym instanceof NonTerminal)
            return formatNonTerm((NonTerminal) sym);
        if (sym instanceof ListSymbol)
            return formatListSymbol((ListSymbol) sym);
        return sym.toString();
    }

    protected String formatLiteral(Literal sym) {
        return sym.toString();
    }

    protected String formatNonTerm(NonTerminal nt) {
        return nt.toString();
    }

    protected String formatListSymbol(ListSymbol sym) {
        return String.format("List<%s,%s>", formatSymbol(sym.getType()), formatSymbol(sym.getDelimiter()));
    }

    protected String formatLhs(ProductionLhs lhs) {
        return lhs.toString();
    }

}
