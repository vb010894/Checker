package ru.checker.tests.ssm.tests;

import lombok.extern.log4j.Log4j2;
import mmarquee.automation.controls.Panel;
import ru.checker.tests.desktop.test.app.CheckerDesktopForm;
import ru.checker.tests.desktop.test.app.CheckerDesktopWindow;
import ru.checker.tests.ssm.controls.grid.SSMGrid;
import ru.checker.tests.ssm.controls.grid.SSMGridData;
import ru.checker.tests.ssm.widgets.controllers.SSMToolsController;

import static org.junit.jupiter.api.Assertions.*;

@Log4j2(topic = "TEST CASE")
public final class SSMSapTests {

    /**
     * SSM 01 test
     * @param window Root window
     * @param form Root form
     */
    public static void SSM01(CheckerDesktopWindow window, CheckerDesktopForm form) {
        Panel sap_order_grid = form.panel("ssm_01_01", -1);
        SSMGrid grid = new SSMGrid(sap_order_grid);
        SSMToolsController menu = window.widget("ssm_menu", SSMToolsController.class);
        menu.toggle("ssm_01", false);
        assertTrue(grid.hasNotData(), "Таблица 'Заказы SAP' не пуста");
        menu.toggle("ssm_01", true);
        assertTrue(grid.hasData(), "Таблица 'Заказы SAP' пуста");
        assertTrue(
                grid.columnExistValue("С", "Открыт"),
                "В таблице 'Заказы SAP' найдено значение в колонке 'C' отличное от 'Открыт'. ID - 'ssm_01_01'");
        menu.toggle("ssm_01", false);
        menu.toggle("ssm_02", true);
        SSMGridData data = grid.getFirstPageData();
        assertTrue(data.getRowSize() > 0L, "Таблица 'Заказы SAP' пуста");
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
    }

    /**
     * ТС.ССМ.2. Заказы SAP. Назначение мастера на заказ.
     * @param window Root window
     * @param form Root form
     */
    public static void SSM02(CheckerDesktopWindow window, CheckerDesktopForm form) {

        Panel sap_order_grid = form.panel("ssm_01_01", -1);
        Panel sap_master_grid = form.panel("ssm_01_02", -1);
        Panel production_release_grid = form.panel("ssm_01_03", -1);

        log.info("Выбор первой строки таблицы 'Производственные заказы SAP'");
        SSMGrid sap_order_grid_wrapper = new SSMGrid(sap_order_grid);
        SSMGridData order_grid_data = sap_order_grid_wrapper.selectAndAcceptCell(0);
        String order_SAP = order_grid_data.getColumnData("Заказ SAP").get(0);
        log.info("Выбран заказ с номером '{}'", order_SAP);

        log.info("Выбор первой строки таблицы 'Мастера'");
        SSMGrid sap_master_grid_wrapper = new SSMGrid(sap_master_grid);
        SSMGridData master_grid_data = sap_master_grid_wrapper.selectAndAcceptCell(0);
        String tab_num = master_grid_data.getColumnData("Таб.").get(0);
        log.info("Выбор первой строки таблицы 'Мастера' c табельным номером {}" + tab_num);

        SSMToolsController tools = window.widget("ssm_menu", SSMToolsController.class);
        tools.clickButton("ssm_04");

        SSMGrid production_release_grid_wrapper = new SSMGrid(production_release_grid);
        assertTrue(production_release_grid_wrapper.hasData(), "В таблице 'Выпуск продукции по заказу' отсутствуют данные");
    }

}
