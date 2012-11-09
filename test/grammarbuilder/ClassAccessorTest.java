package grammarbuilder;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class ClassAccessorTest {

	public static class TestClass {
		@Symbol
		public String x;
		public long y;
		@Symbol
		public TestClass2 inside;

		public String getX() {
			return x;
		}

		public void setX(String x) {
			this.x = x;
		}
	}

	@Parsable
	public static class TestClass2 {
		@Symbol
		public String x;

	}

	@Test
	public void hasTypedFields() {
		ClassAccessor accessor = new ClassAccessor(TestClass.class);
		assertTrue(accessor.hasField("x"));
		assertTrue(accessor.hasField("y"));
		assertEquals("String", accessor.getFieldTypeName("x"));
		assertEquals("long", accessor.getFieldTypeName("y"));
	}

	@Test
	public void setFields() {
		TestClass mine = new TestClass();
		ClassAccessor accessor = new ClassAccessor(mine);
		assertEquals(null, mine.getX());
		accessor.setField("x", "abcdef");
		assertEquals("abcdef", mine.getX());
	}

	@Test(expected = RuntimeException.class)
	public void setFieldFails() {
		ClassAccessor accessor = new ClassAccessor(TestClass.class);
		accessor.setField("x", "abcdef");
	}

	@Test
	public void getInstanceSetField() {
		ClassAccessor accessor = new ClassAccessor(TestClass.class);
		accessor.getInstance();
		accessor.setField("x", "abcdef");
	}

	@Test
	public void setThroughField() {
		ClassAccessor accessor = new ClassAccessor(TestClass.class);
		TestClass myInstance = (TestClass) accessor.getInstance();
		myInstance.inside = new TestClass2();
		ClassAccessor field = accessor.getField("inside");
		assertEquals(null, myInstance.inside.x);
		field.setField("x", "abcd");
		assertEquals("abcd", myInstance.inside.x);
	}

	@Test
	public void annotations() {
		AnnotatedClassAccessor accessor = new AnnotatedClassAccessor(TestClass.class);
		assertFalse(accessor.isBuildable());
		assertTrue(accessor.isSymbol("x"));
		assertFalse(accessor.isSymbol("y"));
		assertTrue(accessor.getField("inside").isBuildable());
	}
}
