package com.zealjagannatha.parsebuilder.grammar;

public class ProductionLhs {
	
	private String value;
	
	public ProductionLhs(String value) {
		this.value = value;
	}
	
	public String getValue() {
		return value;
	}
	
	@Override
	public String toString() {
		return value;
	}
	
	@Override
	public boolean equals(Object other) {
		return other instanceof ProductionLhs && ((ProductionLhs) other).value.equals(value);
	}

}
