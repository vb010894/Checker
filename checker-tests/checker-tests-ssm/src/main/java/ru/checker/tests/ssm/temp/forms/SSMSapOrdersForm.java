package ru.checker.tests.ssm.temp.forms;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import mmarquee.automation.controls.Panel;
import ru.checker.tests.desktop.test.entity.CheckerDesktopForm;
import ru.checker.tests.ssm.controls.grid.SSMGrid;
import ru.checker.tests.ssm.temp.widgets.SSMPage;
import ru.checker.tests.ssm.temp.widgets.SSMTools;

@FieldDefaults(level = AccessLevel.PRIVATE)
public class SSMSapOrdersForm {

    @Getter
    final CheckerDesktopForm form;

    final SSMTools tools;

    public SSMSapOrdersForm(CheckerDesktopForm form) {
        this.form = form;
        this.tools = this.form.widget("ssm_menu", SSMTools.class);
    }

    public void clickAdd() {
        System.out.println("Нажатие кнопки 'Добавить', ID - 'SSM_05'");
        this.tools.clickButton("ssm_05");
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
        System.out.println("Нажатие кнопки 'аНазначить', ID - 'SSM_04'");
        this.tools.clickButton("ssm_04");
    }

    public void toggleClosed(boolean state) {
        this.tools.toggle("ssm_02", state);
    }

    public void toggleOpened(boolean state) {
        this.tools.toggle("ssm_01", state);
    }

    public SSMGrid getSapOrderGrid() {
        return this.form.custom("ssm_01_01", -1, SSMGrid.class);
    }

    public SSMGrid getMasterGrid() {
        return this.form.custom("ssm_01_02", -1, SSMGrid.class);
    }

    public SSMGrid getProductionReleaseGrid() {
        return this.form.custom("ssm_01_03", -1, SSMGrid.class);
    }

    public void selectShop(String name) {
        this.tools.selectCombobox("ssm_03", name);
    }

    public void selectPage(String pageName) {
        SSMPage pages = this.form.widget("ssm_paging", SSMPage.class);
        pages.selectTab(pageName);
    }

}
