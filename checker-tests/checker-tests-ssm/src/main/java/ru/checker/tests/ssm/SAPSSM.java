package ru.checker.tests.ssm;

import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.*;
import org.springframework.stereotype.Component;
import ru.checker.tests.ssm.base.SSMTestCase;
import ru.checker.tests.ssm.tests.SSMSapTests;

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
      /*  SSMToolsMenu menu = new SSMToolsMenu(filterPZ);
        menu.clickOnField("Закрытые");*/
       /* CheckerDesktopWindow prb = getSApplication().window("sap_oreder_prb_form");
        Button b = prb.getControl().getButton("Отмена");
        b.click();*/
     //   prb.getWindow().close();
    }

    /**
     * ТС.SSM.02 test
     */
    @DisplayName("ТС.SSM.01.Заказы SAP. Работа с фильтрами")
    @Test
    void ssm02() {
        SSMSapTests.SSM02(this.getRootWindow(), this.getForm());
    }


}
