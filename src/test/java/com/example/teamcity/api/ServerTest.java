package com.example.teamcity.api;

import com.example.teamcity.api.models.Server;
import org.testng.annotations.Test;

import static com.example.teamcity.api.enums.Endpoint.SERVER;

public class ServerTest extends BaseApiTest {
    @Test
    public void getServerInfo() {
        var response = superUserCheckRequests.<Server>getRequest(SERVER).read("");
        softy.assertThat(response).isNotNull();
    }
}
