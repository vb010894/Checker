package ru.checker.tests.ssm.tests.sap.cases;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.log4j.Log4j2;
import ru.checker.tests.desktop.test.entity.CheckerDesktopWindow;
import ru.checker.tests.ssm.controls.grid.SSMGrid;
import ru.checker.tests.ssm.forms.SSMSapOrdersForm;
import ru.checker.tests.ssm.tests.sap.SAPSSM;
import ru.checker.tests.ssm.windows.sap.SapFilterWindow;

import java.text.SimpleDateFormat;
import java.util.Date;

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
     * Запуск.
     */
    @Override
    public void run() {
        SapFilterWindow filter_window = SAPSSM.getFilter();
        SSMSapOrdersForm orders;
        SSMGrid orders_grid;

        {
            log.info("Шаг 1");
            log.info("Проверка фильтров с выключенным переключателем 'Открытые' и нажатием кнопки 'OK'");
            filter_window.toggleOpened(false);
            filter_window.clickOK();
            log.info("Открытие формы 'Заказы SAP'");

            orders = this.root.form("mf", SSMSapOrdersForm.class);
            log.info("Форма 'Заказы SAP' успешно запущена");
            orders_grid = orders.getSapOrderGrid();
            log.info("Проверка таблицы 'Прозводственные заказы SAP'");
            orders_grid.getDataByRow(0);
            orders_grid.hasNotData();
            log.info("В таблице 'Прозводственные заказы SAP' отсутствуют записи");
        }

        {
            log.info("Шаг 2");
            orders.callFilter();
            filter_window.refresh();
            filter_window.clickCancel();
            log.info("Проверка таблицы 'Прозводственные заказы SAP'");
            orders_grid.getDataByRow(0);
            orders_grid.hasNotData();
            log.info("В таблице 'Прозводственные заказы SAP' отсутствуют записи");
        }

        {
            log.info("Шаг 3");
            orders.callFilter();
            filter_window.toggleOpened(true);
            filter_window.refresh();
            filter_window.clickOK();
            orders_grid.filter("close_filter");
            orders_grid.getDataByRow(0);
            orders_grid.hasNotData();
            log.info("В таблице 'Прозводственные заказы SAP' отсутствуют записи со значением отличным от 'Открыт'");
            orders_grid.clearFilter();
            log.info("Проверка столбца 'Год' на наличие данных меньших чем текущий год");

            SSMGrid.ConditionConfigurer conf = orders_grid.getFilterConfig("year_filter");
            conf.setValue1("01.01." + new SimpleDateFormat("yyyy").format(new Date()));
            orders_grid.filter(conf);
            orders_grid.getDataByRow(0);
            orders_grid.hasNotData();
            orders_grid.clearFilter();
            log.info("В таблице 'Прозводственные заказы SAP' отсутствуют записи со значением отличным от 'Открыт'");
        }

        log.info("Тестовый случай отрешался успешно");
    }
}
