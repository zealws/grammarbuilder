package grammarbuilder.parser;

import grammarbuilder.ClassAccessor;
import grammarbuilder.TokenStream;
import grammarbuilder.TokenStream.Behavior;

import java.util.Hashtable;
import java.util.LinkedList;
import java.util.List;

public class Parser {

	private Hashtable<Character, Behavior> specialChars = new Hashtable<Character, Behavior>();
	private String rootClass;
	private ParseTree tree = new ParseTree();

	public void specialChar(char specialChar, Behavior behavior) {
		specialChars.put(specialChar, behavior);
	}

	public <T> void setRootClass(Class<?> rootClass) {
		this.rootClass = rootClass.getSimpleName();
		tree.addType(rootClass);
	}

	@SuppressWarnings("unchecked")
	public <T> T parse(String body) {
		TokenStream stream = setupTokenStream(body);
		T result = (T) parseClass(tree.getType(rootClass), stream);
		String next = stream.next();
		if (next != null)
			throw new ParseException("There were unconsumed tokens left in the stream: " + next);
		return result;
	}

	private TokenStream setupTokenStream(String body) {
		TokenStream stream = new TokenStream();
		for (Character c : specialChars.keySet()) {
			stream.addSpecialCharacter(c, specialChars.get(c));
		}
		stream.feed(body);
		return stream;
	}

	private void discardTokens(TokenStream stream, boolean matchCase, String... tokens) {
		if (tokens.length > 0) {
			for (String token : tokens) {
				String next = getNextToken(stream);
				if (!compare(token, next, matchCase))
					throw new MismatchedTokenException(token, next);
			}
		}
	}

	private String getNextToken(TokenStream stream) {
		String next = stream.next();
		if (next == null)
			throw new EndOfStreamException();
		else
			return next;
	}

	private boolean compare(String first, String second, boolean matchCase) {
		if (matchCase)
			return first.equals(second);
		else
			return first.equalsIgnoreCase(second);
	}

	public Object parseClass(TypeObject current, TokenStream stream) {
		if (current.getResolvers() != null && current.getResolvers().length > 0)
			return parseResolvers(current, stream);
		ClassAccessor accessor = current.getType();
		accessor.getInstance();

		discardTokens(stream, current.isIgnoreCase(), current.getPrefix());

		for (String fieldName : current.getFields()) {
			FieldObject field = current.getField(fieldName);
			discardTokens(stream, field.isIgnoreCase(), field.getPrefix());
			Object value = null;
			if (field.isPrimitive()) {
				value = parsePrimitive(field, stream);
			} else {
				value = parseClass(tree.getType(field.getType().getName()), stream);
			}
			discardTokens(stream, field.isIgnoreCase(), field.getSuffix());
			accessor.setField(fieldName, value);
		}

		discardTokens(stream, current.isIgnoreCase(), current.getSuffix());

		return accessor.getInstance();
	}

	private Object parseResolvers(TypeObject current, TokenStream stream) {
		TokenStream backup = stream.clone();
		for (Class<?> resolver : current.getResolvers()) {
			try {
				if (tree.getType(resolver.getSimpleName()) == null)
					throw new ParseException("No type found for class " + resolver.getSimpleName());
				return parseClass(tree.getType(resolver.getSimpleName()), stream);
			} catch (ParseException e) {
				e.printStackTrace();
				stream.restoreFrom(backup);
			}
		}
		throw new ParseException("No appropriate resolvers for " + current.getName() + " on " + backup.getAllTokens());
	}

	private Object parsePrimitive(FieldObject field, TokenStream stream) {
		if (field.getType().typeEquals(String.class)) {
			return getNextToken(stream);
		} else if (field.getType().typeEquals(int.class)) {
			return Integer.valueOf(getNextToken(stream));
		} else if (field.getType().typeEquals(long.class)) {
			return Long.valueOf(getNextToken(stream));
		} else if (field.getType().typeEquals(double.class)) {
			return Double.valueOf(getNextToken(stream));
		} else if (field.getType().typeEquals(List.class)) {
			return parseList(field.getPadding(), tree.getType(field.getListSubtype().getName()), stream, field.isIgnoreCase());
		} else {
			throw new RuntimeException("Invalid type for primitive: " + field.getType().getName());
		}
	}

	@SuppressWarnings("unchecked")
	private Object parseList(String padding, TypeObject type, TokenStream stream, boolean matchCase) {
		@SuppressWarnings("rawtypes")
		List results = new LinkedList();
		while (true) {
			results.add(getNextToken(stream));
			String next = stream.next();
			if (next == null || !compare(next, padding, matchCase)) {
				stream.putback(next);
				break;
			}
		}
		return results;
	}
}
