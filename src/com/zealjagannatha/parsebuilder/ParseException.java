package com.zealjagannatha.parsebuilder;

public class ParseException extends RuntimeException {
	
	private static final long serialVersionUID = 4633167383696119202L;

	public ParseException() {
		super();
	}
	
	public ParseException(String message) {
		super(message);
	}
	
	public ParseException(String message, Throwable cause) {
		super(message,cause);
	}

}
