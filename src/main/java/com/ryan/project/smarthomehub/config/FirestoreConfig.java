package com.ryan.project.smarthomehub.config;

import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.homegraph.v1.HomeGraphService;
import com.google.auth.http.HttpCredentialsAdapter;
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
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.List;

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

    private GoogleCredentials googleCredentials;

    public GoogleCredentials googleCredentials() throws IOException {
        if (this.googleCredentials == null) {
            this.googleCredentials = GoogleCredentials.getApplicationDefault().createScoped();
        }
        return this.googleCredentials;
    }

    @Bean
    public Firestore init(){
        try {
            FirebaseOptions options =
                    new FirebaseOptions.Builder().setCredentials(googleCredentials()).setProjectId(hubProperties.getProjectId()).build();
            FirebaseApp firebaseApp = FirebaseApp.initializeApp(options);
            Firestore database = FirestoreClient.getFirestore(firebaseApp);
            log.info("Firestore init finished......");
            return database;
        } catch (Exception e) {
            log.error("ERROR: invalid service account credentials. See README.");
            throw new RuntimeException(e.getMessage());
        }
    }

    @Bean
    public HomeGraphService homeGraphService() throws IOException, GeneralSecurityException {

        // Create Home Graph service client.
        HomeGraphService homegraphService =
                new HomeGraphService.Builder(
                        GoogleNetHttpTransport.newTrustedTransport(),
                        GsonFactory.getDefaultInstance(),
                        new HttpCredentialsAdapter(googleCredentials()))
                        .setApplicationName("Smart-Home-Hub/1.0")
                        .build();

        return homegraphService;
    }
}
