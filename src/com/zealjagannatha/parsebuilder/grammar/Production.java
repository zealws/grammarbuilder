package com.zealjagannatha.parsebuilder.grammar;

import java.util.List;

public class Production {
	
	private ProductionLhs lhs;
	private List<ProductionRhs> rhss;
	
	public Production(ProductionLhs lhs, List<ProductionRhs> rhss) {
		this.lhs = lhs;
		this.rhss = rhss;
	}
	
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append(lhs);
		builder.append(" :=\n    ");
		boolean first = true;
		for(ProductionRhs rhs : rhss) {
			if(!first) {
				builder.append(" |\n    ");
			}
			first = false;
			builder.append(rhs.toString());
		}
		return builder.toString();
	}

	public List<ProductionRhs> getRhss() {
		return rhss;
	}

	public ProductionLhs getLhs() {
		return lhs;
	}
	
	@Override
	public boolean equals(Object other) {
		return other instanceof Production && ((Production)other).lhs.equals(lhs);
	}

}
