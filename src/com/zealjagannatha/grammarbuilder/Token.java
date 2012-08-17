package com.zealjagannatha.grammarbuilder;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
* Created with IntelliJ IDEA.
* User: zeal
* Date: 8/17/12
* Time: 2:22 AM
* To change this template use File | Settings | File Templates.
*/
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface Token {
    int position() default 0;
    String[] prefix() default {};
    String[] suffix() default {};
    String[] either() default {};
    boolean optional() default false;
    boolean ignoreCase() default true;
    Class<?> subtype() default Object.class;
    String padding() default ",";
    boolean greedy() default false;
    String matches() default "";
}
