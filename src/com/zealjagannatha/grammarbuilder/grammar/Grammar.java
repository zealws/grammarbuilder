/*
 * Copyright 2012 Zeal Jagannatha
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.zealjagannatha.grammarbuilder.grammar;

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
