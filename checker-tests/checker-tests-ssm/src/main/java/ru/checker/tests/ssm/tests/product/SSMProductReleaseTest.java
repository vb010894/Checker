package ru.checker.tests.ssm.tests.product;

import lombok.extern.log4j.Log4j2;
import org.testng.annotations.Test;
import ru.checker.tests.ssm.temp.test.SSMTest;
import ru.checker.tests.ssm.tests.product.SSMG01P01;

@Log4j2
public class SSMProductReleaseTest extends SSMTest {

    @Test(
            testName = "ТС.SSM.G.01.01.P.01",
            description = "Выпуск продукции SAP. Работа с фильтрами")
    public void SSMG0101P01() {
       new SSMG01P01(getRootWindow(), "product_release").run();
    }

    @Test(
            testName = "ТС.SSM.G.01.01.P.01",
            description = "Выпуск продукции SAP. Работа с фильтрами 2")
    public void SSMG0101P012() {
        new SSMG01P01(getRootWindow(), "product_release").run();
    }

    @Test(
            testName = "ТС.SSM.G.01.01.P.01",
            description = "Выпуск продукции SAP. Работа с фильтрами 3")
    public void SSMG0101P013() {
        new SSMG01P01(getRootWindow(), "product_release").run();
    }

}
