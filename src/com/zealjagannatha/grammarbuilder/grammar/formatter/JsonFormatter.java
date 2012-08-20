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
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public class JsonFormatter implements GrammarFormatter<JSONObject> {


    @Override
    public JSONObject formatGrammar(Grammar g) {
        JSONObject nonterminals = new JSONObject();
        for(Production p : g.getProductions())
            nonterminals.put(p.getLhs().toString(),formatProduction(p));
        JSONObject grammar = new JSONObject();
        grammar.put("nonterminals",nonterminals);
        return grammar;
    }

    protected JSONArray formatProduction(Production p) {
        JSONArray rhss = new JSONArray();
        for(ProductionRhs rhs : p.getRhss())
            rhss.add(formatProductionRhs(rhs));
        return rhss;
    }

    protected JSONArray formatProductionRhs(ProductionRhs rhs) {
        JSONArray arr = new JSONArray();
        for(RhsValue val : rhs.getSymbols())
            arr.add(formatRhsValue(val));
        return arr;
    }

    protected JSONObject formatRhsValue(RhsValue value) {
        if (value instanceof OptionalRhsValue)
            return formatOptionalValue((OptionalRhsValue) value);
        if (value instanceof Symbol)
            return formatSymbol((Symbol) value);
        return null; // TODO rework this
    }

    protected JSONObject formatOptionalValue(OptionalRhsValue val) {
        JSONArray values = new JSONArray();
        for(Symbol sym : val.getSymbols())
            values.add(formatSymbol(sym));
        JSONObject optional = new JSONObject();
        optional.put("optional",values);
        return optional;
    }

    protected JSONObject formatSymbol(Symbol sym) {
        if (sym instanceof Literal)
            return formatLiteral((Literal) sym);
        if (sym instanceof NonTerminal)
            return formatNonTerm((NonTerminal) sym);
        if (sym instanceof ListSymbol)
            return formatListSymbol((ListSymbol) sym);
        return null; // TODO rework this too

    }

    protected JSONObject formatLiteral(Literal sym) {
        JSONObject lit = new JSONObject();
        lit.put("literal",sym.getValue());
        return lit;
    }

    protected JSONObject formatNonTerm(NonTerminal nt) {
        JSONObject nonterm = new JSONObject();
        nonterm.put("nonterminal",nt.getName());
        return nonterm;
    }

    protected JSONObject formatListSymbol(ListSymbol sym) {
        JSONObject sub = new JSONObject();
        sub.put("subtype",sym.getType().toString());
        sub.put("delimiter",sym.getDelimiter());
        JSONObject list = new JSONObject();
        list.put("list",sub);
        return list;
    }
}
