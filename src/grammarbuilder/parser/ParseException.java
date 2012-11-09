package grammarbuilder.parser;

@SuppressWarnings("serial")
public class ParseException extends RuntimeException {
	protected ParseException(String message) {
		super(message);
	}

	protected ParseException(String message, Throwable t) {
		super(message, t);
	}
}
