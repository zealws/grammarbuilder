package skyql.main;

import java.io.StringReader;
import java.util.LinkedList;


public abstract class Parser<T> {
	
	private static class StyleScheme {
		public String tab() { return "    "; }
		public String nl() { return "\n"; }
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
	
	@SuppressWarnings("unused")
	private static class HtmlScheme extends StyleScheme {
		private static final String lhsColor = "red";
		private static final String nonTerminalColor = "orange";
		private static final String terminalColor = "blue";
		private static final String literalColor = "green";
		private static final String specialColor = "black";
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
	public static <K> K read(CreatorStream stream, Class<K> toRead) throws Exception {
		BuildableClass clazz = new BuildableClass(toRead);
		return (K) clazz.read(stream);
	}
	
	public static <K> K read(String toParse, Class<K> clazz) throws Exception {
		return read(new CreatorStream(new StringReader(toParse)), clazz);
	}
	
	public static <K> String generateGrammar(Class<K> toRead) {
		BuildableClass clazz = new BuildableClass(toRead);
		LinkedList<AnnotatedDeclaration> uniqueNonterminals = new LinkedList<AnnotatedDeclaration>();
		LinkedList<AnnotatedDeclaration> toGenerate = new LinkedList<AnnotatedDeclaration>();
		toGenerate.add(clazz);
		while(!toGenerate.isEmpty()) {
			AnnotatedDeclaration decl = toGenerate.poll();
			uniqueNonterminals.add(decl);
			for(AnnotatedDeclaration sub : decl.getSubdeclarations()) {
				if(!uniqueNonterminals.contains(sub) && !toGenerate.contains(sub)) {
					toGenerate.add(sub);
				}
			}
		}
		StringBuilder result = new StringBuilder();
		result.append(scheme.topGrammar());
		for(AnnotatedDeclaration decl : uniqueNonterminals) {
			result.append(decl.generateGrammar());
			result.append("\n");
		}
		result.append(scheme.bottomGrammar());
		return result.toString();
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
	
	static String lhs(String val) {
		return scheme.lhsTag() + val + scheme.lhsEnd();
	}
	
}
