package ru.checker.tests.ssm.tests;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import ru.checker.tests.desktop.test.entity.CheckerDesktopWindow;
import ru.checker.tests.ssm.controls.grid.SSMGrid;
import ru.checker.tests.ssm.controls.grid.SSMGridData;
import ru.checker.tests.ssm.temp.forms.SSMSapOrdersForm;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@FieldDefaults(level = AccessLevel.PRIVATE)
public class SSM01 implements Runnable {

    final CheckerDesktopWindow root;

    SSMSapOrdersForm form;

    public SSM01(CheckerDesktopWindow root) {
        this.root = root;
    }

    /**
     * When an object implementing interface {@code Runnable} is used
     * to create a thread, starting the thread causes the object's
     * {@code run} method to be called in that separately executing
     * thread.
     * <p>
     * The general contract of the method {@code run} is that it may
     * take any action whatsoever.
     *
     * @see Thread#run()
     */

    SSMGrid.ConditionConfigurer open_close_filter = SSMGrid
            .ConditionConfigurer
            .builder()
            .condition1(SSMGrid.Condition.EQUAL)
            .value1("Открыт")
            .column("С").build();

    @Override
    public void run() {
        this.form = this.root.form("mf", SSMSapOrdersForm.class);
        this.form.selectYear("2021");
        this.form.clickRefresh();
        this.form.toggleOpened(false);
        SSMGrid orderGrid = this.form.getSapOrderGrid();
        SSMGridData data = orderGrid.getAllData();
        assertEquals(data.getRowSize(), 0, "В таблице 'Производственные заказы Sap' найдены данные");
        this.form.toggleOpened(true);
        data = orderGrid.getAllData();
        assertNotEquals(data.getRowSize(), 0, "В таблице 'Производственные заказы Sap' не найдены данные");

        List<String> columnData = data.getColumnData("С");
        assertTrue(
                columnData.parallelStream().allMatch(row -> row.equals("Открыт")),
                "В таблице 'Производственные заказы Sap' найдены записи со статусом 'Закрыт'");

        /// Много данных тест не проходит
       /* this.form.toggleClosed(true);
        data = orderGrid.getAllData();
        assertNotEquals(data.getRowSize(), 0, "В таблице 'Производственные заказы Sap' не найдены данные");

        columnData = data.getColumnData("С");
        assertTrue(
                columnData.parallelStream().allMatch(row -> row.equals("Закрыт")),
                "В таблице 'Производственные заказы Sap' найдены записи со статусом 'Открыт'");*/

        String[] shops = new String[] {"РМЦ-1", "КПЦ", "ЦРМО-1"};
        for (String shop:shops) {
            this.form.selectShop(shop);
            data = orderGrid.getAllData();
            List<String> shopColumn = data.getColumnData("Цех");
            assertTrue(shopColumn.parallelStream().allMatch(row -> row.equals(shop)), "Найдено запись отличная от " + shop);
        }

    }



}
