package grammarbuilder.example;

import grammarbuilder.Parsable;

@Parsable(resolvers = { InputExpression.class, AdditionExpression.class })
public abstract class Expression {

}
