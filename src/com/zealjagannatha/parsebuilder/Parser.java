package com.zealjagannatha.parsebuilder;

import java.io.IOException;
import java.io.StringReader;
import java.util.LinkedList;

import com.zealjagannatha.parsebuilder.ParserLookaheadStream.LookaheadEndOfStream;

public abstract class Parser<T> {
	
	private static class StyleScheme {
		public String tab() { return "    "; }
		public String nl() { return "\n"; }
		public String space() { return " "; }
		public String productionSym() { return ":"; }
		public String lhsTag() { return ""; }
		public String lhsEnd() { return ""; }
		public String topGrammar() { return ""; }
		public String bottomGrammar() { return ""; }
		public String nonTerminalTag() { return ""; }
		public String nonTerminalEnd() { return ""; }
		public String terminalTag() { return ""; }
		public String terminalEnd() { return ""; }
		public String literalTag() { return "\""; }
		public String literalEnd() { return "\""; }
		public String specialTag() { return ""; }
		public String specialEnd() { return ""; }
	}
	
	private static class HtmlScheme extends StyleScheme {
		private static final String lhsColor = "#FF4466";
		private static final String nonTerminalColor = "orange";
		private static final String terminalColor = "#D197D9";
		private static final String literalColor = "#A6E22E";
		private static final String specialColor = "#272822";
		@Override
		public String topGrammar() {
			return "<div style=\"padding:10px;font-family:'Courier New',Courier,monospace;color:#272822;\">";
		}
		@Override
		public String bottomGrammar() { return "</div>"; }
		@Override
		public String tab() { return "&nbsp;&nbsp;&nbsp;&nbsp;"; }
		@Override
		public String nl() { return "<br/>"; }
		@Override
		public String lhsTag() { return "<span style='color:"+lhsColor+";'>"; }
		@Override
		public String lhsEnd() { return "</span>"; }
		@Override
		public String nonTerminalTag() { return "<span style='color:"+nonTerminalColor+";'>"; }
		@Override
		public String nonTerminalEnd() { return "</span>"; }
		@Override
		public String terminalTag() { return "<span style='color:"+terminalColor+";'>"; }
		@Override
		public String terminalEnd() { return "</span>"; }
		@Override
		public String literalTag() { return "<i><span style='color:"+literalColor+";'>"; }
		@Override
		public String literalEnd() { return "</span></i>"; }
		@Override
		public String specialTag() { return "<span style='color:"+specialColor+";'>"; }
		@Override
		public String specialEnd() { return "</span>"; }
	}
	
	private static final StyleScheme scheme = new HtmlScheme();
	
	@SuppressWarnings("unchecked")
	public static <K> K parse(ParserStream stream, Class<K> toRead) throws IOException {
		BuildableClass clazz = new BuildableClass(toRead);
		return (K) clazz.read(stream);
	}
	
	public static <K> K parse(String toParse, Class<K> clazz) throws IOException {
		return parse(new ParserStream(new StringReader(toParse)), clazz);
	}
	
	public static <K> String generateGrammar(Class<K> toRead) {
		BuildableClass clazz = new BuildableClass(toRead);
		LinkedList<BuildableClass> uniqueNonterminals = new LinkedList<BuildableClass>();
		LinkedList<BuildableClass> toGenerate = new LinkedList<BuildableClass>();
		toGenerate.add(clazz);
		while(!toGenerate.isEmpty()) {
			BuildableClass decl = toGenerate.poll();
			uniqueNonterminals.add(decl);
			for(BuildableClass sub : decl.getSubdeclarations()) {
				if(!uniqueNonterminals.contains(sub) && !toGenerate.contains(sub)) {
					toGenerate.add(sub);
				}
			}
		}
		StringBuilder result = new StringBuilder();
		result.append(scheme.topGrammar());
		for(BuildableClass decl : uniqueNonterminals) {
			result.append(decl.generateGrammar());
			result.append("\n");
		}
		result.append(scheme.bottomGrammar());
		return result.toString();
	}
	
	public static <K> Grammar generateActualGrammar(Class<K> toRead) {
		BuildableClass clazz = new BuildableClass(toRead);
		LinkedList<BuildableClass> uniqueNonterminals = new LinkedList<BuildableClass>();
		LinkedList<BuildableClass> toGenerate = new LinkedList<BuildableClass>();
		toGenerate.add(clazz);
		while(!toGenerate.isEmpty()) {
			BuildableClass decl = toGenerate.poll();
			uniqueNonterminals.add(decl);
			for(BuildableClass sub : decl.getSubdeclarations()) {
				if(!uniqueNonterminals.contains(sub) && !toGenerate.contains(sub)) {
					toGenerate.add(sub);
				}
			}
		}
		Grammar result = new Grammar();
		for(BuildableClass decl : uniqueNonterminals) {
			result.addNonTerminal(decl.getName());
			result.addProductions(decl.getName(),decl.generateProductions());
		}
		return result;
	}
	
	public static <K> String getNextToken(String toParse, Class<K> start) throws IOException {
		BuildableClass clazz = new BuildableClass(start);
		ParserLookaheadStream stream = new ParserLookaheadStream(new StringReader(toParse));
		try {
			clazz.nextToken(stream);
		} catch (LookaheadEndOfStream e) {
			// do nothing
		}
		return (stream.getNextToken().size() == 0 ? "No tokens left." : Util.join(stream.getNextToken(),", "));
	}
	
	static String nt(String val) {
		return scheme.nonTerminalTag() + val + scheme.nonTerminalEnd();
	}
	
	static String t(String val) {
		return scheme.terminalTag() + val + scheme.terminalEnd();
	}
	
	static String sc(String val) {
		return scheme.specialTag() + val + scheme.specialEnd();
	}
	
	static String lt(String val) {
		return scheme.literalTag() + val + scheme.literalEnd();
	}

	static String nl() {
		return scheme.nl();
	}
	
	static String tab() {
		return scheme.tab();
	}
	
	static String space() {
		return scheme.space();
	}
	
	static String lhs(String val) {
		return scheme.lhsTag() + val + scheme.lhsEnd();
	}
	
	static String productionSym() {
		return scheme.productionSym();
	}
	
}
