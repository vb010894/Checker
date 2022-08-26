package ru.checker.tests.ssm.tests.sap;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.log4j.Log4j2;
import ru.checker.tests.desktop.test.entity.CheckerDesktopWindow;
import ru.checker.tests.ssm.controls.grid.SSMGrid;
import ru.checker.tests.ssm.controls.grid.SSMGridData;
import ru.checker.tests.ssm.forms.SSMSapOrdersForm;
import ru.checker.tests.ssm.windows.sap.SapFilterWindow;

import java.text.SimpleDateFormat;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * SSM.G.01.02.P.01. Работа с фильтрами. Настройки по умолчанию
 * @author vd.zinovev
 */
@Log4j2
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SSMG0102P0101 implements Runnable {

    /**
     * Главное окно ССМ.
     */
    final CheckerDesktopWindow root;

    /**
     * Конструктор.
     * @param root Родительский элемент
     */
    public SSMG0102P0101(CheckerDesktopWindow root) {
        this.root = root;
    }

    /**
     * Фильтр 'C' со значением 'Закрыт'.
     */
    final SSMGrid.ConditionConfigurer open_close_filter = SSMGrid
            .ConditionConfigurer
            .builder()
            .condition1(SSMGrid.Condition.EQUAL)
            .value1("Закрыт")
            .columnCondition("[CСсс]")
            .column("С").build();

    /**
     * Фильтр 'ДеБлок' со значением меньшим текущего года.
     */
    final SSMGrid.ConditionConfigurer year_filter = SSMGrid
            .ConditionConfigurer
            .builder()
            .condition1(SSMGrid.Condition.LESS_THEN)
            .value1("01.01." + new SimpleDateFormat("yyyy").format(new Date()))
            .column("ДеБлок")
            .build();

    /**
     * Запуск.
     */
    @Override
    public void run() {
        SapFilterWindow filter_window = SAPSSM.getFilter();

        log.info("Проверка фильтров с выключенным переключателем 'Открытые' и нажатием кнопки 'OK'");
        filter_window.toggleOpened(false);
        filter_window.clickOK();
        log.info("Открытие формы 'Заказы SAP'");

        SSMSapOrdersForm orders = this.root.form("mf", SSMSapOrdersForm.class);
        log.info("Форма 'Заказы SAP' успешно запущена");
        SSMGrid orders_grid = orders.getSapOrderGrid();
        log.info("Проверка таблицы 'Прозводственные заказы SAP'");
        SSMGridData data = orders_grid.getDataFromRow(0);
        assertEquals(
                data.getRowSize(),
                0,
                "В таблице 'Прозводственные заказы SAP' найдены записи" +
                        " при выключенных переключателях 'Открытые' и  'Закрытые'");
        log.info("В таблице 'Прозводственные заказы SAP' отсутствуют записи");
        orders.callFilter();
        filter_window.refresh();
        filter_window.clickCancel();
        log.info("Проверка таблицы 'Прозводственные заказы SAP'");
        data = orders_grid.getDataFromRow(0);
        assertEquals(
                data.getRowSize(),
                0,
                "В таблице 'Прозводственные заказы SAP' найдены записи" +
                        " при выключенных переключателях 'Открытые' и  'Закрытые'");
        log.info("В таблице 'Прозводственные заказы SAP' отсутствуют записи");

        orders.callFilter();
        filter_window.toggleOpened(true);
        filter_window.refresh();
        filter_window.clickOK();
        orders_grid.filterByGUI(open_close_filter);
        data = orders_grid.getDataFromRow(0);
        assertEquals(
                data.getRowSize(),
                0,
                "В таблице 'Прозводственные заказы SAP' найдены записи" +
                        " со значением 'Закрыт' при включенном переключателе 'Открытые'");
        log.info("В таблице 'Прозводственные заказы SAP' отсутствуют записи со значением отличным от 'Открыт'");
        orders_grid.clearFilter();
        log.info("Проверка столбца 'Год' на наличие данных меньших чем текущий год");
        orders_grid.filterByGUI(year_filter);
        data = orders_grid.getDataFromRow(0);
        assertEquals(
                data.getRowSize(),
                0,
                "В таблице 'Прозводственные заказы SAP' найдены записи" +
                        " со значением менее текущего года при настройках по умолчанию");
        orders_grid.clearFilter();
        log.info("В таблице 'Прозводственные заказы SAP' отсутствуют записи со значением отличным от 'Открыт'");
        log.info("Тестовый случай отрешался успешно");
    }
}
