package grammarbuilder.example;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import grammarbuilder.TokenStream.Behavior;
import grammarbuilder.parser.Parser;

import org.junit.Test;

public class ProgramTest {

	public Program parse(String body) {
		Parser parser = new Parser();
		parser.specialChar(';', Behavior.Keep);
		parser.specialChar('=', Behavior.Keep);
		parser.specialChar('+', Behavior.Keep);
		parser.specialChar(' ', Behavior.Discard);
		parser.specialChar('\n', Behavior.Discard);
		parser.specialChar('\t', Behavior.Discard);
		return parser.parse(body, Program.class);
	}

	@Test
	public void simpleProgram() {
		Program prog = parse("var x");
		assertEquals(1, prog.getStatements().size());
		assertTrue(prog.getStatements().get(0) instanceof VariableDeclaration);
		assertEquals("x", ((VariableDeclaration) prog.getStatements().get(0)).getName());
	}

	@Test
	public void complexProgram() {
		Program prog = parse("var x; var y; x = input; y = input; output y + z");
		assertEquals(5, prog.getStatements().size());
		assertTrue(prog.getStatements().get(0) instanceof VariableDeclaration);
		assertTrue(prog.getStatements().get(1) instanceof VariableDeclaration);
		assertTrue(prog.getStatements().get(2) instanceof Assignment);
		assertTrue(prog.getStatements().get(3) instanceof Assignment);
		assertTrue(prog.getStatements().get(4) instanceof OutputStatement);
	}
}
