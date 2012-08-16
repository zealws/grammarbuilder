package com.zealjagannatha.parsebuilder;

import java.io.IOException;
import java.io.Reader;
import java.util.List;

public class ParserStream extends BasicStream {
	
	public ParserStream(Reader reader) {
		super(reader);
	}
	
	//////////////////////////////////////////////
	//////////////////////////////////////////////
	//////////////////////////////////////////////

	public void assertEqualsAndDiscard(String token, boolean ignoreCase) throws IOException {
		String next = nextToken();
		boolean fine = compare(token,next,ignoreCase);
		if(fine)
			return;
		else
			throw new ParseException(String.format("Expected token '%s' but got %s instead",token,(next==null?"end of stream":"'"+next+"'")));
	}

	public boolean compareAndDiscardIfEq(String token, boolean ignoreCase) throws IOException {
		String next = peekToken();
		if(next == null)
			return false;
		boolean eq = compare(token,next, ignoreCase);
		if(eq)
			nextToken();
		return eq;
	}

	public String assertMatchesAndReturn(String regex) throws IOException {
		String next = nextToken();
		if(next == null || !next.matches(regex))
			throw new RuntimeException(next+" does not match '"+regex+"'");
		return next;
	}

	public String assertContainsAndReturn(List<String> list) throws IOException {
		String next = nextToken();
		if(next == null || !list.contains(next))
			throw new RuntimeException(next+" is not contained in "+Util.join(list,","));
		return next;
	}
	
	protected static boolean compare(String a, String b, boolean ignoreCase) {
		if(ignoreCase)
			return a.equalsIgnoreCase(b);
		else
			return a.equals(b);
	}
	
	public boolean compareAndDiscardIfEq(String[] tokens, boolean ignoreCase) throws IOException {
		if(!compareAndPassIfEq(tokens, ignoreCase))
			return false;
		for(int i = 0; i < tokens.length; i++) {
			assertEqualsAndDiscard(tokens[i], ignoreCase);
		}
		return true;
	}
	
	public boolean compareAndPassIfEq(String[] tokens, boolean ignoreCase) throws IOException {
		readTokens();
		try {
			for(int i = 0; i < tokens.length; i++) {
				if(!compare(parsedTokens.get(i),tokens[i],ignoreCase))
					return false;
			}
		} catch (IndexOutOfBoundsException e) {
			return false;
		}
		return true;
	}

	public void assertEqualsAndDiscard(String[] prefix, boolean ignoreCase) throws IOException {
		for(String pre : prefix)
			assertEqualsAndDiscard(pre, ignoreCase);
	}

	public boolean compareMatchesAndPass(String matches) throws IOException {
		String next = peekToken();
		return next.matches(matches);
	}

	public boolean compareContainsAndIgnore(String[] either, boolean ignoreCase) throws IOException {
		String next = peekToken();
		for(String s : either) {
			if (compare(s, next, ignoreCase))
				return true;
		}
		return false;
	}

}
