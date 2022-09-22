package ru.checker.tests.ssm.tests.sap;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.log4j.Log4j2;
import ru.checker.tests.desktop.test.entity.CheckerDesktopWindow;
import ru.checker.tests.ssm.controls.grid.SSMGrid;
import ru.checker.tests.ssm.controls.grid.SSMGridData;
import ru.checker.tests.ssm.forms.SSMSapOrdersForm;
import ru.checker.tests.ssm.windows.sap.SapFilterWindow;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * SSM.G.01.02.P.02. Работа с фильтрами. Фильтр 'ЦЕХ'
 * @author vd.zinovev
 */
@Log4j2
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class SSMG0102P0102 implements Runnable {

    /**
     * Главное окно ССМ.
     */
    final CheckerDesktopWindow root;


    /**
     * Фильтр 'Цех' со значением тестируемого цеха.
     * @see SSMG0102P0102#SHOPS
     *
     */
    SSMGrid.ConditionConfigurer.ConditionConfigurerBuilder shop_filter = SSMGrid
            .ConditionConfigurer
            .builder()
            .condition1(SSMGrid.Condition.NOT_EQUAL)
            .column("Цех");

    /**
     * Проверяемые цеха.
     */
    List<String> SHOPS = List.of("РМЦ-1", "ЦРМО-1", "КПЦ");

    /**
     * Конструктор.
     * @param root Родительский элемент
     */
    public SSMG0102P0102(CheckerDesktopWindow root) {
        this.root = root;
    }

    /**
     * Запуск.
     */
    @Override
    public void run() {
        SapFilterWindow filter_window = SAPSSM.getFilter();

        SHOPS.forEach(shop -> {
            log.info("Проверка фильтров с включенным переключателем 'Открытые'");
            log.info("Проверка фильтров со значением '{}' поля 'Цех'", shop);
            filter_window.toggleOpened(true);
            filter_window.selectShop("");
            filter_window.selectShop(shop);
            filter_window.clickOK();
            log.info("Фильтры настроены");

            log.info("Открытие формы 'Заказы SAP'");
            SSMSapOrdersForm orders = this.root.form("mf", SSMSapOrdersForm.class);
            log.info("Форма 'Заказы SAP' успешно запущена");
            SSMGrid orders_grid = orders.getSapOrderGrid();
            log.info("Проверка таблицы 'Прозводственные заказы SAP'");
            SSMGrid.ConditionConfigurer filter = orders_grid.getFilterConfig("shop_filter");
            filter.setValue1(shop);
            orders_grid.filter(filter);
            orders_grid.getDataByRow(0);
            orders_grid.hasNotData();
            orders_grid.clearFilter();
            orders.callFilter();
            filter_window.refresh();
        });
        filter_window.clickCancel();
    }
}