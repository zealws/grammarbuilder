package grammarbuilder.parser;

import grammarbuilder.ClassAccessor;
import grammarbuilder.Parsable;
import grammarbuilder.Symbol;
import grammarbuilder.TokenStream;
import grammarbuilder.TokenStream.Behavior;

import java.util.Hashtable;
import java.util.LinkedList;
import java.util.List;

public class Parser {

	private static final boolean debug = true;

	// private static Logger logger = Logger.getLogger("Parser");

	private Hashtable<Character, Behavior> specialChars = new Hashtable<Character, Behavior>();

	public void specialChar(char specialChar, Behavior behavior) {
		specialChars.put(specialChar, behavior);
	}

	public <T> T parse(String body, Class<T> rootClass) {
		TokenStream stream = setupTokenStream(body);
		T result = (T) parseClass(new ClassAccessor<T>(rootClass), stream);
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

	private void discardTokens(TokenStream stream, boolean ignoreCase, String... tokens) {
		if (tokens.length > 0) {
			for (String token : tokens) {
				String next = getNextToken(stream);
				if (!compare(token, next, ignoreCase))
					throw new MismatchedTokenException(token, next);
			}
		}
	}

	private String getNextToken(TokenStream stream) {
		String next = stream.next();
		if (next == null)
			throw new ParseException("End of stream while reading token.");
		// System.out.println("Parsed token \"" + next + "\"");
		return next;
	}

	private boolean compare(String first, String second, boolean ignoreCase) {
		if (!ignoreCase)
			return first.equals(second);
		else
			return first.equalsIgnoreCase(second);
	}

	public <T> T parseClass(ClassAccessor<T> clazz, TokenStream stream) {
		debug("Entering type " + clazz.getName() + " \"" + stream.getBuffer() + "\"");
		if (!clazz.hasAnnotation(Parsable.class))
			throw new ParseException("Cannot parse non-buildable class " + clazz.getName());
		Parsable parsable = clazz.getAnnotation(Parsable.class);
		if (parsable.resolvers().length > 0)
			return parseResolvers(parsable, clazz, stream);
		T object = clazz.getInstance();

		discardTokens(stream, parsable.ignoreCase(), parsable.prefix());

		for (String fieldName : clazz.getFieldNames()) {
			debug("Entering field " + clazz.getName() + "." + fieldName + " \"" + stream.getBuffer() + "\"");
			Symbol sym = clazz.getFieldAnnotation(fieldName, Symbol.class);
			if (sym != null) {
				if (sym.optional()) {
					TokenStream backup = stream.clone();
					try {
						clazz.setField(object, fieldName, parseField(clazz.getField(fieldName), sym, stream));
					} catch (ParseException e) {
						stream.restoreFrom(backup);
					}
				} else
					clazz.setField(object, fieldName, parseField(clazz.getField(fieldName), sym, stream));
			}
		}

		discardTokens(stream, parsable.ignoreCase(), parsable.suffix());

		return object;
	}

	private <T, F> F parseField(ClassAccessor<F> field, Symbol sym, TokenStream stream) {
		discardTokens(stream, sym.ignoreCase(), sym.prefix());
		F value = null;
		if (isPrimitive(field)) {
			value = parsePrimitive(field, sym, stream);
		} else {
			value = parseClass(field, stream);
		}
		discardTokens(stream, sym.ignoreCase(), sym.suffix());
		return value;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private <T> T parseResolvers(Parsable parsable, ClassAccessor<T> clazz, TokenStream stream) {
		TokenStream backup = stream.clone();
		Exception lastException = null;
		for (Class<?> resolver : parsable.resolvers()) {
			ClassAccessor<?> resolverAccessor = new ClassAccessor(resolver);
			if (!resolverAccessor.subclassOf(clazz))
				throw new ParseException("Resolver " + resolverAccessor.getName() + " does not extend base class "
						+ clazz.getName());
			try {
				T result = parseClass((ClassAccessor<? extends T>) resolverAccessor, stream);
				debug("Abstract class " + clazz.getName() + " resolved to " + resolverAccessor.getName());
				return result;
			} catch (ParseException e) {
				lastException = e;
				debug(e);
				debug("Restoring stream from \"" + backup.getBuffer() + "\"");
				stream.restoreFrom(backup);
			}
		}
		throw new ParseException("No appropriate resolvers for " + clazz.getName(), lastException);
	}

	private void debug(Exception e) {
		if (debug)
			e.printStackTrace();
	}

	private void debug(String string) {
		if (debug)
			System.out.println(string);
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	private <T> T parsePrimitive(ClassAccessor<T> clazz, Symbol sym, TokenStream stream) {
		try {
			if (clazz.subclassOf(String.class)) {
				return (T) getNextToken(stream);
			} else if (clazz.subclassOf(int.class)) {
				return (T) Integer.valueOf(getNextToken(stream));
			} else if (clazz.subclassOf(long.class)) {
				return (T) Long.valueOf(getNextToken(stream));
			} else if (clazz.subclassOf(double.class)) {
				return (T) Double.valueOf(getNextToken(stream));
			} else if (clazz.subclassOf(List.class)) {
				if (sym == null)
					throw new ParseException("Cannot parse list class without @Symbol annotation.");
				return (T) parseList(sym.padding(), new ClassAccessor(sym.subtype()), stream, sym.ignoreCase());
			} else {
				throw new ParseException("Invalid type for primitive: " + clazz.getName());
			}
		} catch (ParseException e) {
			throw e;
		} catch (Exception e) {
			throw new ParseException("Could not parse primitive of type " + clazz.getName(), e);
		}
	}

	@SuppressWarnings("unchecked")
	private <T> List<T> parseList(String padding, ClassAccessor<T> subtype, TokenStream stream, boolean ignoreCase) {
		@SuppressWarnings("rawtypes")
		LinkedList results = new LinkedList();
		TokenStream backup = stream.clone();
		try {
			while (true) {
				Object nextItem;
				if (isPrimitive(subtype))
					nextItem = parsePrimitive(subtype, null, stream);
				else
					nextItem = parseClass(subtype, stream);
				results.add(nextItem);
				String next = stream.next();
				if (next == null || !compare(next, padding, ignoreCase)) {
					stream.putback(next);
					break;
				}
			}
		} catch (ParseException e) {
			stream.restoreFrom(backup);
		}
		return results;
	}

	static boolean isPrimitive(ClassAccessor<?> clazz) {
		if (clazz.subclassOf(String.class))
			return true;
		if (clazz.subclassOf(int.class))
			return true;
		if (clazz.subclassOf(long.class))
			return true;
		if (clazz.subclassOf(double.class))
			return true;
		if (clazz.subclassOf(List.class))
			return true;
		return false;
	}
}
