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

package com.zealjagannatha.grammarbuilder.sample.expression;

import com.zealjagannatha.grammarbuilder.Buildable;
import com.zealjagannatha.grammarbuilder.Token;

@Buildable(prefix="+")
public class AdditionExpression extends Expression {

    @Token(position=0)
    private String left;

    @Token(position=1)
    private String right;

    public AdditionExpression(String left, String right) {
        this.left = left;
        this.right = right;
    }
    
    public String toString() {
    	return left + " + " + right;
    }
}
