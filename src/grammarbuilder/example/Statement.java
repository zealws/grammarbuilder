package grammarbuilder.example;

import grammarbuilder.Parsable;

@Parsable(resolvers = { VariableDeclaration.class, Assignment.class, OutputStatement.class })
public abstract class Statement {

}
