package ru.checker.workitems;

import org.junit.jupiter.api.Test;
import ru.checker.AzureConnection;
import ru.checker.enums.workitems.AzureActivity;

import static org.junit.jupiter.api.Assertions.*;

class AzureErrorTest {

    private AzureConnection connection = new AzureConnection(
            "https://mestfs.severstal.severstalgroup.com/tfs",
            "MESProgrammers",
            "QMET",
            "username",
            "wqyxz3agfl4yio5h64zxeozui7rrl3qzz5wjrwzdcidofgzdecba");

    @Test
    public void JSONCreationTest() {
        AzureError error = AzureError.builder()
                .activity(AzureActivity.Testing)
                .area("QMET")
                .assignedTo("SEVERSTAL\\vd.zinovev")
                .priority(3)
                .title("Test")
                .reproSteps("test").build();
        error.setConnection(connection);
        error.create();
    }

}