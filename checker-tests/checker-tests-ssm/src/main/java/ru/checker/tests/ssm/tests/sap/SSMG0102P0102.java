package ru.checker.tests.ssm.tests.sap;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.log4j.Log4j2;
import ru.checker.tests.desktop.base.robot.CheckerDesktopMarker;
import ru.checker.tests.desktop.test.entity.CheckerDesktopWindow;
import ru.checker.tests.desktop.test.temp.CheckerDesktopTest;
import ru.checker.tests.ssm.controls.grid.SSMGrid;
import ru.checker.tests.ssm.controls.grid.SSMGridData;
import ru.checker.tests.ssm.temp.forms.SSMSapOrdersForm;
import ru.checker.tests.ssm.temp.windows.SapFilterWindow;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * ТС.SSM.01. Заказы SAP. Работа с фильтрами.
 * @author vd.zinovev
 */
@Log4j2
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SSMG0102P0102 implements Runnable {

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
    public SSMG0102P0102(CheckerDesktopWindow root) {
        this.root = root;
    }

    /**
     * Фильтр 'C' со значением 'Открыт'.
     */
    final SSMGrid.ConditionConfigurer.ConditionConfigurerBuilder shop_filter = SSMGrid
            .ConditionConfigurer
            .builder()
            .condition1(SSMGrid.Condition.NOT_EQUAL)
            .column("Цех");
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

    private final List<String> SHOPS = List.of("РМЦ-1", "ЦРМО-1", "КПЦ");

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
            orders_grid.filterByGUI(shop_filter.value1(shop).build());
            SSMGridData data = orders_grid.getDataFromRow(0);
            assertEquals(
                    data.getRowSize(),
                    0,
                    "В таблице 'Прозводственные заказы SAP' найдены записи" +
                            " со значением отличающейся от '" + shop + "'");
            orders_grid.clearFilter();
            orders.callFilter();
            filter_window.refresh();
        });

        filter_window.clickCancel();

    }

}
