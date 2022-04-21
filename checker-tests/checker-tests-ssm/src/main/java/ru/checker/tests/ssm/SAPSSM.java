package ru.checker.tests.ssm;

import lombok.extern.log4j.Log4j2;
import mmarquee.automation.controls.Panel;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.stereotype.Component;
import ru.checker.tests.ssm.base.SSMTestCase;
import ru.checker.tests.ssm.controls.grid.SSMGrid;
import ru.checker.tests.ssm.controls.grid.SSMGridData;
import ru.checker.tests.ssm.widgets.controllers.SSMToolsController;

import static org.junit.jupiter.api.Assertions.*;

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

        Panel sap_order_grid = this.getForm().panel("ssm_01_01", -1);
        SSMGrid grid = new SSMGrid(sap_order_grid);
        SSMToolsController menu = this.getRootWindow().widget("ssm_menu", SSMToolsController.class);
        menu.toggle("ssm_01", false);
        SSMGridData data = grid.getAllData();
        assertEquals(data.getRowSize(), 0L, "Таблица 'Заказы SAP' не пуста");
        menu.toggle("ssm_01", true);
        data = grid.getAllData();
        assertNotEquals(data.getRowSize(), 0L, "Таблица 'Заказы SAP' пуста");
        assertTrue(
                grid.columnExistValue("С", "Открыт"),
                "В таблице 'Заказы SAP' найдено значение в колонке 'C' отличное от 'Открыт'. ID - 'ssm_01_01'");
        menu.toggle("ssm_01", false);
        menu.toggle("ssm_02", true);
        data = grid.getFirstPageData();
        assertNotEquals(data.getRowSize(), 0L, "Таблица 'Заказы SAP' пуста");
        assertTrue(
                grid.columnExistValue("С", "Закрыт"),
                "В таблице 'Заказы SAP' найдено значение в колонке 'C' отличное от 'Закрыт'. ID - 'ssm_01_01'");

        menu.toggle("ssm_02", false);
        menu.toggle("ssm_01", true);

        menu.selectCombobox("ssm_03", "РМЦ-1");
        grid.getAllData();
        assertTrue(
                grid.columnExistValue("Цех", "РМЦ-1"),
                "В таблице 'Заказы SAP' найдено значение в колонке 'Цех' отличное от 'РМЦ-1'. ID - 'ssm_01_01'");
        menu.selectCombobox("ssm_03", "КПЦ");
        grid.getAllData();
        assertTrue(
                grid.columnExistValue("Цех", "КПЦ"),
                "В таблице 'Заказы SAP' найдено значение в колонке 'Цех' отличное от 'КПЦ'. ID - 'ssm_01_01'");
        menu.selectCombobox("ssm_03", "ЦРМО-1");
        grid.getAllData();
        assertTrue(
                grid.columnExistValue("Цех", "ЦРМО-1"),
                "В таблице 'Заказы SAP' найдено значение в колонке 'Цех' отличное от 'ЦРМО-1'. ID - 'ssm_01_01'");
      /*  SSMToolsMenu menu = new SSMToolsMenu(filterPZ);
        menu.clickOnField("Закрытые");*/
       /* CheckerDesktopWindow prb = getSApplication().window("sap_oreder_prb_form");
        Button b = prb.getControl().getButton("Отмена");
        b.click();*/
     //   prb.getWindow().close();
    }


}
