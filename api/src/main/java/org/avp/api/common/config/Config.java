package org.avp.api.common.config;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Optional annotation to provide extra data for configs.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface Config {

    /**
     * Name override for the config in the file. By default, uses the field name.
     */
    String value() default "";

    String comment() default "";

    float min() default Float.MIN_NORMAL;

    float max() default Float.MAX_VALUE;
}
