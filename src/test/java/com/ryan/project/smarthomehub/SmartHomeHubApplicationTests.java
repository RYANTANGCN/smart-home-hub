package com.ryan.project.smarthomehub;

import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.homegraph.v1.HomeGraphService;
import com.google.api.services.homegraph.v1.model.RequestSyncDevicesRequest;
import com.google.auth.http.HttpCredentialsAdapter;
import com.google.auth.oauth2.GoogleCredentials;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
class SmartHomeHubApplicationTests {

    @Test
    void contextLoads() {
    }

    @Test
    void requestSyncTest() throws Exception {
        // Get Application Default credentials.
        GoogleCredentials credentials =
                GoogleCredentials.getApplicationDefault()
                        .createScoped(List.of("https://www.googleapis.com/auth/homegraph"));

        // Create Home Graph service client.
        HomeGraphService homegraphService =
                new HomeGraphService.Builder(
                        GoogleNetHttpTransport.newTrustedTransport(),
                        GsonFactory.getDefaultInstance(),
                        new HttpCredentialsAdapter(credentials))
                        .setApplicationName("Smart-Home-Hub/1.0")
                        .build();

        // Request sync.
        RequestSyncDevicesRequest request =
                new RequestSyncDevicesRequest().setAgentUserId("PLACEHOLDER-USER-ID").setAsync(false);
        homegraphService.devices().requestSync(request);

    }

}
