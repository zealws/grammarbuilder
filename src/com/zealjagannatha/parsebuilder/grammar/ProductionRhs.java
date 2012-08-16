package com.zealjagannatha.parsebuilder.grammar;

import java.util.List;

import com.zealjagannatha.parsebuilder.Util;

public class ProductionRhs {
	
	public List<RhsValue> symbols;
	
	public ProductionRhs(List<RhsValue> symbols) {
		this.symbols = symbols;
	}
	
	@Override
	public String toString() {
		return Util.join(symbols, " ");
	}
	
	public List<RhsValue> getSymbols() {
		return symbols;
	}
}