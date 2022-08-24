package ru.checker.tests.ssm.tests.product;

import lombok.extern.log4j.Log4j2;
import org.testng.annotations.Test;
import ru.checker.tests.ssm.test.SSMTest;

@Log4j2
public class SSMProductReleaseTest extends SSMTest {

    @Test(
            testName = "ТС.SSM.G.01.01.P.01",
            description = "Выпуск продукции SAP. Работа с фильтрами")
    public void SSMG0101P01() {
        new SSMG01P01(getRootWindow(), "product_release").run();
    }

    @Test(
            testName = "ТС.SSM.G.01.01.P.02",
            description = "Выпуск продукции SAP. Выпуск")
    public void SSMG0101P02() {
        new SSMG01P02(getRootWindow(), "product_release").run();
    }
}

