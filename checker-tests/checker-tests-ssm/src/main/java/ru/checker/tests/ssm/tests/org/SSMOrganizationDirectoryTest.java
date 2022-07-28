package ru.checker.tests.ssm.tests.org;

import org.testng.annotations.Test;
import ru.checker.tests.ssm.temp.test.SSMTest;

public class SSMOrganizationDirectoryTest extends SSMTest {
    @Test(
            testName = "SSM.G.01.03.P.01",
            description = "Справочник организации. Добавление цеха")
    public void SSMG0103P01() {
        new SSMG0103P01(getRootWindow(), "organization_directory").run();  }

}
