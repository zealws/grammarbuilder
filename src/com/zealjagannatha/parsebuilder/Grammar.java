package com.zealjagannatha.parsebuilder;

import java.util.Arrays;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.List;

public class Grammar {
	
	public static class ProductionRhs {
		public ProductionRhs(String... symbols) {
			this.symbols = Arrays.asList(symbols);
		}
		public ProductionRhs(List<String> symbols) {
			this.symbols = symbols;
		}
		public List<String> symbols;
		public String toString() {
			return Util.join(symbols, " ");
		}
	}

	private LinkedList<String> nonTerminals = new LinkedList<String>();
	private Hashtable<String,List<ProductionRhs>> productions = new Hashtable<String,List<ProductionRhs>>();
	
	public Grammar() {
		
	}
	
	public void addNonTerminal(String name) {
		if(!nonTerminals.contains(name))
			nonTerminals.add(name);
	}
	
	public List<ProductionRhs> getProductions(String name) {
		return productions.get(name);
	}
	
	public String toString() {
		StringBuilder builder = new StringBuilder();
		for(String lhs : nonTerminals) {
			builder.append(lhs);
			builder.append(" :=\n");
			builder.append("    ");
			boolean first = true;
			if(productions.get(lhs) != null) {
				for(ProductionRhs rhs : productions.get(lhs)) {
					if(!first) {
						builder.append(" |\n    ");
					}
					first = false;
					builder.append(rhs.toString());
				}
			}
			builder.append("\n");
		}
		return builder.toString();
	}

	public void addProductions(String name, List<ProductionRhs> prods) {
		addNonTerminal(name);
		productions.put(name, prods);
	}

}
