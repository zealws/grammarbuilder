package grammarbuilder.parser;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import grammarbuilder.ClassAccessor;
import grammarbuilder.Parsable;
import grammarbuilder.Symbol;
import grammarbuilder.TokenStream.Behavior;
import grammarbuilder.parser.ParserTest.DeepResolverParsingClass.FirstDeepResolver;
import grammarbuilder.parser.ParserTest.DeepResolverParsingClass.SecondDeepResolver;
import grammarbuilder.parser.ParserTest.ResolverParsingClass.FirstResolver;
import grammarbuilder.parser.ParserTest.ResolverParsingClass.SecondResolver;

import java.util.List;

import org.junit.Test;

public class ParserTest {

    @SuppressWarnings({ "rawtypes", "unchecked" })
    @Test
    public void primitives() {
        assertTrue(Parser.isPrimitive(new ClassAccessor<String>(String.class)));
        assertTrue(Parser.isPrimitive(new ClassAccessor(int.class)));
        assertTrue(Parser.isPrimitive(new ClassAccessor(long.class)));
        assertTrue(Parser.isPrimitive(new ClassAccessor(double.class)));
        assertTrue(Parser.isPrimitive(new ClassAccessor<List>(List.class)));
    }

    // Simple Parsing

    @Parsable
    public static class SimpleParsingClass {
        @Symbol
        public String x;

        @Symbol
        public String y;
    }

    @Test
    public void simpleParsing() {
        Parser parser = new Parser();
        parser.specialChar(' ', Behavior.Discard);
        SimpleParsingClass body = parser.parse("a b", SimpleParsingClass.class);
        assertEquals("a", body.x);
        assertEquals("b", body.y);
    }

    @Test(expected = ParseException.class)
    public void failsIfEndOfStream() {
        Parser parser = new Parser();
        parser.specialChar(' ', Behavior.Discard);
        parser.parse("a", SimpleParsingClass.class);
    }

    @Test(expected = ParseException.class)
    public void unconsumedTokensError() {
        Parser parser = new Parser();
        parser.specialChar(' ', Behavior.Discard);
        parser.parse("a b c", SimpleParsingClass.class);
    }

    // Simple Parsing with Privates

    @Parsable
    public static class SimpleParsingPrivatesClass {
        @Symbol
        private String x;

        @Symbol
        private int y;
    }

    @Test
    public void simpleParsingPrivates() {
        Parser parser = new Parser();
        parser.specialChar(' ', Behavior.Discard);
        SimpleParsingPrivatesClass body = parser.parse("a 3", SimpleParsingPrivatesClass.class);
        assertEquals("a", body.x);
        assertEquals(3, body.y);
    }

    // Simple Parsing with Prefix

    @Parsable(prefix = "alpha")
    public static class SimpleParsingPrefixClass {
        @Symbol(prefix = "beta")
        private String x;
    }

    @Test
    public void simpleParsingPrefix() {
        Parser parser = new Parser();
        parser.specialChar(' ', Behavior.Discard);
        SimpleParsingPrefixClass body = parser.parse("alpha beta abcdef", SimpleParsingPrefixClass.class);
        assertEquals("abcdef", body.x);
    }

    // Ignore Case

    @Parsable(prefix = "alpha", ignoreCase = false)
    public static class IgnoresCaseClass {
        @Symbol(prefix = "beta", ignoreCase = true)
        private String x;
    }

    @Test
    public void ignoresCase() {
        Parser parser = new Parser();
        parser.specialChar(' ', Behavior.Discard);
        IgnoresCaseClass body = parser.parse("alpha BeTa abcdef", IgnoresCaseClass.class);
        assertEquals("abcdef", body.x);
    }

    @Test(expected = MismatchedTokenException.class)
    public void doesntIgnoreCase() {
        Parser parser = new Parser();
        parser.specialChar(' ', Behavior.Discard);
        parser.parse("AlPhA BeTa abcdef", IgnoresCaseClass.class);
    }

    // Nested Parsing

    @Parsable
    public static class NestedParsingClass {

        @Parsable
        public static class SubClass {
            @Symbol
            public String x;
            @Symbol
            public String y;
        }

        @Symbol
        public String x;

        @Symbol
        public SubClass sub;
    }

    @Test
    public void nestedParsing() {
        Parser parser = new Parser();
        parser.specialChar(' ', Behavior.Discard);
        NestedParsingClass body = parser.parse("alpha beta abcdef", NestedParsingClass.class);
        assertEquals("alpha", body.x);
        assertNotNull(body.sub);
        assertEquals("beta", body.sub.x);
        assertEquals("abcdef", body.sub.y);
    }

    // List Parsing

    @Parsable
    public static class ListParsingClass {
        @Symbol(subtype = String.class, padding = ",")
        public List<String> x;
    }

    @Test
    public void listParsing() {
        Parser parser = new Parser();
        parser.specialChar(' ', Behavior.Discard);
        parser.specialChar(',', Behavior.Keep);
        ListParsingClass body = parser.parse("alpha, beta, abcdef", ListParsingClass.class);
        assertEquals(3, body.x.size());
        assertEquals("alpha", body.x.get(0));
        assertEquals("beta", body.x.get(1));
        assertEquals("abcdef", body.x.get(2));
    }

    // Resolver Parsing

    @Parsable(resolvers = { FirstResolver.class, SecondResolver.class })
    public static abstract class ResolverParsingClass {
        @Parsable(prefix = "a")
        public static class FirstResolver extends ResolverParsingClass {
            @Symbol
            public String x;
        }

        @Parsable(prefix = "b")
        public static class SecondResolver extends ResolverParsingClass {
            @Symbol
            public String y;
        }
    }

    @Test
    public void resolverParsingFirst() {
        Parser parser = new Parser();
        parser.specialChar(' ', Behavior.Discard);
        parser.specialChar(',', Behavior.Keep);
        ResolverParsingClass body = parser.parse("a blah", ResolverParsingClass.class);
        assertTrue(body instanceof FirstResolver);
        assertEquals("blah", ((FirstResolver) body).x);
    }

    @Test
    public void resolverParsingSecond() {
        Parser parser = new Parser();
        parser.specialChar(' ', Behavior.Discard);
        parser.specialChar(',', Behavior.Keep);
        ResolverParsingClass body = parser.parse("b halb", ResolverParsingClass.class);
        assertTrue(body instanceof SecondResolver);
        assertEquals("halb", ((SecondResolver) body).y);
    }

    // Deep Resolver Parsing

    @Parsable(resolvers = { FirstDeepResolver.class, SecondDeepResolver.class })
    public static abstract class DeepResolverParsingClass {
        @Parsable
        public static class FirstDeepResolver extends DeepResolverParsingClass {
            @Symbol
            public String x;

            @Symbol(prefix = "+")
            public String y;
        }

        @Parsable
        public static class SecondDeepResolver extends DeepResolverParsingClass {
            @Symbol
            public String x;

            @Symbol(prefix = "-")
            public String y;
        }
    }

    @Test
    public void deepResolverParsingFirst() {
        Parser parser = new Parser();
        parser.specialChar(' ', Behavior.Discard);
        DeepResolverParsingClass body = parser.parse("x + y", DeepResolverParsingClass.class);
        assertTrue(body instanceof FirstDeepResolver);
        assertEquals("x", ((FirstDeepResolver) body).x);
        assertEquals("y", ((FirstDeepResolver) body).y);
    }

    @Test
    public void deepResolverParsingSecond() {
        Parser parser = new Parser();
        parser.specialChar(' ', Behavior.Discard);
        DeepResolverParsingClass body = parser.parse("c - d", DeepResolverParsingClass.class);
        assertTrue(body instanceof SecondDeepResolver);
        assertEquals("c", ((SecondDeepResolver) body).x);
        assertEquals("d", ((SecondDeepResolver) body).y);
    }

    @Test(expected = ParseException.class)
    public void deepResolverParsingFails() {
        Parser parser = new Parser();
        parser.specialChar(' ', Behavior.Discard);
        parser.parse("c / d", DeepResolverParsingClass.class);
    }

    // Deep Resolver Parsing

    @Parsable
    public static class OptionalParsing {
        @Symbol(optional = true)
        public int x;

        @Symbol
        public String y;
    }

    @Test
    public void optionalParsingFirst() {
        Parser parser = new Parser();
        parser.specialChar(' ', Behavior.Discard);
        OptionalParsing body = parser.parse("y", OptionalParsing.class);
        assertTrue(body instanceof OptionalParsing);
        assertEquals(0, ((OptionalParsing) body).x);
        assertEquals("y", ((OptionalParsing) body).y);
    }

    @Test
    public void optionalParsingSecond() {
        Parser parser = new Parser();
        parser.specialChar(' ', Behavior.Discard);
        OptionalParsing body = parser.parse("5 d", OptionalParsing.class);
        assertTrue(body instanceof OptionalParsing);
        assertEquals(5, ((OptionalParsing) body).x);
        assertEquals("d", ((OptionalParsing) body).y);
    }

    // List Subclass Parsing

    @Parsable
    public static class ListSubclassParsingClass {
        @Parsable
        public static class ListSubclassParsingSubclass {
            @Symbol
            public String x;
            @Symbol
            public String y;
        }

        @Symbol(subtype = ListSubclassParsingSubclass.class, padding = ";")
        public List<ListSubclassParsingSubclass> sub;

    }

    @Test
    public void listSubclassParsingOneItem() {
        Parser parser = new Parser();
        parser.specialChar(' ', Behavior.Discard);
        parser.specialChar(';', Behavior.Keep);
        ListSubclassParsingClass body = parser.parse("abra cadabra", ListSubclassParsingClass.class);
        assertTrue(body instanceof ListSubclassParsingClass);
        assertEquals(1, ((ListSubclassParsingClass) body).sub.size());
        assertEquals("abra", ((ListSubclassParsingClass) body).sub.get(0).x);
        assertEquals("cadabra", ((ListSubclassParsingClass) body).sub.get(0).y);
    }

    @Test
    public void listSubclassParsingThreeItems() {
        Parser parser = new Parser();
        parser.specialChar(' ', Behavior.Discard);
        parser.specialChar(';', Behavior.Keep);
        ListSubclassParsingClass body = parser.parse("ala cazam ; yo yo ; ding dong", ListSubclassParsingClass.class);
        assertTrue(body instanceof ListSubclassParsingClass);
        assertEquals(3, ((ListSubclassParsingClass) body).sub.size());
        assertEquals("ala", ((ListSubclassParsingClass) body).sub.get(0).x);
        assertEquals("cazam", ((ListSubclassParsingClass) body).sub.get(0).y);
        assertEquals("yo", ((ListSubclassParsingClass) body).sub.get(1).x);
        assertEquals("yo", ((ListSubclassParsingClass) body).sub.get(1).y);
        assertEquals("ding", ((ListSubclassParsingClass) body).sub.get(2).x);
        assertEquals("dong", ((ListSubclassParsingClass) body).sub.get(2).y);
    }
}
