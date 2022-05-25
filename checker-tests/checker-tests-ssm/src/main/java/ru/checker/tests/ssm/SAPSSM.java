package ru.checker.tests.ssm;

import lombok.extern.log4j.Log4j2;
import mmarquee.automation.AutomationException;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.stereotype.Component;
import ru.checker.tests.ssm.base.SSMTestCase;
import ru.checker.tests.ssm.tests.SSMSapTests;

import java.awt.*;

/**
 * SSM 'SAP Orders' form testing.
 * Test case file - SSM_SAP.yaml
 * @author vd.zinovev
 */
@DisplayName("Тесты ССМ. Форма Заказы SAP")
@Component("SAP_SSM")
@Log4j2(topic = "TEST CASE")
public class SAPSSM extends SSMTestCase {

    // prepare test case.
    static {
        prepare("SSM_SAP");
    }

    /**
     * ТС.SSM.01 test
     */
    @DisplayName("ТС.SSM.01.Заказы SAP. Работа с фильтрами")
    @Test
    void ssm01() {
        SSMSapTests.SSM01(this.getRootWindow(), this.getForm());
    }

    /**
     * ТС.SSM.02 test
     */
    @DisplayName("ТС.SSM.01.Заказы SAP. Работа с фильтрами")
    @Test
    void ssm02() {
        SSMSapTests.SSM02(this.getRootWindow(), this.getForm());
    }

    /**
     * ТС.SSM.03 test
     */
    @DisplayName("ТС.SSM.01.Заказы SAP. Работа с фильтрами")
    @Test
    void ssm03() {
        SSMSapTests.SSM03(this.getRootWindow(), this.getForm());
    }

    /**
     * ТС.SSM.04 test
     */
    @DisplayName("ТС.ССМ.4.Заказы SAP. Ручное назначение мастера на операцию")
    @Test
    @Disabled
    void ssm04() {
        SSMSapTests.SSM04(this.getRootWindow(), this.getForm());
    }

    /**
     * Draft
     */
    @DisplayName("ТС.SSM.01.Заказы SAP. Работа с фильтрами")
    @Test
    @Disabled
    void draft() {

    }


}
