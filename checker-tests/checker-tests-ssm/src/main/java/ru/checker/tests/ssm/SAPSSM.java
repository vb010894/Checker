package ru.checker.tests.ssm;

import lombok.extern.log4j.Log4j2;
import mmarquee.automation.controls.Panel;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.*;
import org.springframework.stereotype.Component;
import ru.checker.tests.ssm.base.SSMTestCase;
import ru.checker.tests.ssm.controls.grid.SSMGrid;
import ru.checker.tests.ssm.controls.grid.SSMGridData;
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
    void ssm04() {
        SSMSapTests.SSM04(this.getRootWindow(), this.getForm());
    }

    /**
     * Draft
     */
    @DisplayName("ТС.SSM.01.Заказы SAP. Работа с фильтрами")
    @Test
    void draft() {
        Panel sap_order_grid = this.getForm().panel("ssm_01_01", -1);
        SSMGrid sap_order_grid_wrapper = new SSMGrid(sap_order_grid);
        SSMGrid.ConditionConfigurer config = SSMGrid.ConditionConfigurer
                .builder()
                .column("Н")
                .condition1(SSMGrid.Condition.EQUAL)
                .value1("000058005621")
                .separator(SSMGrid.Separator.OR)
                .condition2(SSMGrid.Condition.EQUAL)
                .value2("222")
                .build();
        sap_order_grid_wrapper.filterByGUI(config, "С", "Н", "Т");
        sap_order_grid_wrapper.clearFilter();
        System.out.println("aaa");
    }


}
