package com.ryan.project.smarthomehub;

import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.homegraph.v1.HomeGraphService;
import com.google.api.services.homegraph.v1.model.RequestSyncDevicesRequest;
import com.google.api.services.homegraph.v1.model.RequestSyncDevicesResponse;
import com.google.auth.http.HttpCredentialsAdapter;
import com.google.auth.oauth2.GoogleCredentials;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
class SmartHomeHubApplicationTests {

    @Autowired
    HomeGraphService homeGraphService;

    @Test
    void contextLoads() {
    }

    @Test
    void requestSyncTest() throws Exception {

        // Request sync.
        RequestSyncDevicesRequest request =
                new RequestSyncDevicesRequest().setAgentUserId("PLACEHOLDER-USER-ID").setAsync(false);
        RequestSyncDevicesResponse response = homeGraphService.devices().requestSync(request).execute();
        System.out.println(response.toPrettyString());
    }

}
