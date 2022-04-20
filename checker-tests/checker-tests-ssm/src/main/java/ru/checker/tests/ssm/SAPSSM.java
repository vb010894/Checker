package ru.checker.tests.ssm;

import lombok.extern.log4j.Log4j2;
import mmarquee.automation.AutomationException;
import mmarquee.automation.controls.Panel;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.stereotype.Component;
import ru.checker.tests.ssm.base.SSMTestCase;
import ru.checker.tests.ssm.controls.grid.SSMGrid;
import ru.checker.tests.ssm.controls.grid.SSMGridData;
import ru.checker.tests.ssm.widgets.controllers.SSMToolsController;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Тесты ССМ. Форма Заказы SAP")
@Component("SAP_SSM")
@Log4j2(topic = "TEST CASE")
public class SAPSSM extends SSMTestCase {

    static {
        prepare("SSM", "SSM_SAP");
    }

    @DisplayName("ТС.SSM.01.Заказы SAP. Работа с фильтрами")
    @Test
    void check() {
        Panel sap_order_grid = this.getForm().panel("ssm_01_01", -1);
        SSMGrid grid = new SSMGrid(sap_order_grid);
        SSMToolsController menu = this.getRootWindow().widget("ssm_menu", SSMToolsController.class);
        menu.toggle("ssm_01", false);
        SSMGridData data = grid.getAllData();
        assertEquals(data.getRowSize(), 0, "Таблица 'Заказы SAP' не пуста");
        menu.toggle("ssm_01", true);
        data = grid.getAllData();
        assertNotEquals(data.getRowSize(), 0, "Таблица 'Заказы SAP' пуста");
        assertTrue(
                grid.columnExistValue("С", "Открыт"),
                "В таблице 'Заказы SAP' найдено значение в колонке 'C' отличное от 'Открыт'. ID - 'ssm_01_01'");
        menu.toggle("ssm_01", false);
        menu.toggle("ssm_02", true);
        data = grid.getFirstPageData();
        assertNotEquals(data.getRowSize(), 0, "Таблица 'Заказы SAP' пуста");
        assertTrue(
                grid.columnExistValue("С", "Закрыт"),
                "В таблице 'Заказы SAP' найдено значение в колонке 'C' отличное от 'Закрыт'. ID - 'ssm_01_01'");

      /*  SSMToolsMenu menu = new SSMToolsMenu(filterPZ);
        menu.clickOnField("Закрытые");*/
       /* CheckerDesktopWindow prb = getSApplication().window("sap_oreder_prb_form");
        Button b = prb.getControl().getButton("Отмена");
        b.click();*/
     //   prb.getWindow().close();

        System.out.println("a");
    }


}
