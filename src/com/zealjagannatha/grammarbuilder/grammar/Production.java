package com.zealjagannatha.grammarbuilder.grammar;

//Copyright 2012 Zeal Jagannatha
//
//Licensed under the Apache License, Version 2.0 (the "License");
//you may not use this file except in compliance with the License.
//You may obtain a copy of the License at
//
//  http://www.apache.org/licenses/LICENSE-2.0
//
//Unless required by applicable law or agreed to in writing, software
//distributed under the License is distributed on an "AS IS" BASIS,
//WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
//See the License for the specific language governing permissions and
//limitations under the License.

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
