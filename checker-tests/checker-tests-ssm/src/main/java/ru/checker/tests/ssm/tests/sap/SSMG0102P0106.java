package ru.checker.tests.ssm.tests.sap;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.log4j.Log4j2;
import ru.checker.tests.desktop.test.entity.CheckerDesktopWindow;
import ru.checker.tests.desktop.test.temp.CheckerDesktopTest;
import ru.checker.tests.ssm.controls.grid.SSMGrid;
import ru.checker.tests.ssm.temp.forms.SSMSapOrdersForm;
import ru.checker.tests.ssm.temp.windows.SapFilterWindow;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

/**
 * ТС.SSM.01. Заказы SAP. Работа с фильтрами.
 * @author vd.zinovev
 */
@Log4j2
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class SSMG0102P0106 implements Runnable {

    /**
     * Заказ Лоцмана, при котором таблица будет пуста.
     */
    String lotsman_empty_order = "3-06-9330/0";

    /**
     * Заказ Лоцмана для теста с непустыми значениями
     */
    String lotsman_order = "5-148-2022/0";

    /**
     * Главное окно ССМ.
     */
    final CheckerDesktopWindow root;

    /**
     * Конструктор.
     * @param root Родительский элемент
     */
    public SSMG0102P0106(CheckerDesktopWindow root) {
        this.root = root;
    }


    /**
     * Фильтр 'C' со значением 'Открыт'.
     */
    final SSMGrid.ConditionConfigurer open_filter = SSMGrid
            .ConditionConfigurer
            .builder()
            .condition1(SSMGrid.Condition.NOT_EQUAL)
            .value1("Открыт")
            .columnCondition("[CСсс]")
            .column("С").build();

    /**
     * Запуск.
     */
    @Override
    public void run() {
        SapFilterWindow filter_window = CheckerDesktopTest.getCurrentApp().window("SAP_FILTER_FORM", SapFilterWindow.class);
        filter_window.refresh();
        log.info("Ожидание инициализации компонентов окна 'Фильтр'");
        assertDoesNotThrow(() -> Thread.sleep(2000), "Не удалось выполнить ожидание инициализации компонентов окна 'Фильтр'");
        log.info("Компоненты инициализированы.");

        log.info("Настройка фильтров");
        filter_window.toggleOpened(true);
        filter_window.selectYear("2021");
        filter_window.setClient("");
        filter_window.setClient("ККЦ");
        filter_window.clickOK();
        log.info("Фильтры настроены");

        log.info("Открытие формы 'Заказы SAP'");
        SSMSapOrdersForm orders = this.root.form("mf", SSMSapOrdersForm.class);
        log.info("Форма 'Заказы SAP' успешно запущена");
        SSMGrid orders_grid = orders.getSapOrderGrid();
       /* log.info("Фильтрация колонки 'C' не равной 'Открыт'");
        orders_grid.filterByGUI(open_filter);
        log.info("Проверка данных");
        SSMGridData data = orders_grid.getDataFromRow(0);
        assertEquals(data.getRowSize(), 0, "Найдены записи не соответствующие условию: не равно 'Открыт'");
        log.info("Данные колонки 'С' соответствуют условию: равно 'Открыт'");
        orders_grid.clearFilter();*/
        orders.callFilter();

        filter_window.refresh();
        log.info("Настройка фильтров");
        filter_window.toggleOpened(true);
        filter_window.clearClient();
        filter_window.clickOK();
        log.info("Фильтры настроены");

        log.info("Тестовый случай выполнен");
    }
}
