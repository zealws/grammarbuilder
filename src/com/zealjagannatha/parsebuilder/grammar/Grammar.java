package com.zealjagannatha.parsebuilder.grammar;

import java.util.LinkedList;
import java.util.List;

public class Grammar {
	
	private LinkedList<Production> productions = new LinkedList<Production>();
	
	public void addProduction(Production val) {
		if(!productions.contains(val))
			productions.add(val);
		else {
			Production use = productions.get(productions.indexOf(val));
			use.getRhss().addAll(val.getRhss());
		}
	}
	
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		for(Production p : productions) {
			builder.append(p.toString());
			builder.append("\n\n");
		}
		return builder.toString();
	}
	
	public List<Production> getProductions() {
		return productions;
	}

}
