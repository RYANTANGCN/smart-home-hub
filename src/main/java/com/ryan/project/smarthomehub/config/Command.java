package com.ryan.project.smarthomehub.config;


import java.lang.annotation.*;

/**
 * @author ryan
 */
@Documented
@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Command {
    String value() default "";
}
