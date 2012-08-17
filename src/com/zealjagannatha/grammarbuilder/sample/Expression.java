package com.zealjagannatha.grammarbuilder.sample;

import com.zealjagannatha.grammarbuilder.Buildable;
import com.zealjagannatha.grammarbuilder.Parser;
import com.zealjagannatha.grammarbuilder.ParserTokenizer;
import com.zealjagannatha.grammarbuilder.Token;

import java.io.IOException;
import java.nio.file.attribute.BasicFileAttributes;

@Buildable(resolvers={PlusExpression.class,MinusExpression.class})
public class Expression {

    private String left;
    private String operation;
    private String right;

    public Expression(String left, String operation, String right) {
        this.left = left;
        this.operation = operation;
        this.right = right;
    }

    public String toString() {
        return left + " " + operation + " " + right;
    }

    public static void main(String[] args) throws IOException {
        ParserTokenizer.useDefaultChars();
        ParserTokenizer.addSpecialChar('+');
        ParserTokenizer.addSpecialChar('-');
        System.out.println(Parser.parse("- x y",Expression.class));
    }

}
