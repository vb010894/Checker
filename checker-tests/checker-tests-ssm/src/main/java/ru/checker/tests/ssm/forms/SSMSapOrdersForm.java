package ru.checker.tests.ssm.forms;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import lombok.extern.log4j.Log4j2;
import ru.checker.tests.desktop.test.entity.CheckerDesktopForm;
import ru.checker.tests.desktop.test.temp.CheckerDesktopTest;
import ru.checker.tests.ssm.controls.grid.SSMGrid;
import ru.checker.tests.ssm.widgets.SSMPage;
import ru.checker.tests.ssm.widgets.SSMTools;
import ru.checker.tests.ssm.windows.sap.SapLotsmanFilterWindow;
import ru.checker.tests.ssm.windows.sap.SapPRBCreationWindow;

@Log4j2
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SSMSapOrdersForm {

    @Getter
    final CheckerDesktopForm form;

    final SSMTools tools;

    public SSMSapOrdersForm(CheckerDesktopForm form) {
        this.form = form;
        this.tools = this.form.widget("ssm_menu", SSMTools.class);
    }

    public void callFilter() {
        log.info("Вызов окна 'Фильтр' по кнопке 'Открыть'");
        this.tools.clickButton("sap_filter");
        log.info("Кнопка 'Открыть' нажата");
    }

    public SapPRBCreationWindow clickAdd() {
        log.info("Нажатие кнопки 'Добавить'");
        this.tools.clickButton("ssm_05");
        log.info("Инициализация окна 'Задание ПРБ создание'");
        SapPRBCreationWindow prbWindow = CheckerDesktopTest.getCurrentApp().window("sap_order_prb_form", SapPRBCreationWindow.class);
        log.info("Окно инициализировано");
        return prbWindow;
    }

    public void selectYear(String year) {
        System.out.println("Выбор года. Фильтр 'Год', ID - 'SSM_06'");
        this.tools.selectCombobox("ssm_06", year);
    }

    public void clickRefresh() {
        System.out.println("Нажатие кнопки 'Обновить', ID - 'SSM_07'");
        this.tools.clickButton("ssm_07");
    }

    public void clickAssign() {
        System.out.println("Нажатие кнопки 'Назначить', ID - 'SSM_04'");
        this.tools.clickButton("ssm_04");
    }


    public SSMGrid getSapOrderGrid() {
        return this.form.custom("ssm_01_01", -1, SSMGrid.class);
    }

    public SSMGrid getMasterGrid() {
        log.info("Получение таблицы 'Мастера' формы 'Заказы SAP'");
        return this.form.custom("ssm_01_02", -1, SSMGrid.class);
    }

    public SSMGrid getOperationGrid() {
        log.info("Получение таблицы 'Операции' формы 'Заказы SAP'");
        return this.form.custom("ssm_01_05", -1, SSMGrid.class);
    }

    public SSMGrid getProductionReleaseGrid() {
        log.info("Получение таблицы 'Заказ продукции' формы 'Заказы SAP'");
        return this.form.custom("ssm_01_03", -1, SSMGrid.class);
    }


    public SSMGrid getOrderPRBGrid() {
        log.info("Получение таблицы 'Задания ПРБ' формы 'Заказы SAP'");
        return this.form.custom("ssm_01_04", -1, SSMGrid.class);
    }


    public void selectPage(String pageName) {

        SSMPage pages = this.form.widget("ssm_paging", SSMPage.class);
        pages.selectTab(pageName);
    }

}
