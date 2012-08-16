package com.zealjagannatha.parsebuilder.grammar.formatter;

import com.zealjagannatha.parsebuilder.grammar.Grammar;
import com.zealjagannatha.parsebuilder.grammar.ListSymbol;
import com.zealjagannatha.parsebuilder.grammar.Literal;
import com.zealjagannatha.parsebuilder.grammar.NonTerminal;
import com.zealjagannatha.parsebuilder.grammar.Production;
import com.zealjagannatha.parsebuilder.grammar.ProductionLhs;
import com.zealjagannatha.parsebuilder.grammar.ProductionRhs;

public class HtmlFormatter extends GrammarFormatter {

	@Override
	public String formatGrammar(Grammar g) {
		StringBuilder result = new StringBuilder();
		result.append("<div style=\"padding:10px;font-family:'Courier New',Courier,monospace;color:#272822;\">");
		for(Production p : g.getProductions()) {
			result.append(formatProduction(p));
			result.append("<br/>");
		}
		result.append("</div>");
		return result.toString();
	}

	@Override
	protected String formatProduction(Production p) {
		StringBuilder result = new StringBuilder();
		result.append(formatLhs(p.getLhs()));
		result.append(" :=<br/>&nbsp;&nbsp;&nbsp;&nbsp;");
		boolean first = true;
		for(ProductionRhs rhs : p.getRhss()) {
			if(!first)
				result.append(" |<br/>&nbsp;&nbsp;&nbsp;&nbsp;");
			first = false;
			result.append(formatRhs(rhs));
		}
		result.append("<br/>");
		return result.toString();
	}
	
	@Override
	protected String formatLiteral(Literal sym) {
		return String.format("\"<span style='color:#A6E22E;'>%s</span>\"",sym.getValue());
	}

	@Override
	protected String formatNonTerm(NonTerminal nt) {
		return String.format("<span style='color:orange;'>%s</span>",nt.toString());
	}
	
	@Override
	protected String formatListSymbol(ListSymbol sym) {
		return String.format("<span style='color:#D197D9;'>List&lt;</span>%s,%s<span style='color:#D197D9;'>&gt;</span>",formatSymbol(sym.getType()),formatSymbol(sym.getDelimiter()));
	}

	@Override
	protected String formatLhs(ProductionLhs lhs) {
		return String.format("<span style=\"color:#FF4466;\">%s</span>",lhs.toString());
	}

}
