package ru.checker.tests.ssm.tests.sap;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.log4j.Log4j2;
import ru.checker.tests.desktop.test.entity.CheckerDesktopWindow;
import ru.checker.tests.ssm.controls.grid.SSMGrid;
import ru.checker.tests.ssm.controls.grid.SSMGridData;
import ru.checker.tests.ssm.forms.SSMSapOrdersForm;
import ru.checker.tests.ssm.windows.SapFilterWindow;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * SSM.G.01.02.P.01.04. Работа с фильтрами. Фильтр 'C'
 * @author vd.zinovev
 */
@Log4j2
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SSMG0102P0104 implements Runnable {

    /**
     * Главное окно ССМ.
     */
    final CheckerDesktopWindow root;

    /**
     * Фильтр 'C' со значением не равным 'Открыт'.
     */
    final SSMGrid.ConditionConfigurer open_filter = SSMGrid
            .ConditionConfigurer
            .builder()
            .condition1(SSMGrid.Condition.NOT_EQUAL)
            .value1("Открыт")
            .columnCondition("[CСсс]")
            .column("С").build();

    /**
     * Фильтр 'C' со значением не равным 'Закрыт'.
     */
    final SSMGrid.ConditionConfigurer closed_filter = SSMGrid
            .ConditionConfigurer
            .builder()
            .condition1(SSMGrid.Condition.NOT_EQUAL)
            .value1("Закрыт")
            .columnCondition("[CСсс]")
            .column("С").build();

    /**
     * Конструктор.
     * @param root Родительский элемент
     */
    public SSMG0102P0104(CheckerDesktopWindow root) {
        this.root = root;
    }

    /**
     * Запуск.
     */
    @Override
    public void run() {
        SapFilterWindow filter_window = SAPSSM.getFilter();

        log.info("Настройка фильтров");
        filter_window.toggleOpened(true);
        filter_window.selectShop("");
        filter_window.selectShop("КПЦ");
        filter_window.clickOK();
        log.info("Фильтры настроены");

        log.info("Открытие формы 'Заказы SAP'");
        SSMSapOrdersForm orders = this.root.form("mf", SSMSapOrdersForm.class);
        log.info("Форма 'Заказы SAP' успешно запущена");
        SSMGrid orders_grid = orders.getSapOrderGrid();
        log.info("Фильтрация колонки 'C' не равной 'Открыт'");
        orders_grid.filterByGUI(open_filter);
        log.info("Проверка данных");
        SSMGridData data = orders_grid.getDataFromRow(0);
        assertEquals(data.getRowSize(), 0, "Найдены записи не соответствующие условию: не равно 'Открыт'");
        log.info("Данные колонки 'С' соответствуют условию: равно 'Открыт'");
        orders_grid.clearFilter();
        orders.callFilter();

        filter_window.refresh();
        log.info("Настройка фильтров");
        filter_window.toggleOpened(false);
        filter_window.toggleClosed(true);
        filter_window.selectShop("");
        filter_window.selectShop("КПЦ");
        filter_window.clickOK();
        log.info("Фильтры настроены");

        orders_grid.filterByGUI(closed_filter);
        log.info("Проверка данных");
        data = orders_grid.getDataFromRow(0);
        assertEquals(data.getRowSize(), 0, "Найдены записи не соответствующие условию: не равно 'Закрыт'");
        log.info("Данные колонки 'С' соответствуют условию: равно 'Закрыт'");
        log.info("Тестовый случай выполнен");
    }
}
