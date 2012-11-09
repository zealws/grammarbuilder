package grammarbuilder;

import static org.junit.Assert.assertEquals;
import grammarbuilder.TokenStream.Behavior;

import org.junit.Test;

public class TokenStreamTest {

	@Test
	public void nullEmptyStream() {
		TokenStream stream = new TokenStream();
		assertTokensEqual(stream);
	}

	@Test
	public void getsSingleCharacter() {
		TokenStream stream = new TokenStream();
		stream.feed("a");
		assertEquals("a", stream.next());
	}

	@Test
	public void ignoresWhitespace() {
		TokenStream stream = new TokenStream();
		stream.addSpecialCharacter(' ', Behavior.Discard);
		stream.feed(" a");
		assertEquals("a", stream.next());
	}

	@Test
	public void getsMultipleTokens() {
		TokenStream stream = new TokenStream();
		stream.feed("feeding the\tstream", "some\ntokens");
		stream.addSpecialCharacter(' ', Behavior.Discard);
		stream.addSpecialCharacter('\n', Behavior.Discard);
		stream.addSpecialCharacter('\t', Behavior.Discard);

		assertEquals("feeding", stream.next());
		assertEquals("the", stream.next());
		assertEquals("stream", stream.next());
		assertEquals("some", stream.next());
		assertEquals("tokens", stream.next());
	}

	@Test
	public void grouping() {
		TokenStream stream = new TokenStream();
		stream.feed("feeeeeeding the streeam");
		stream.addSpecialCharacter(' ', Behavior.Discard);
		stream.addSpecialCharacter('e', Behavior.Group);

		assertEquals("f", stream.next());
		assertEquals("eeeeee", stream.next());
		assertEquals("ding", stream.next());
		assertEquals("th", stream.next());
		assertEquals("e", stream.next());
		assertEquals("str", stream.next());
		assertEquals("ee", stream.next());
		assertEquals("am", stream.next());
	}

	@Test
	public void terminate() {
		TokenStream stream = new TokenStream();
		stream.feed("feeding the stream");
		stream.addSpecialCharacter(' ', Behavior.Terminate);

		assertEquals("feeding ", stream.next());
		assertEquals("the ", stream.next());
		assertEquals("stream", stream.next());
	}

	@Test
	public void threshold() {
		TokenStream stream = new TokenStream();
		stream.feed("feedingthestream");
		stream.addSpecialCharacter('t', Behavior.Threshold);

		assertEquals("feeding", stream.next());
		assertEquals("thes", stream.next());
		assertEquals("tream", stream.next());
	}

	public void assertTokensEqual(TokenStream stream, String... tokens) {
		if (tokens.length == 0)
			assertEquals(null, stream.next());
		else {
			for (String token : tokens) {
				assertEquals(token, stream.next());
			}
		}
	}

	@Test
	public void putback() {
		TokenStream stream = new TokenStream();
		stream.feed("some text");
		stream.addSpecialCharacter(' ', Behavior.Discard);

		assertEquals("some", stream.next());
		stream.putback("gnarly");
		assertEquals("gnarly", stream.next());
		assertEquals("text", stream.next());
	}

	@Test
	public void cloneWorks() {
		TokenStream stream = new TokenStream();
		stream.feed("this is some sample text to test clone for token stream");
		stream.addSpecialCharacter(' ', Behavior.Discard);
		TokenStream other = stream.clone();
		assertEquals("this", stream.next());
		assertEquals("is", stream.next());
		assertEquals("some", stream.next());
		assertEquals("sample", stream.next());
		assertEquals("text", stream.next());

		assertEquals("this", other.next());
		assertEquals("is", other.next());
		assertEquals("some", other.next());
		assertEquals("sample", other.next());
		assertEquals("text", other.next());

		assertEquals("to", stream.next());
		assertEquals("test", stream.next());
		assertEquals("clone", stream.next());
		assertEquals("for", stream.next());
		assertEquals("token", stream.next());
		assertEquals("stream", stream.next());

		assertEquals("to", other.next());
		assertEquals("test", other.next());
		assertEquals("clone", other.next());
		assertEquals("for", other.next());
		assertEquals("token", other.next());
		assertEquals("stream", other.next());
	}

	@Test
	public void backupAndRestore() {
		TokenStream stream = new TokenStream();
		stream.feed("this is some text");
		stream.addSpecialCharacter(' ', Behavior.Discard);

		assertEquals("this", stream.next());
		TokenStream backup = stream.clone();
		assertEquals("is", stream.next());
		assertEquals("some", stream.next());
		assertEquals("text", stream.next());
		stream.restoreFrom(backup);
		assertEquals("is", stream.next());
		assertEquals("some", stream.next());
		assertEquals("text", stream.next());
	}
}
