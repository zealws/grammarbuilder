package com.zealjagannatha.parsebuilder;

import java.io.IOException;
import java.io.Reader;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;


public class ParserStream {
	
	private enum Mode { Normal , Escaped , Keep };
	
	Mode mode = Mode.Normal;
	
	private char escapeChar;
	
	private Reader reader;
	
	private LinkedList<Integer> putBacks = new LinkedList<Integer>();
	
	public ParserStream(Reader reader) {
		this.reader = reader;
	}
	
	private int read() throws IOException {
		if(putBacks.isEmpty())
			return reader.read();
		else
			return putBacks.poll();
	}
	
	private void putBack(int i) {
		putBacks.offer(i);
	}
	
	private LinkedList<String> parsedTokens = new LinkedList<String>();
	
	private String readSingleToken() throws IOException {
		StringBuilder token = new StringBuilder();
		while(reader.ready() || parsedTokens.size() == 0) {
			int p = read();
			if(p == -1) {
				break;
			}
			char c = (char) p;
			
			if(mode == Mode.Escaped) {
				if(c == escapeChar) {
					mode = Mode.Keep;
					putBack(p);
					break;
				}
				else
					token.append(c);
			} else {
				if(token.length() == 0) {
					if(isSpecialCharacter(c)) {
						token.append(c);
						break;
					}
					else if(isModeCharacter(c) && mode == Mode.Keep) {
						token.append(c);
						mode = Mode.Normal;
						break;
					} else if(isModeCharacter(c) && mode == Mode.Normal) {
						token.append(c);
						mode = Mode.Escaped;
						escapeChar = c;
						break;
					}
					else if(!Character.isWhitespace(c))
						token.append(c);
				} else {
					if(Character.isWhitespace(c))
						break;
					if(isSpecialCharacter(c) || isModeCharacter(c)) {
						putBack(p);
						break;
					}
					else if(!Character.isWhitespace(c))
						token.append(c);
				}
			}
		}
		if(token.length() == 0)
			return null;
		//System.out.println(token + "\t:\t" + mode);
		return token.toString();
	}
	
	private void readTokens() throws IOException {
		while(true) {
			String token = readSingleToken();
			if(token != null)
				parsedTokens.offer(token);
			else {
				return;
			}
		}
	}
	
	public String nextToken() throws IOException {
		readTokens();
		return parsedTokens.poll();
	}
	
	public String peekToken() throws IOException {
		readTokens();
		return parsedTokens.peek();
	}
	
	public void putBackToken(String token) {
		parsedTokens.offer(token);
	}
	
	public static boolean isModeCharacter(Character c) {
		final List<Character> modeChars
			= Arrays.asList(
					'\'',
					'"');
		return modeChars.contains(c);
	}
	
	public static boolean isSpecialCharacter(Character c) {
		final List<Character> specChars
			= Arrays.asList(
					';',
					',',
					'*',
					'=',
					'(',
					')',
					'=',
					'<',
					'>',
					'!');
		return specChars.contains(c);
	}
	
	public boolean hasMoreTokens() throws IOException {
		readTokens();
		return !parsedTokens.isEmpty();
	}
	
	public List<String> allTokens() throws IOException {
		readTokens();
		return parsedTokens;
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
	
	private static boolean compare(String a, String b, boolean ignoreCase) {
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

}
