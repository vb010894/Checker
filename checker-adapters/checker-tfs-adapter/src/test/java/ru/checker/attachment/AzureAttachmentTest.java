package ru.checker.attachment;

import org.junit.jupiter.api.Test;
import ru.checker.AzureConnection;

import java.io.File;

import static org.junit.jupiter.api.Assertions.*;

class AzureAttachmentTest {

    private AzureConnection connection = new AzureConnection(
            "https://mestfs.severstal.severstalgroup.com/tfs",
            "MESProgrammers",
            "QMET",
            "username",
            "wqyxz3agfl4yio5h64zxeozui7rrl3qzz5wjrwzdcidofgzdecba");

    @Test
    public void create() {
        AzureAttachment azureAttachment = new AzureAttachment();
        azureAttachment.setConnection(connection);
        azureAttachment.setFile(new File("D:\\dialog-windows.bmp"));
        azureAttachment.upload();
    }
}