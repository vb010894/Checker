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
     * SSM.G.01.02.P.02. Работа с фильтрами. Фильтр 'Год'
     */
    @Test(
            testName = "SSM.G.01.02.P.01.03",
            description = "SSM.G.01.02.P.03. Работа с фильтрами. Фильтр 'Год'")
    public void SSMG0102P0103() {
        new SSMG0102P0103(getRootWindow()).run();
    }

    /**
     * SSM.G.01.02.P.04. Работа с фильтрами. Фильтр 'C'
     */
    @Test(
            testName = "SSM.G.01.02.P.01.04",
            description = "SSM.G.01.02.P.01.04. Работа с фильтрами. Фильтр 'C'")
    public void SSMG0102P0104() {
        new SSMG0102P0104(getRootWindow()).run();
    }

    /**
     * SSM.G.01.02.P.04. Работа с фильтрами. Фильтр 'Клиент'
     */
    // TODO: 15.08.2022 Доделать после комментария от менеджеров
    @Test(
            testName = "SSM.G.01.02.P.01.05",
            description = "SSM.G.01.02.P.01.05. Работа с фильтрами. Фильтр 'Клиент'",
    groups = {"broken"})
    public void SSMG0102P0105() {
        new SSMG0102P0105(getRootWindow()).run();
    }

    /**
     * SSM.G.01.02.P.06. Работа с фильтрами. Фильтр 'Заказ Лоцман'
     */
    @Test(
            testName = "SSM.G.01.02.P.01.06",
            description = "SSM.G.01.02.P.01.06. Работа с фильтрами. Фильтр 'Заказ Лоцман'",
    groups = {"inWork"})
    public void SSMG0102P0106() {
        new SSMG0102P0106(getRootWindow()).run();
    }

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
