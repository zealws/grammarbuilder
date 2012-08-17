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
@Target(ElementType.TYPE)
public @interface Buildable {
    String[] prefix() default {};
    String[] suffix() default {};
    boolean ignoreCase() default true;
    Class<?>[] resolvers() default {};
}
