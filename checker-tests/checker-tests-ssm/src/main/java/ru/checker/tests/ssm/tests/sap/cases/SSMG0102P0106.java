package ru.checker.tests.ssm.tests.sap.cases;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.log4j.Log4j2;
import ru.checker.tests.desktop.test.entity.CheckerDesktopWindow;
import ru.checker.tests.ssm.controls.grid.SSMGrid;
import ru.checker.tests.ssm.forms.SSMSapOrdersForm;
import ru.checker.tests.ssm.tests.sap.SAPSSM;
import ru.checker.tests.ssm.windows.sap.SapFilterWindow;
import ru.checker.tests.ssm.windows.sap.SapLotsmanFilterWindow;

/**
 * ТС.SSM.01. Заказы SAP. Работа с фильтрами.
 * @author vd.zinovev
 */
@Log4j2
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@SuppressWarnings("FieldCanBeLocal")
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
     * Запуск.
     */
    @Override
    public void run() {
        log.info("Шаг 1");
        SapFilterWindow filter_window = SAPSSM.getFilter();

        log.info("Настройка фильтров");
        filter_window.toggleOpened(true);
        filter_window.selectYear("2021");
        SapLotsmanFilterWindow lotsman = filter_window.callLotsmanOrderWindow();
        log.info("Вставка значения {} для проверки пустой таблицы", lotsman_empty_order);
        lotsman.setSearchValue(lotsman_empty_order);
        lotsman.clickSearch();

        SSMGrid lotsman_grid = lotsman.getLotsmanOrderGrid();
        lotsman_grid.getAllData();
        lotsman_grid.hasNotData();

        log.info("Шаг 2");

        lotsman.setSearchValue(lotsman_order);
        lotsman.clickSearch();

        lotsman_grid.getAllData();
        lotsman_grid.columnDataEquals("Заказ", lotsman_order);
        lotsman_grid.selectRow(0);
        lotsman.clickOK();

        filter_window.clickOK();
        log.info("Фильтры настроены");

        log.info("Открытие формы 'Заказы SAP'");
        SSMSapOrdersForm orders = this.root.form("mf", SSMSapOrdersForm.class);
        log.info("Форма 'Заказы SAP' успешно запущена");
        SSMGrid orders_grid = orders.getSapOrderGrid();
        log.info("Фильтрация колонки 'Заказ' по условию: 'Колонка 'Заказ' не равна '{}'", lotsman_order);
        orders_grid.filter("lotsman_order");
        orders_grid.getDataFromRow(0);
        orders_grid.hasNotData();
        orders_grid.clearFilter();

        log.info("Шаг 3");

        orders.callFilter();
        filter_window.refresh();
        lotsman = filter_window.callLotsmanOrderWindow();
        lotsman_grid.getAllData();
        lotsman_grid.hasNotData();
        lotsman.clickOK();

        filter_window.clickOK();

        orders_grid.getDataFromRow(0);
        orders_grid.hasData();

        log.info("Шаг 4");

        orders.callFilter();
        filter_window.refresh();
        lotsman = filter_window.callLotsmanOrderWindow();

        lotsman.setSearchValue("test");
        lotsman.clickCancel();
        filter_window.clickOK();

        orders_grid.getDataFromRow(0);
        orders_grid.hasData();

        log.info("Шаг 5");

        orders.callFilter();
        filter_window.refresh();
        filter_window.clearLotsmanOrder();
        filter_window.clickOK();

        orders_grid.getDataFromRow(0);
        orders_grid.hasData();

        log.info("Тестовый случай выполнен");
    }
}