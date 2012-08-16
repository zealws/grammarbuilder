package com.zealjagannatha.parsebuilder;

import static org.junit.Assert.assertEquals;

import java.io.IOException;

import junit.framework.AssertionFailedError;

import org.junit.Test;

import com.zealjagannatha.parsebuilder.BuildableClass.Buildable;
import com.zealjagannatha.parsebuilder.TokenField.Token;

public class ParserTest {
	
	@Test
	public void nonBuildableException() throws IOException {
		final class TestGrammar { }
		try {
			Parser.parse("a",TestGrammar.class);
		} catch (ParseException e) {
			assertEquals("Attempt to create BuildableClass from non-buildable object: TestGrammar", e.getMessage());
			return;
		} 
		throw new AssertionFailedError();
	}
	
	@Test
	public void privateConstructorException() throws IOException {
		@Buildable
		final class TestGrammar {
			@Token(position=0)
			private String val1;
			@Token(position=1)
			private String val2;
			private TestGrammar(String val1, String val2) {
				this.val1 = val1;
				this.val2 = val2;
			}
		}
		try {
			Parser.parse("a b",TestGrammar.class);
		} catch (ParseException e) {
			System.out.println(e.getMessage());
			assertEquals("Constructor TestGrammar(String,String) must exist.", e.getMessage());
			return;
		} 
		throw new AssertionFailedError();
	}
}
