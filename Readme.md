# Grammarbuilder
A parser/deserializer using java annotations

## About

Grammarbuilder is a java deserializer that allows you to specify your own syntax.
Essentially, it allows you to specify elements of a grammar using java annotations.

Generally when specifying a language, one would write a grammar for that language, and then create a number of classes representing the syntax tree of that language. Since the structure of the syntax tree often replicates the structure of the grammar, there is duplicate information about the structure of the language. Grammarbuilder attempts to simplify this process by eliminating the need to create both a grammar and an abstract syntax tree. Instead, users need only specify the abstract syntax tree and annotate it, showing Grammarbuilder exactly how to parse it.

## Using Grammarbuilder

Using grammarbuilder is as simple as adding java annotations to your [abstract syntax tree](http://en.wikipedia.org/wiki/Abstract_syntax_tree) objects. 

For example, suppose you had a grammar for a simple programming language:

    program :=
        statement |
        statement ';' statement

    statement :=
        variable_declaration |
        assignment |
        output_statement

    variable_declaration :=
        'var' name

    assignment :=
        name '=' expression

    output_statement :=
        'output' expression

    expression :=
        name '+' name |
        'input'

A abstract syntax tree cooresponding to this grammar has 8 classes:

* [Program](https://github.com/zfjagann/grammarbuilder/blob/master/src/grammarbuilder/example/Program.java)
* [Statement](https://github.com/zfjagann/grammarbuilder/blob/master/src/grammarbuilder/example/Statement.java)
* [VariableDeclaration](https://github.com/zfjagann/grammarbuilder/blob/master/src/grammarbuilder/example/VariableDeclaration.java)
* [Assignment](https://github.com/zfjagann/grammarbuilder/blob/master/src/grammarbuilder/example/Assignment.java)
* [OutputStatement](https://github.com/zfjagann/grammarbuilder/blob/master/src/grammarbuilder/example/OutputStatement.java)
* [Expression](https://github.com/zfjagann/grammarbuilder/blob/master/src/grammarbuilder/example/Expression.java)
* [AdditionExpression](https://github.com/zfjagann/grammarbuilder/blob/master/src/grammarbuilder/example/AdditionExpression.java)
* [InputExpression](https://github.com/zfjagann/grammarbuilder/blob/master/src/grammarbuilder/example/InputExpression.java)

You may notice that 6 of the classes coorespond with the non-terminals in the grammar. It is generally the case that Grammarbuilder classes coorespond with non-terminals, although combinations of terminal expressions (such as `name '+' name | 'input'`) require creating classes to model each expression.

## Specifying symbols

Grammarbuilder uses the fields of classes to specify symbols, lists of symbols, or other expressions that must be parsed.

The `@Symbol` annotation provides an interface to do this.

Using a `@Symbol` annotation on a field notifies Grammarbuilder that it should attempt to parse this field. If the type of the field is annotated with `@Parsable` or a primitive type, then it is parsed and the field is set using reflection.

### Primitive Types

The following primitive types are implicitly `@Parsable`:

* String
* int
* double
* long
* List (See [Lists](#lists))

### Lists

To specify a list of sub-expressions, the `@Symbol` annotation can be used with the attribute `subtype`, which allows Grammarbuilder to know the subtype of the list. The `padding` attribute can be used to specify the token to be parsed between items in the list.

For example, to parse the following grammar:

    item_list :=
        string |
        string '$' string

the following class can be used:

    @Parsable
    public class ItemList {

        @Symbol(subtype = String.class, padding = "$")
        private List<String> statements;

    }

## Using abstract classes

Grammarbuilder uses abstract classes to model non-terminals in a grammar that have many complex right-hand sides. This can be done by using the `resolvers` attribute of the `@Parsable` annotation, which allows classes to be speficied that should be used in place of the current class.

The exact behavior is:

When parsing a class B with resolvers C, D, and E, the body of B is ignored. Instead, grammarbuilder attempts to parse C. If a `ParseException` is thrown while parsing C, it restores the stream, and attempts to parse D, and so on. The first resolver to be parsed correctly is returned as the result of parsing B. If none of the resolvers are parsed, a `ParseException` is thrown.

Note that classes C, D, and E must extends B in order to be parsed correctly.

An example in the above grammar is the non-terminal `statement`:

    @Parsable(resolvers = { VariableDeclaration.class, Assignment.class, OutputStatement.class })
    public abstract class Statement {

    }