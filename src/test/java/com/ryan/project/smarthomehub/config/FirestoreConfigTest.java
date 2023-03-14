package com.ryan.project.smarthomehub.config;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.QuerySnapshot;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.util.Assert;

import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

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
    public void autoConfigTest() throws ExecutionException, InterruptedException, TimeoutException {

        Assert.notNull(database,"database should not be null!");

        ApiFuture<QuerySnapshot> deviceQuery =
                database.collection("users").document("1234").collection("devices").get();

        System.out.println(deviceQuery.get(10, TimeUnit.SECONDS).getDocuments().toString());

    }

    @Test
    public void migrantTest(){
        DocumentReference documentReference = database.collection("users").document("1234").collection("devices").document("jVOrS75jx88KT0cP4oWh");
        try {
            Map<String, Object> map = documentReference.get().get().getData();

            database.collection("users").document("9c8ac10fb21b41119c08cb665921a333").collection("devices").document("jVOrS75jx88KT0cP4oWh").set(map);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
