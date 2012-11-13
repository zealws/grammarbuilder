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
	}

	@Parsable
	public static class TestClass2 {
		@Symbol
		public String x;
	}

	@Test
	public void hasTypedFields() {
		ClassAccessor<TestClass> accessor = new ClassAccessor<TestClass>(TestClass.class);
		assertEquals(TestClass.class.getSimpleName(), accessor.getName());
		assertTrue(accessor.hasField("x"));
		assertTrue(accessor.hasField("y"));
		assertEquals("String", accessor.getFieldTypeName("x"));
		assertEquals("long", accessor.getFieldTypeName("y"));
	}

	@Test
	public void setFields() {
		TestClass mine = new TestClass();
		ClassAccessor<TestClass> accessor = new ClassAccessor<TestClass>(TestClass.class);
		assertEquals(null, mine.x);
		accessor.setField(mine, "x", "abcdef");
		assertEquals("abcdef", mine.x);
	}

	@Test
	public void getFields() {
		ClassAccessor<TestClass> accessor = new ClassAccessor<TestClass>(TestClass.class);
		assertEquals("String", accessor.getField("x", String.class).getName());
	}

	@Test(expected = RuntimeException.class)
	public void setFieldFailsWithoutInstance() {
		ClassAccessor<TestClass> accessor = new ClassAccessor<TestClass>(TestClass.class);
		accessor.setField(null, "x", "abcdef");
	}

	@Test(expected = RuntimeException.class)
	public void nonExistantFieldFails() {
		ClassAccessor<TestClass> accessor = new ClassAccessor<TestClass>(TestClass.class);
		assertFalse(accessor.hasField("z"));
		accessor.getRawField("z");
	}

	@Test(expected = RuntimeException.class)
	public void setFieldWithBadData() {
		ClassAccessor<TestClass> accessor = new ClassAccessor<TestClass>(TestClass.class);
		accessor.setField(accessor.getInstance(), "y", null);
	}

	@Test(expected = RuntimeException.class)
	public void setNonExistantField() {
		ClassAccessor<TestClass> accessor = new ClassAccessor<TestClass>(TestClass.class);
		accessor.setField(accessor.getInstance(), "z", null);
	}

	@Test(expected = RuntimeException.class)
	public void cannotInstantiateMemberClass() {
		@Parsable
		class TestClass3 {

		}
		ClassAccessor<TestClass3> accessor = new ClassAccessor<TestClass3>(TestClass3.class);
		accessor.getInstance();
	}

	@Test
	public void getInstanceSetField() {
		ClassAccessor<TestClass> accessor = new ClassAccessor<TestClass>(TestClass.class);
		TestClass instance = (TestClass) accessor.getInstance();
		accessor.setField(instance, "x", "abcdef");
		assertEquals("abcdef", instance.x);
	}

	@Test
	public void setThroughField() {
		ClassAccessor<TestClass> accessor = new ClassAccessor<TestClass>(TestClass.class);
		TestClass myInstance = (TestClass) accessor.getInstance();
		myInstance.inside = new TestClass2();
		ClassAccessor<TestClass2> field = accessor.getField("inside", TestClass2.class);
		assertEquals(null, myInstance.inside.x);
		field.setField(myInstance.inside, "x", "abcd");
		assertEquals("abcd", myInstance.inside.x);
	}

	@Test
	public void annotations() {
		ClassAccessor<TestClass> accessor = new ClassAccessor<TestClass>(TestClass.class);
		assertFalse(accessor.hasAnnotation(Parsable.class));
		assertTrue(accessor.hasAnnotatedField("x", Symbol.class));
		assertFalse(accessor.hasAnnotatedField("y", Symbol.class));
		assertTrue(accessor.getField("inside", TestClass2.class).hasAnnotation(Parsable.class));
	}

	@Test
	public void subSupClass() {
		ClassAccessor<Integer> integer = new ClassAccessor<Integer>(Integer.class);
		ClassAccessor<Number> number = new ClassAccessor<Number>(Number.class);
		ClassAccessor<Object> object = new ClassAccessor<Object>(Object.class);
		assertTrue(integer.subclassOf(number));
		assertTrue(number.superclassOf(integer));
		assertTrue(object.superclassOf(integer));
		assertTrue(object.superclassOf(number));
		assertTrue(integer.subclassOf(object));
		assertTrue(number.subclassOf(object));
	}
}
