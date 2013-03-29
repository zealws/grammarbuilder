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
    public void ignore() {
        TokenStream stream = new TokenStream();
        stream.feed("feeding the stream some tokens");
        stream.addSpecialCharacter('e', Behavior.Ignore);
        stream.addSpecialCharacter(' ', Behavior.Discard);

        assertTokensEqual(stream, "fding", "th", "stram", "som", "tokns");
    }

    @Test
    public void grouping() {
        TokenStream stream = new TokenStream();
        stream.feed("feeeaeeding the streeam");
        stream.addSpecialCharacter(' ', Behavior.Discard);
        stream.addSpecialCharacter('e', Behavior.Group);
        stream.addSpecialCharacter('a', Behavior.Group);

        assertTokensEqual(stream, "f", "eeeaee", "ding", "th", "e", "str", "eea", "m");
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
                stream.next();
                assertEquals(token, stream.getCurrentToken());
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
        assertTokensEqual(stream, "this", "is", "some", "sample", "text");
        assertTokensEqual(other, "this", "is", "some", "sample", "text");
        assertTokensEqual(stream, "to", "test", "clone", "for", "token", "stream");
        assertTokensEqual(other, "to", "test", "clone", "for", "token", "stream");
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

    @Test(expected = RuntimeException.class)
    public void putsBack1Token() {
        TokenStream stream = new TokenStream();
        stream.putback("blah");
        stream.putback("blah2");
    }

    @Test
    public void keepsToken() {
        TokenStream stream = new TokenStream();
        stream.addSpecialCharacter(')', Behavior.Keep);
        stream.feed("f)");
        assertTokensEqual(stream, "f", ")");
    }

    @Test
    public void ignoresFirstWhitespace() {
        TokenStream stream = new TokenStream();
        stream.addSpecialCharacter(' ', Behavior.Discard);
        stream.feed(" f");
        assertTokensEqual(stream, "f");
    }

    @Test
    public void escapesProperly() {
        TokenStream stream = new TokenStream();
        stream.addSpecialCharacter('\'', Behavior.Escape);
        stream.addSpecialCharacter(' ', Behavior.Discard);
        stream.feed("abc'd e'f");
        assertTokensEqual(stream, "abc", "'", "d e", "'", "f");
    }

    @Test
    public void parsesLessEqual() {
        TokenStream stream = new TokenStream();
        stream.addSpecialCharacter('>', Behavior.Group);
        stream.addSpecialCharacter('=', Behavior.Group);
        stream.feed("abc>=def");
        assertTokensEqual(stream, "abc", ">=", "def");
    }
}
