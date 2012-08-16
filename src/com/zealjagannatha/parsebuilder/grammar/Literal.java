package com.zealjagannatha.parsebuilder.grammar;

public class Literal implements Symbol {
	
	private String value;
	
	public Literal(String val) {
		value = val;
	}
	
	public String getValue() {
		return value;
	}
	
	@Override
	public String toString() {
		return "\"" + value + "\"";
	}

}
