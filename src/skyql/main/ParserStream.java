package skyql.main;

import java.io.IOException;
import java.io.Reader;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class ParserStream {
	
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
			char c = (char) p;
			
			if(p == -1) {
				break;
			}
			
			if(Character.isWhitespace(c) && token.length() > 0)
				break;
			
			if(isSpecialCharacter(c)) {
				if(token.length() != 0 ) {
					putBack(p);
				}
				else
					token.append(String.valueOf(c));
				break;
			}
			if(!Character.isWhitespace(c))
				token.append(c);
		}
		if(token.length() == 0)
			return null;
		return token.toString();
	}
	
	private void readTokens() throws IOException {
		while(true) {
			String token = readSingleToken();
			if(token != null)
				parsedTokens.offer(token);
			else {
				//System.out.println(allTokens());
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
	
	public static boolean isSpecialCharacter(Character c) {
		final List<Character> specChars
			= Arrays.asList(
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
	
	public boolean hasMoreTokens() {
		return !parsedTokens.isEmpty();
	}
	
	public List<String> allTokens() {
		return parsedTokens;
	}
	
	//////////////////////////////////////////////
	//////////////////////////////////////////////
	//////////////////////////////////////////////

	public void assertEqualsAndDiscard(String token, boolean ignoreCase) throws IOException {
		String next = nextToken();
		boolean fine;
		if(ignoreCase)
			fine = token.equalsIgnoreCase(next);
		else
			fine = token.equals(next);
		if(fine)
			return;
		else
			throw new RuntimeException("expected token '"+token+"' but got '"+next+"' instead");
	}

	public boolean compareAndDiscardIfEq(String token, boolean ignoreCase) throws IOException {
		String next = peekToken();
		if(next == null)
			return false;
		boolean eq;
		if(ignoreCase)
			eq = token.equalsIgnoreCase(next);
		else
			eq = token.equals(next);
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

}
