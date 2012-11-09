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

package com.zealjagannatha.grammarbuilder.old;

import java.util.List;

public class Util {
	
	public static <K> String join(List<K> list, String insert) {
		boolean already = false;
		StringBuilder result = new StringBuilder();
		for(K cond : list) {
			if(already)
				result.append(insert);
			result.append(cond.toString());
			already = true;
		}
		return result.toString();
	}
	
	public static <K> String join(K[] list, String insert) {
		boolean already = false;
		StringBuilder result = new StringBuilder();
		for(K cond : list) {
			if(already)
				result.append(insert);
			result.append(cond.toString());
			already = true;
		}
		return result.toString();
	}

}
