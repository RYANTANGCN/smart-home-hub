package com.ryan.project.smarthomehub.config;

import java.lang.annotation.*;

@Documented
@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface DeviceType {
    String value() default "";
}
