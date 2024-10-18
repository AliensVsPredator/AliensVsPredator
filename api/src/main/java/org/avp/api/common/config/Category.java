package org.avp.api.common.config;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Optional annotation to provide name override for categories.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Category {

    /**
     * Name override for the category in the file. By default, uses the class name.
     */
    String value() default "";
}
