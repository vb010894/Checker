package ru.checker.tests.ssm.tests.sap;

import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.DisplayName;
import org.testng.annotations.Test;
import ru.checker.tests.ssm.temp.test.SSMTest;

/**
 * SSM 'SAP Orders' form testing.
 * Test case file - SSM_SAP.yaml
 * @author vd.zinovev
 */
@DisplayName("Тесты ССМ. Форма Заказы SAP")
@Log4j2(topic = "TEST CASE")
public class SAPSSM extends SSMTest {

    /**
     * SSM.G.01.02.P.01. Работа с фильтрами. Настройки по умолчанию
     */
    @Test(
            testName = "SSM.G.01.02.P.01.01",
            description = "SSM.G.01.02.P.01. Работа с фильтрами. Настройки по умолчанию")
    public void SSMG0102P0101() {
        new SSMG0102P0101(getRootWindow()).run();
    }

    /**
     * SSM.G.01.02.P.02. Работа с фильтрами. Фильтр 'ЦЕХ'
     */
    @Test(
            testName = "SSM.G.01.02.P.01.02",
            description = "SSM.G.01.02.P.02. Работа с фильтрами. Фильтр 'ЦЕХ'")
    public void SSMG0102P0102() {
        new SSMG0102P0102(getRootWindow()).run();
    }

    /**
     * ТС.SSM.03. Заказы SAP. Назначение мастера на операции
     */
    @Test(
            testName = "ТС.SSM.03",
            description = "ТС.SSM.03. Заказы SAP. Назначение мастера на операции")
    public void ssm03() {
        new SSM03(getRootWindow()).run();
    }

    /**
     * ТС.SSM.04 test
     */
    /*@Test(
            testName = "ТС.SSM.04",
            description = "ТС.SSM.04.Skip")
    public void ssm04() {
        throw new SkipException("Skipped");
    }*/


    /**
     * ТС.SSM.02 test
     */
   /* @DisplayName("ТС.SSM.01.Заказы SAP. Работа с фильтрами")
    @Test
    void ssm02() {
        SSMSapTests.SSM02(this.getRootWindow(), this.getForm());
    }*/

    /**
     * ТС.SSM.03 test
     */
    /*@DisplayName("ТС.SSM.01.Заказы SAP. Работа с фильтрами")
    @Test
    void ssm03() {
        SSMSapTests.SSM03(this.getRootWindow(), this.getForm());
    }*/

    /**
     * ТС.SSM.04 test
     */
    /*@DisplayName("ТС.ССМ.4.Заказы SAP. Ручное назначение мастера на операцию")
    @Test
    @Disabled
    void ssm04() {
        SSMSapTests.SSM04(this.getRootWindow(), this.getForm());
    }
*/
    /**
     * Draft
     */
  /*  @DisplayName("ТС.SSM.01.Заказы SAP. Работа с фильтрами")
    @Test
    //@Disabled
    void draft() {

    }

*/
}
