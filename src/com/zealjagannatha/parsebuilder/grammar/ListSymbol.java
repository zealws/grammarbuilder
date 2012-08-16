package com.zealjagannatha.parsebuilder.grammar;

public class ListSymbol implements Symbol {
	
	private Symbol internalType;
	private Literal delimiter;
	
	public ListSymbol(Symbol internalType, Literal delimiter) {
		this.internalType = internalType;
		this.delimiter = delimiter;
	}
	
	@Override
	public String toString() {
		return String.format("List<%s,%s>",internalType,delimiter);
	}

	public Symbol getType() {
		return internalType;
	}
	
	public Literal getDelimiter() {
		return delimiter;
	}

}
