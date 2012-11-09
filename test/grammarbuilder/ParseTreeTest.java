package grammarbuilder;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import grammarbuilder.parser.ParseTree;

import org.junit.Test;

public class ParseTreeTest {

	@Parsable
	public static class TestClass {
		@Symbol
		public String x;

		@Symbol
		public TestClass2 other;
	}

	@Parsable
	public static class TestClass2 {
		@Symbol
		public String z;
	}

	@Test
	public void parsesTreeFromSample() {
		ParseTree parseTree = new ParseTree();
		parseTree.addType(TestClass.class);
		assertTrue(parseTree.containsType("TestClass"));
		assertTrue(parseTree.getType("TestClass").hasField("x"));
		assertTrue(parseTree.getType("TestClass").hasField("other"));
		assertTrue(parseTree.getType("TestClass").getField("x").isPrimitive());
		assertFalse(parseTree.getType("TestClass").getField("other").isPrimitive());
	}

	@Test
	public void parsesRecursively() {
		ParseTree parseTree = new ParseTree();
		parseTree.addType(TestClass.class);
		assertTrue(parseTree.containsType("TestClass"));
		assertTrue(parseTree.containsType("TestClass2"));
	}
}
