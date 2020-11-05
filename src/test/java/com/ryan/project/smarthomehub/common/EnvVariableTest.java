package com.ryan.project.smarthomehub.common;

import org.junit.jupiter.api.Test;

/**
 * @Descritption
 * @Date 2020/11/2
 * @Author tangqianli
 */
public class EnvVariableTest {

    @Test
    public void test(){
        System.out.println(System.getenv("${user.home}"));
        System.out.println(System.getProperty("${user.home}"));
    }
}
