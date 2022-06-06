package ru.checker.attachment;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.checker.AzureConnection;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

@Component
@FieldDefaults(level = AccessLevel.PRIVATE)
@Getter
@Setter
public class AzureAttachment {
    AzureConnection connection;
    File file;
    String azureURL;

    @Autowired
    public void setConnection(AzureConnection connection) {
        this.connection = connection;
    }

    public void upload() {
        String url = this.connection.getUrl()
                + "/" + this.connection.getCollection()
                + "/" + this.connection.getProject()
                + "/_apis/wit/attachments?"
                + "fileName=" + this.file.getName()
                + "&uploadType=simple"
                + "&api-version=6.0";
        byte[] content = new byte[0];
        try {
            content = Files.readAllBytes(file.toPath());
        } catch (IOException e) {
            e.printStackTrace();
        }
        Request request = this.connection.getRequest()
                .url(url)
                .post(RequestBody.create(
                       MediaType.parse("application/octet-stream"),
                        content
                )).build();
        try {
            System.out.println(this.connection.getResponse(request).body().string());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
