package grammarbuilder.parser;

@SuppressWarnings("serial")
public class MismatchedTokenException extends ParseException {
	public MismatchedTokenException(String expected, String actual) {
		super(String.format("Unexpected token. Expected: '%s', Actual: '%s'", expected, actual));
	}
}
