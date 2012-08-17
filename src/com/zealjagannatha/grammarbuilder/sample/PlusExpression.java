package com.zealjagannatha.grammarbuilder.sample;

import com.zealjagannatha.grammarbuilder.Buildable;
import com.zealjagannatha.grammarbuilder.Token;

@Buildable(prefix="+")
public class PlusExpression extends Expression {

    @Token(position=0)
    private String left;

    @Token(position=1)
    private String right;

    public PlusExpression(String left, String right) {
        super(left, "+", right);
    }
}
