package ru.checker;

import okhttp3.Credentials;
import okhttp3.Request;
import okhttp3.Response;
import org.junit.jupiter.api.Test;

import java.io.IOException;

class AzureConnectionTest {

    private AzureConnection connection = new AzureConnection(
            "https://mestfs.severstal.severstalgroup.com/tfs",
            "MESProgrammers",
            "QMET",
            "username",
            "wqyxz3agfl4yio5h64zxeozui7rrl3qzz5wjrwzdcidofgzdecba");

    @Test
    public void draft() throws IOException {
        Request request = new Request
                .Builder()
                .url(this.connection.getUrl() + "/" +  this.connection.getCollection() + "/" + this.connection.getProject() + "/_apis/test/runs?api-version=6.0")
                .get()
                .header("Authorization", Credentials.basic(this.connection.getUsername(), this.connection.getToken()))
                .build();

        Response resp = this.connection.getResponse(request);
        String body = resp.body().string();
        System.out.println(body);

    }


}