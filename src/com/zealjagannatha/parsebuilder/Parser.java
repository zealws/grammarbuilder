package com.zealjagannatha.parsebuilder;

import java.io.IOException;
import java.io.StringReader;
import java.util.LinkedList;

import com.zealjagannatha.parsebuilder.ParserLookaheadStream.LookaheadEndOfStream;
import com.zealjagannatha.parsebuilder.grammar.Grammar;
import com.zealjagannatha.parsebuilder.grammar.Production;
import com.zealjagannatha.parsebuilder.grammar.ProductionLhs;

public abstract class Parser<T> {
	@SuppressWarnings("unused")
	private static class HtmlScheme {
		public String terminalTag() { return "<span style='color:#D197D9;'>"; }
		public String terminalEnd() { return "</span>"; }
		public String specialTag() { return "<span style='color:#272822;'>"; }
		public String specialEnd() { return "</span>"; }
	}
	
	@SuppressWarnings("unchecked")
	public static <K> K parse(ParserStream stream, Class<K> toRead) throws IOException {
		BuildableClass clazz = new BuildableClass(toRead);
		return (K) clazz.read(stream);
	}
	
	public static <K> K parse(String toParse, Class<K> clazz) throws IOException {
		return parse(new ParserStream(new StringReader(toParse)), clazz);
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
			result.addProduction(new Production(new ProductionLhs(decl.getName()),decl.generateProductions()));
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
	
}
