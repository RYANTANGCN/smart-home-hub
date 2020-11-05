package com.ryan.project.smarthomehub.config;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.QuerySnapshot;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.util.Assert;

import java.util.concurrent.ExecutionException;

/**
 * @Descritption
 * @Date 2020/11/3
 * @Author tangqianli
 */
@SpringBootTest
public class FirestoreConfigTest {

    @Autowired
    Firestore database;

    @Test
    public void autoConfigTest() throws ExecutionException, InterruptedException {

        Assert.notNull(database,"database should not be null!");

        ApiFuture<QuerySnapshot> deviceQuery =
                database.collection("users").document("1234").collection("devices").get();

        System.out.println(deviceQuery.get().getDocuments().toString());

    }
}
