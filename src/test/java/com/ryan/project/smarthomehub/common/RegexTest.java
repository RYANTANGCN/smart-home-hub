package com.ryan.project.smarthomehub.common;

import org.junit.jupiter.api.Test;

import java.util.regex.Pattern;

/**
 * @Descritption
 * @Date 2020/11/2
 * @Author tangqianli
 */
public class RegexTest {

    @Test
    public void test() {

        Pattern pattern = Pattern.compile("/home/fullfillment/*");
//        Matcher matcher = pattern.matcher("/home/fullfillment/create");
        System.out.println(pattern.matcher("/home/fullfillment/create").matches());
        System.out.println(pattern.matcher("/home/create").matches());
    }
}
