package ru.checker.tests.ssm.tests.sap;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.log4j.Log4j2;
import ru.checker.tests.desktop.test.entity.CheckerDesktopWindow;
import ru.checker.tests.ssm.controls.grid.SSMGrid;
import ru.checker.tests.ssm.controls.grid.SSMGridData;
import ru.checker.tests.ssm.forms.SSMSapOrdersForm;
import ru.checker.tests.ssm.windows.SapFilterWindow;

/**
 * SSM.G.01.02.P.02. Назначение мастера на заказ.
 *
 * @author vd.zinovev
 */
@Log4j2
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class SSMG0102P02 implements Runnable{

    /**
     * Главное окно ССМ.
     */
    CheckerDesktopWindow root;

    /**
     * Фильтр колонки 'ДеБлок' со значениями в диапазоне от 01.01.{Текущий год} и 31.12.{Текущий год}.
     */
    SSMGrid.ConditionConfigurer H_filter = SSMGrid
            .ConditionConfigurer
            .builder()
            .condition1(SSMGrid.Condition.EQUAL)
            .value1("0")
            .column("Н")
            .columnCondition("\\d[HнН]|[HнН]").build();

    /**
     * Фильтр колонки 'ДеБлок' со значениями в диапазоне от 01.01.{Текущий год} и 31.12.{Текущий год}.
     */
    SSMGrid.ConditionConfigurer.ConditionConfigurerBuilder order_sap_filter = SSMGrid
            .ConditionConfigurer
            .builder()
            .condition1(SSMGrid.Condition.EQUAL)
            .column("Заказ SAP");

    /**
     * Конструктор.
     * @param root Родительский элемент
     */
    public SSMG0102P02(CheckerDesktopWindow root) {
        this.root = root;
    }

    /**
     * Запуск.
     */
    @Override
    public void run() {
        SSMGrid orders_grid;
        SSMSapOrdersForm orders;
        String master_tab;
        String master_fio;
        String operation;
        String start_from;
        String start_to;

        {
            log.info("Шаг 1");
            SapFilterWindow filter_window = SAPSSM.getFilter();

            log.info("Настройка фильтров окна 'Фильтр' модуля 'Заказы SAP'");
            filter_window.toggleOpened(true);
            filter_window.clickOK();
            log.info("Фильтры настроены");

            log.info("Открытие формы 'Заказы SAP'");
            orders = this.root.form("mf", SSMSapOrdersForm.class);
            log.info("Форма 'Заказы SAP' успешно запущена");
            orders_grid = orders.getSapOrderGrid();
            orders_grid.filterByGUI(H_filter);
            SSMGridData data = orders_grid.getDataFromRow(0);
            orders_grid.hasData();
            String order_sap_number = data.getColumnData("Заказ SAP").get(0);
            orders_grid.filterByGUI(order_sap_filter.value1(order_sap_number).build());
            orders_grid.getDataFromRow(0);
            orders_grid.hasData();
        }

        {
            log.info("Шаг 2");
            SSMGrid master_grid = orders.getMasterGrid();
            SSMGridData master_data = master_grid.selectAndAcceptCell(0);
            master_grid.hasData();
            master_tab = master_data.getColumnData("Таб.").get(0);
            master_fio = master_data.getColumnData("Фамилия И.О.").get(0);
            log.info("Выбран мастер {}.{}", master_tab, master_fio);
        }

        {
            log.info("Шаг 3");
            SSMGrid operation_grid = orders.getOperationGrid();
            SSMGridData operation_data = operation_grid.selectAndAcceptCell(0);
            operation_grid.hasData();
            operation = operation_data.getColumnData("Операция").get(0);
            start_from = operation_data.getColumnData("Дата начала").get(0);
            start_to = operation_data.getColumnData("Дата окончания").get(0);
            orders.clickAssign();
        }

        {
            log.info("Шаг 4");
            SSMGrid release_grid = orders.getProductionReleaseGrid();
            release_grid.selectTab("ПРБ");
            SSMGrid prb_grid = orders.getOrderPRBGrid();
            prb_grid.getAllData();
            prb_grid.containsData("Мастер", master_fio);
            prb_grid.containsData("Дата начала", start_from);
            prb_grid.containsData("Дата окончания", start_to);
            prb_grid.containsData("Операция", operation);
        }

        log.debug("Тестовый случай выполнен");
    }

}
