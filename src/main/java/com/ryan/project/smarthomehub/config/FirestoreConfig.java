package com.ryan.project.smarthomehub.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.firestore.Firestore;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.cloud.FirestoreClient;
import com.ryan.project.smarthomehub.config.properties.HubProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;

/**
 * @Descritption Firebase configuration class, more detail refer to: https://firebase.google.com/docs/admin/setup
 * @Date 2020/11/3
 * @Author tangqianli
 */
@Slf4j
@Configuration
public class FirestoreConfig {

    @Autowired
    HubProperties hubProperties;

    @Bean
    public Firestore init(){
        try {
            GoogleCredentials credentials = GoogleCredentials.getApplicationDefault();
            FirebaseOptions options =
                    new FirebaseOptions.Builder().setCredentials(credentials).setProjectId(hubProperties.getProjectId()).build();
            FirebaseApp firebaseApp = FirebaseApp.initializeApp(options);
            Firestore database = FirestoreClient.getFirestore(firebaseApp);
            log.info("Firestore init finished......");
            return database;
        } catch (Exception e) {
            log.error("ERROR: invalid service account credentials. See README.");
            throw new RuntimeException(e.getMessage());
        }
    }
}
