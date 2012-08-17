package com.zealjagannatha.grammarbuilder;

//   Copyright 2012 Zeal Jagannatha
//
//   Licensed under the Apache License, Version 2.0 (the "License");
//   you may not use this file except in compliance with the License.
//   You may obtain a copy of the License at
//
//       http://www.apache.org/licenses/LICENSE-2.0
//
//   Unless required by applicable law or agreed to in writing, software
//   distributed under the License is distributed on an "AS IS" BASIS,
//   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
//   See the License for the specific language governing permissions and
//   limitations under the License.

import java.io.IOException;
import java.io.Reader;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public abstract class ParserTokenizer {

    public static List<Character> specialChars = new LinkedList<Character>();
    public static List<Character> modeChars = new LinkedList<Character>(Arrays.asList('\'','"'));

    private static final List<Character> defaultSpecialChars
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
	
	private enum Mode { Normal , Escaped , Keep , No_Read };
	
	Mode mode = Mode.Normal;
	
	private char escapeChar;
	
	private Reader reader;
	
	private LinkedList<Integer> putBacks = new LinkedList<Integer>();
	
	protected ParserTokenizer(LinkedList<String> parsedTokens) {
		this.parsedTokens = parsedTokens;
		this.mode = Mode.No_Read;
	}
	
	public ParserTokenizer(Reader reader) {
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
		return specialChars.contains(c);
	}

    public static void addSpecialChar(Character c) {
        specialChars.add(c);
    }

    public static void useDefaultChars() {
        specialChars.addAll(defaultSpecialChars);
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
