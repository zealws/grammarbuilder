package com.zealjagannatha.parsebuilder.grammar;

public class GrammarFormatter {
	
	public String formatGrammar(Grammar g) {
		StringBuilder result = new StringBuilder();
		result.append("<div style=\"padding:10px;font-family:'Courier New',Courier,monospace;color:#272822;\">");
		for(Production p : g.getProductions()) {
			result.append(formatProduction(p));
			result.append("\n");
		}
		result.append("</div>");
		return result.toString();
	}

	protected String formatProduction(Production p) {
		StringBuilder result = new StringBuilder();
		result.append(formatLhs(p.getLhs()));
		result.append(" :=\n    ");
		for(ProductionRhs rhs : p.getRhss()) {
			result.append(formatRhs(rhs));
			result.append("\n    ");
		}
		return result.toString();
	}

	protected String formatRhs(ProductionRhs rhs) {
		StringBuilder result = new StringBuilder();
		for(RhsValue value : rhs.getSymbols()) {
			result.append(formatRhsValue(value));
			result.append(" ");
		}
		return result.toString();
	}

	protected String formatRhsValue(RhsValue value) {
		if(value instanceof OptionalRhsValue)
			return formatOptionalValue((OptionalRhsValue) value);
		if(value instanceof Symbol)
			return formatSymbol((Symbol) value);
		else
			return value.toString();
	}
	
	protected String formatOptionalValue(OptionalRhsValue val) {
		StringBuilder result = new StringBuilder();
		result.append("[ ");
		for(Symbol sym : val.getSymbols()) {
			result.append(formatSymbol(sym));
			result.append(" ");
		}
		result.append("]");
		return result.toString();
	}
	
	protected String formatSymbol(Symbol sym) {
		if(sym instanceof Literal)
			return formatLiteral((Literal) sym);
		if(sym instanceof NonTerminal)
			return formatNonTerm((NonTerminal) sym);
		if(sym instanceof ListSymbol)
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
		return String.format("List<%s,%s>",formatSymbol(sym.getType()),formatSymbol(sym.getDelimiter()));
	}

	protected String formatLhs(ProductionLhs lhs) {
		return lhs.toString();
	}

}
