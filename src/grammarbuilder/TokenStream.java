package grammarbuilder;

import java.util.Deque;
import java.util.Hashtable;
import java.util.LinkedList;

public class TokenStream {

	private Deque<Character> buffer = new LinkedList<Character>();
	private String putbackToken;
	private Character putback;
	private Character escapeChar = null;

	private String currentToken;

	private Hashtable<Character, Behavior> specialChars = new Hashtable<Character, Behavior>();

	public static enum Behavior {
		// Discard the character, continuing the current token.
		// Designates nothing.
		Ignore,
		// Discard the character, ending the current token.
		// Designates nothing.
		Discard,
		// Keep the character.
		// Designates a single token.
		Keep,
		// Group the character with identical following characters.
		// Designates a single token.
		Group,
		// Terminate the current token, using this character as the last token
		// in the character.
		// Designates the last character in a token.
		Terminate,
		// End the current token and use this character as the first
		// character in the next token.
		// Designates the first character in a token.
		Threshold,
		// End the current token. Use this character as the current escape
		// character and full next token. Future tokens are read in literally,
		// ignoring all special characters until the character that caused the
		// escape is given again.
		// Designates a single token and the start of escaping.
		Escape
	}

	public void addSpecialCharacter(Character c, Behavior b) {
		specialChars.put(c, b);
	}

	public void feed(String s) {
		for (byte b : s.getBytes()) {
			feed((char) b);
		}
	}

	public void feed(char c) {
		buffer.addLast(c);
	}

	public void feed(String... strings) {
		feed(' ', strings);
	}

	public void feed(char padding, String... strings) {
		for (String s : strings) {
			feed(s);
			feed(padding);
		}
	}

	public String next() {
		StringBuilder token = readNextToken();
		if (token.length() == 0)
			currentToken = null;
		else
			currentToken = token.toString();
		return currentToken;
	}

	private StringBuilder readNextToken() {
		if (putbackToken != null) {
			String temp = putbackToken;
			putbackToken = null;
			return new StringBuilder().append(temp);
		}
		StringBuilder token = new StringBuilder();
		boolean finished = false;
		Character groupChar = null;
		while (!finished) {
			Character c = readNextCharacter();
			if (c == null)
				finished = true;
			else {
				if (groupChar != null) {
					if (c != groupChar) {
						putback(c);
						return token;
					} else {
						token.append(c);
					}
				} else if (escapeChar != null) {
					if (c.equals(escapeChar)) {
						if (token.length() != 0) {
							putback(c);
							return token;
						} else {
							escapeChar = null;
							return token.append(c);
						}
					} else
						token.append(c);
				} else if (specialChars.containsKey(c)) {
					switch (specialChars.get(c)) {
					case Keep:
						if (token.length() == 0) {
							return token.append(c);
						} else {
							putback(c);
							return token;
						}
					case Discard:
						if (token.length() != 0) {
							return token;
						}
						break;
					case Group:
						if (token.length() == 0) {
							groupChar = c;
							token.append(c);
						} else {
							putback(c);
							return token;
						}
						break;
					case Terminate:
						token.append(c);
						return token;
					case Threshold:
						if (token.length() == 0)
							token.append(c);
						else {
							putback(c);
							return token;
						}
						break;
					case Ignore:
						break;
					case Escape:
						if (token.length() == 0) {
							escapeChar = c;
							return token.append(c);
						} else {
							putback(c);
							return token;
						}
					}
				} else {
					token.append(c);
				}
			}
		}
		return token;
	}

	private Character readNextCharacter() {
		if (putback == null && buffer.isEmpty())
			return null;
		else if (putback == null)
			return buffer.removeFirst();
		else {
			Character temp = putback;
			putback = null;
			return temp;
		}
	}

	private void putback(Character c) {
		putback = c;
	}

	public void putback(String token) {
		if (putbackToken == null)
			putbackToken = token;
		else
			throw new RuntimeException("Conflict with existing put-back token: " + putbackToken);
	}

	@Override
	public TokenStream clone() {
		TokenStream other = new TokenStream();
		for (Character c : buffer) {
			other.buffer.addLast(c);
		}
		other.currentToken = currentToken;
		other.putback = putback;
		other.putbackToken = putbackToken;
		other.specialChars = specialChars;
		return other;
	}

	public void restoreFrom(TokenStream other) {
		buffer = other.buffer;
		currentToken = other.currentToken;
		putback = other.putback;
		putbackToken = other.putbackToken;
	}

	public String getCurrentToken() {
		return currentToken;
	}

	public String getBuffer() {
		StringBuilder b = new StringBuilder();
		for (Character c : buffer) {
			if (c == '\n')
				b.append("\\n");
			else if (c == '\t')
				b.append("\\t");
			else
				b.append(c);
		}
		return b.toString();
	}
}
