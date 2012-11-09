package grammarbuilder.parser;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
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
		parser.setRootClass(SimpleParsingClass.class);
		SimpleParsingClass body = parser.parse("a b");
		assertEquals("a", body.x);
		assertEquals("b", body.y);
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
		parser.setRootClass(SimpleParsingPrivatesClass.class);
		SimpleParsingPrivatesClass body = parser.parse("a 3");
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
		parser.setRootClass(SimpleParsingPrefixClass.class);
		SimpleParsingPrefixClass body = parser.parse("alpha beta abcdef");
		assertEquals("abcdef", body.x);
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
		parser.setRootClass(NestedParsingClass.class);
		NestedParsingClass body = parser.parse("alpha beta abcdef");
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
		parser.setRootClass(ListParsingClass.class);
		ListParsingClass body = parser.parse("alpha, beta, abcdef");
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
		parser.setRootClass(ResolverParsingClass.class);
		ResolverParsingClass body = parser.parse("a blah");
		assertTrue(body instanceof FirstResolver);
		assertEquals("blah", ((FirstResolver) body).x);
	}

	@Test
	public void resolverParsingSecond() {
		Parser parser = new Parser();
		parser.specialChar(' ', Behavior.Discard);
		parser.specialChar(',', Behavior.Keep);
		parser.setRootClass(ResolverParsingClass.class);
		ResolverParsingClass body = parser.parse("b halb");
		assertTrue(body instanceof SecondResolver);
		assertEquals("halb", ((SecondResolver) body).y);
	}

	// Deep Resolver Parsing

	@Parsable(resolvers = { FirstResolver.class, SecondResolver.class })
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
		parser.setRootClass(DeepResolverParsingClass.class);
		DeepResolverParsingClass body = parser.parse("x + y");
		assertTrue(body instanceof FirstDeepResolver);
		assertEquals("x", ((FirstDeepResolver) body).x);
		assertEquals("y", ((FirstDeepResolver) body).y);
	}

	@Test
	public void deepResolverParsingSecond() {
		Parser parser = new Parser();
		parser.specialChar(' ', Behavior.Discard);
		parser.setRootClass(DeepResolverParsingClass.class);
		DeepResolverParsingClass body = parser.parse("c - d");
		assertTrue(body instanceof SecondDeepResolver);
		assertEquals("c", ((SecondDeepResolver) body).x);
		assertEquals("d", ((SecondDeepResolver) body).y);
	}
}
