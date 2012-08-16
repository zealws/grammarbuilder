package com.zealjagannatha.parsebuilder;

import java.io.IOException;
import java.io.Reader;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public abstract class BasicStream {
	
	private enum Mode { Normal , Escaped , Keep , No_Read };
	
	Mode mode = Mode.Normal;
	
	private char escapeChar;
	
	private Reader reader;
	
	private LinkedList<Integer> putBacks = new LinkedList<Integer>();
	
	protected BasicStream(LinkedList<String> parsedTokens) {
		this.parsedTokens = parsedTokens;
		this.mode = Mode.No_Read;
	}
	
	public BasicStream(Reader reader) {
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
	
	protected LinkedList<String> parsedTokens = new LinkedList<String>();
	
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
	
	protected void readTokens() throws IOException {
		if(mode != Mode.No_Read) {
			while(true) {
				String token = readSingleToken();
				if(token != null)
					parsedTokens.offer(token);
				else {
					return;
				}
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
	
	protected List<String> allTokens() throws IOException {
		readTokens();
		return parsedTokens;
	}

}
