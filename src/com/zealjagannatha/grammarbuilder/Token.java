/*
 * Copyright 2012 Zeal Jagannatha
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
