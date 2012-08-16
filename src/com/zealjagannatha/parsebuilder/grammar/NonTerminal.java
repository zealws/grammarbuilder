package com.zealjagannatha.parsebuilder.grammar;

public class NonTerminal implements Symbol {
	
	private String name;
	
	public NonTerminal(String name) {
		this.name = name;
	}
	
	@Override
	public String toString() {
		return name;
	}

}
