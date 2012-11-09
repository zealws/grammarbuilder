package grammarbuilder.parser;

@SuppressWarnings("serial")
public class EndOfStreamException extends ParseException {

	protected EndOfStreamException() {
		super("Unexpected end of file while parsing.");
	}

}
