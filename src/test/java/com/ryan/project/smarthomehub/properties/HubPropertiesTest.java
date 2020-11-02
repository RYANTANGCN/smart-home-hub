package com.ryan.project.smarthomehub.properties;

import com.ryan.project.smarthomehub.config.properties.HubProperties;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * @Descritption
 * @Date 2020/11/2
 * @Author tangqianli
 */
@SpringBootTest
public class HubPropertiesTest {

    @Autowired
    HubProperties hubProperties;

    @Test
    public void test(){
        System.out.println(hubProperties);
    }

}
