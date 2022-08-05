package ru.checker.tests.ssm.tests.futures;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.testng.annotations.Test;
import ru.checker.tests.ssm.controls.grid.SSMGrid;
import ru.checker.tests.ssm.controls.grid.SSMGridData;
import ru.checker.tests.ssm.temp.forms.SSMSapOrdersForm;
import ru.checker.tests.ssm.temp.test.SSMTest;

import static org.junit.jupiter.api.Assertions.*;

@FieldDefaults(level = AccessLevel.PRIVATE)
public class TestSapOrdersFutures extends SSMTest {


    SSMSapOrdersForm form;

    public SSMSapOrdersForm getForm() {
        if(this.form == null) {
            this.form = getRootWindow().form("mf", SSMSapOrdersForm.class);
        }

        return this.form;
    }

    /**
     * Проверка панели инструментов SAP
     */
    @Test(testName = "Проверка кнопок верхней панели",
    description = "Проверка инструментов")
    public void toolsСheck() {
      this.getForm().selectYear("2021");
      this.getForm().clickRefresh();
    }


    /**
     * Проверка таблиц SAP
     */
    @Test(testName = "Проверка Таблиц",
            description = "Проверка таблиц")
    public void gridCheck() {
        this.toolsСheck();
        SSMGrid sapGrid = this.getForm().getSapOrderGrid();
        SSMGridData data = sapGrid.getAllData();
        assertNotEquals(data.getRowSize(), 0, "Не получены данные из таблицы");

        SSMGrid masterGrid = this.getForm().getSapOrderGrid();
        data = masterGrid.getAllData();
        assertNotEquals(data.getRowSize(), 0, "Не получены данные из таблицы");

        SSMGrid productionGrid = this.getForm().getMasterGrid();
        data = productionGrid.getAllData();
        assertNotEquals(data.getRowSize(), 0, "Не получены данные из таблицы");

    }

    /**
     * Проверка таблиц SAP
     */
    @Test(testName = "Проверка закрытия всплывающих окон",
            description = "Проверка всплывающих окон")
    public void popupCheck() {
        this.getForm().clickAdd();

    }


}
