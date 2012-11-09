package grammarbuilder;

import java.util.Deque;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.List;

public class TokenStream {

	private Deque<Character> charBuffer = new LinkedList<Character>();
	private String putbackToken;
	private Character putback;

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
		Threshold
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
		charBuffer.addLast(c);
	}

	public void feed(String... strings) {
		for (String s : strings) {
			feed(s);
			feed(' ');
		}
	}

	public void feed(char padding, String... strings) {
		for (String s : strings) {
			feed(s);
			feed(padding);
		}
	}

	public String getCurrentToken() {
		return currentToken;
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
		boolean finished = charBuffer.isEmpty();
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
					case Ignore:
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
					}
				} else {
					token.append(c);
				}
			}
		}
		return token;
	}

	private Character readNextCharacter() {
		if (putback == null && charBuffer.isEmpty())
			return null;
		else if (putback == null)
			return charBuffer.removeFirst();
		else {
			Character temp = putback;
			putback = null;
			return temp;
		}
	}

	private void putback(Character c) {
		if (putback == null)
			putback = c;
		else
			throw new RuntimeException("Conflict with existing put-back character.");
	}

	public void putback(String token) {
		if (putbackToken == null)
			putbackToken = token;
		else
			throw new RuntimeException("Conflict with existing put-back token: " + putbackToken);
	}

	public List<String> getAllTokens() {
		List<String> tokens = new LinkedList<String>();
		String nextToken = next();
		while (nextToken != null) {
			tokens.add(nextToken);
			nextToken = next();
		}
		return tokens;
	}

	@Override
	public TokenStream clone() {
		TokenStream other = new TokenStream();
		for (Character c : charBuffer) {
			other.charBuffer.addLast(c);
		}
		other.currentToken = currentToken;
		other.putback = putback;
		other.putbackToken = putbackToken;
		other.specialChars = specialChars;
		return other;
	}

	public void restoreFrom(TokenStream other) {
		charBuffer = other.charBuffer;
		currentToken = other.currentToken;
		putback = other.putback;
		putbackToken = other.putbackToken;
	}

	@Override
	public String toString() {
		StringBuilder b = new StringBuilder();
		if (putback != null)
			b.append(putback);
		for (Character c : charBuffer)
			b.append(c);
		return String.format("current='%s'\nputbackToken='%s'\nraw='%s'", currentToken, putbackToken, b.toString());
	}
}
