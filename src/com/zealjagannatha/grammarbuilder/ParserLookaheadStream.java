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

package com.zealjagannatha.grammarbuilder;

import java.io.IOException;
import java.io.StringReader;
import java.util.LinkedList;
import java.util.List;

public class ParserLookaheadStream extends ParserStream {
	
	public static final class LookaheadEndOfStream extends ParseException {
		private static final long serialVersionUID = -469894933807625603L;
	}

	public ParserLookaheadStream(StringReader reader) {
		super(reader);
	}
	
	public ParserLookaheadStream(String join, List<String> nextToken) {
		this(new StringReader(join));
		this.nextToken = nextToken;
	}

	@Override
	public ParserLookaheadStream clone() {
		try {
			return new ParserLookaheadStream(Util.join(allTokens()," "),nextToken);
		} catch (IOException e) {
			throw new ParseException("Could not clone lookahead stream",e);
		}
	}
	
	private List<String> nextToken = new LinkedList<String>();
	
	public void setNextToken(String next) {
		nextToken.add(next);
	}
	
	public List<String> getNextToken() {
		return nextToken;
	}
	
	@Override
	public void assertEqualsAndDiscard(String token, boolean ignoreCase) throws IOException {
		String next = nextToken();
		boolean fine = compare(token,next,ignoreCase);
		if(fine)
			return;
		else {
			if(next == null) {
				setNextToken(token);
				throw new LookaheadEndOfStream();
			}
			else
				throw new ParseException(String.format("Expected token '%s' but got '%s' instead",token,next));
		}
	}

	@Override
	public String assertMatchesAndReturn(String regex) throws IOException {
		String next = nextToken();
		if(next == null) {
			setNextToken(regex);
			throw new LookaheadEndOfStream();
		}
		if(!next.matches(regex))
			throw new RuntimeException(next+" does not match '"+regex+"'");
		return next;
	}

	@Override
	public String assertContainsAndReturn(List<String> list) throws IOException {
		String next = nextToken();
		if(next == null) {
			for(String x : list)
				setNextToken(x);
			throw new LookaheadEndOfStream();
		}
		if(!list.contains(next))
			throw new RuntimeException(next+" is not contained in "+Util.join(list,","));
		return next;
	}

	@Override
	public void assertEqualsAndDiscard(String[] prefix, boolean ignoreCase) throws IOException {
		for(String pre : prefix)
			assertEqualsAndDiscard(pre, ignoreCase);
	}

}
