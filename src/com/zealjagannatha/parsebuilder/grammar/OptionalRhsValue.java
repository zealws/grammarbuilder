package com.zealjagannatha.parsebuilder.grammar;

import java.util.List;

import com.zealjagannatha.parsebuilder.Util;

public class OptionalRhsValue implements RhsValue {
	
	private List<Symbol> values;
	
	public OptionalRhsValue(List<Symbol> values) {
		this.values = values;
	}
	
	@Override
	public String toString() {
		return String.format("[ %s ] ",Util.join(values," "));
	}

	public List<Symbol> getSymbols() {
		return values;
	}

}
