package ru.checker.tests.ssm.tests;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.log4j.Log4j2;
import ru.checker.tests.desktop.test.entity.CheckerDesktopWindow;
import ru.checker.tests.ssm.controls.grid.SSMGrid;
import ru.checker.tests.ssm.controls.grid.SSMGridData;
import ru.checker.tests.ssm.temp.forms.SSMSapOrdersForm;

import java.util.Calendar;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * ТС.SSM.01. Заказы SAP. Работа с фильтрами.
 * @author vd.zinovev
 */
@Log4j2
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SSM01 implements Runnable {

    /**
     * Главное окно ССМ.
     */
    final CheckerDesktopWindow root;

    /**
     * Форма 'Заказы SAP'.
     */
    SSMSapOrdersForm form;

    /**
     * Конструктор.
     * @param root Родительский элемент
     */
    public SSM01(CheckerDesktopWindow root) {
        this.root = root;
    }

    /**
     * Фильтр 'C' со значением 'Открыт'.
     */
    final SSMGrid.ConditionConfigurer open_close_filter = SSMGrid
            .ConditionConfigurer
            .builder()
            .condition1(SSMGrid.Condition.EQUAL)
            .value1("Открыт")
            .column("С").build();

    /**
     * Запуск.
     */
    @Override
    public void run() {
        String year = String.valueOf(Calendar.getInstance().get(Calendar.YEAR));
        log.info("Установка текущего года {}", year);

        this.form = this.root.form("mf", SSMSapOrdersForm.class);
        this.form.selectYear(year);
        this.form.clickRefresh();

        log.info("Данные по году успешно обновлены");

        log.info("Проверка фильтра 'Открытые'");
        this.form.toggleOpened(false);
        SSMGrid orderGrid = this.form.getSapOrderGrid();
        SSMGridData data = orderGrid.getAllData();
        assertEquals(data.getRowSize(), 0, "В таблице 'Производственные заказы Sap' найдены данные");

        log.info("Проверка фильтра 'Закрытые'");
        this.form.toggleClosed(true);
        orderGrid.filterByGUI(open_close_filter, "С", "Н", "Т");
        data = orderGrid.getAllData();
        assertEquals(data.getRowSize(),0, "В таблице 'Производственные заказы SAP' найдены заказы с статусом 'Открыт'. Ошибка фильтра 'Закрытые'");
        log.info("Проверка пройдена");

        this.form.toggleClosed(false);
        this.form.toggleOpened(true);
        data = orderGrid.getAllData();
        assertNotEquals(data.getRowSize(), 0, "В таблице 'Производственные заказы Sap' не найдены данные");

        List<String> columnData = data.getColumnData("С");
        assertTrue(
                columnData.parallelStream().allMatch(row -> row.equals("Открыт")),
                "В таблице 'Производственные заказы Sap' найдены записи со статусом 'Закрыт'");
        log.info("Проверка пройдена");

        log.info("Проверка фильтра 'Цех'");
        this.form.toggleOpened(true);
        this.form.toggleClosed(false);

        String[] shops = new String[] {"РМЦ-1", "КПЦ", "ЦРМО-1"};
        for (String shop:shops) {
            log.info("Проверка фильтра 'Цех' со значением {}", shop);
            this.form.selectShop(shop);
            data = orderGrid.getAllData();
            List<String> shopColumn = data.getColumnData("Цех");
            assertTrue(shopColumn.parallelStream().allMatch(row -> row.equals(shop)), "Найдено запись отличная от " + shop);
            log.info("Проверка фильтра 'Цех' со значением {} прошла успешно", shop);
        }
        log.info("Проверка фильтра 'Цех' прошла успешно");
    }
}
