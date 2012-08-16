package com.zealjagannatha.parsebuilder.grammar;

public class HtmlGrammarFormatter extends GrammarFormatter { 

	@Override
	public String formatGrammar(Grammar g) {
		StringBuilder result = new StringBuilder();
		for(Production p : g.getProductions()) {
			result.append(formatProduction(p));
			result.append("\n");
		}
		return result.toString();
	}

	@Override
	protected String formatProduction(Production p) {
		StringBuilder result = new StringBuilder();
		result.append(formatLhs(p.getLhs()));
		result.append(" :=<br/>&nbsp;&nbsp;&nbsp;&nbsp;");
		for(ProductionRhs rhs : p.getRhss()) {
			result.append(formatRhs(rhs));
			result.append("<br/>");
		}
		return result.toString();
	}

	@Override
	protected String formatRhs(ProductionRhs rhs) {
		StringBuilder result = new StringBuilder();
		for(RhsValue value : rhs.getSymbols()) {
			result.append(formatRhsValue(value));
			result.append("");
		}
		return result.toString();
	}

	@Override
	protected String formatRhsValue(RhsValue value) {
		if(value instanceof OptionalRhsValue)
			return formatOptionalValue((OptionalRhsValue) value);
		if(value instanceof Symbol)
			return formatSymbol((Symbol) value);
		else
			return value.toString();
	}
	
	@Override
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
	
	@Override
	protected String formatSymbol(Symbol sym) {
		if(sym instanceof Literal)
			return formatLiteral((Literal) sym);
		if(sym instanceof NonTerminal)
			return formatNonTerm((NonTerminal) sym);
		if(sym instanceof ListSymbol)
			return formatListSymbol((ListSymbol) sym);
		return sym.toString();
	}
	
	@Override
	protected String formatLiteral(Literal sym) {
		return sym.toString();
	}

	@Override
	protected String formatNonTerm(NonTerminal nt) {
		return nt.toString();
	}
	
	@Override
	protected String formatListSymbol(ListSymbol sym) {
		return String.format("List<%s,%s>",formatSymbol(sym.getType()),formatSymbol(sym.getDelimiter()));
	}

	@Override
	protected String formatLhs(ProductionLhs lhs) {
		return String.format("<span style=\"color:#FF4466;\">%s</span>",lhs.toString());
	}

}
