package ru.checker.tests.ssm.tests.sap.cases;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.log4j.Log4j2;
import ru.checker.tests.base.test.CheckerConstants;
import ru.checker.tests.desktop.test.entity.CheckerDesktopWindow;
import ru.checker.tests.ssm.controls.grid.SSMGrid;
import ru.checker.tests.ssm.controls.grid.SSMGridData;
import ru.checker.tests.ssm.forms.SSMSapOrdersForm;
import ru.checker.tests.ssm.tests.sap.SAPSSM;
import ru.checker.tests.ssm.windows.sap.SapPRBCreationWindow;
import ru.checker.tests.ssm.windows.sap.SapPRBMasterCreationWindow;

import java.util.Map;

/**
 * SSM.G.01.02.P.02. Назначение мастера на заказ.
 *
 * @author vd.zinovev
 */
@Log4j2
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class SSMG0102P04 implements Runnable{

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
    SSMGrid.ConditionConfigurer count_filter = SSMGrid
            .ConditionConfigurer
            .builder()
            .condition1(SSMGrid.Condition.MORE_THEN)
            .column("Кол-во")
            .value1("0").build();

    /**
     * Фильтр колонки 'Заказ SAP' со значениями полученным в ходе тестирования.
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
    public SSMG0102P04(CheckerDesktopWindow root) {
        this.root = root;
    }

    /**
     * Запуск.
     */
    @Override
    public void run() {
        SSMGrid orders_grid;
        SSMSapOrdersForm orders;
        SapPRBCreationWindow prb;
        SapPRBMasterCreationWindow choose_master;
        SSMGridData operation_data;
        String master_fio;
        String master_tab;
        String operation;
        String start_from;
        String start_to;

        {
            log.info("Шаг 1");
            orders = SAPSSM.getSapOrdersForm(this.root);
            orders_grid = orders.getSapOrderGrid();
            orders_grid.filter("SSMG0102P0104_H_filter");
            orders_grid.getDataFromRow(0);
            orders_grid.hasData();

            orders_grid.filter("order_count_more_filter");
            orders_grid.hasData();
            String sap_orders = orders_grid.getDataFromRow(0).getColumnData("Заказ SAP").get(0);

            CheckerConstants.saveConstant("SSMG0102P0104_SAP_order", sap_orders);
            orders_grid.filter("SSMG0102P0104_SAP_order_filter");
            orders_grid.hasData();
        }

        {
            log.info("Шаг 2");
            orders_grid.selectRowAndCheckSelection(0);
            orders_grid.hasData();
        }

        {
            log.info("Шаг 3");
            SSMGrid operation_grid = orders.getOperationGrid();
            operation_data = operation_grid.selectRowAndCheckSelection(0);
            operation_grid.hasData();
            operation = operation_data.getColumnData("Операция").get(0);
            start_from = operation_data.getColumnData("Дата начала").get(0);
            start_to = operation_data.getColumnData("Дата окончания").get(0);
        }

        {
            log.info("Шаг 4");
            prb = orders.clickAdd();
        }

        {
            log.info("Шаг 5");
            choose_master = prb.callAddMaster();
        }

        {
            log.info("Шаг 6");
            SSMGrid prb_master = choose_master.getMasterGrid();
            log.info("Получение информации о мастере в окне 'Выбор Мастеров'");
            SSMGridData prb_master_data = prb_master.getDataByRow(0, true);
            master_tab = prb_master_data.getColumnData("Таб.").get(0);
            master_fio = prb_master_data.getColumnData("Фамилия И.О.").get(0);
            log.info("Информация о мастере в окне 'Выбор Мастеров' получена. Таб. номер - '{}', ФИО - '{}'", master_tab, master_fio);
        }

        {
            log.info("Шаг 7");
            choose_master.clickChoose();
            SSMGrid prb_master = prb.getMasterGrid();
            prb_master.getDataFromRow(0);
            prb_master.columnDataEquals("Таб.", master_tab);
            prb_master.columnDataEquals("Фамилия И.О.", master_fio);
        }

        {
            log.info("Шаг 8");
            prb.setPlanCountValue("1");
            prb.clickOK();
            Map<String, String> values = Map.of(
                    "Мастер", master_fio,
                    "Дата начала", start_from,
                    "Дата окончания", start_to,
                    "Операция", operation
            );
            SAPSSM.checkPrbOrder(orders, values);
            SSMGrid operation_grid = orders.getOperationGrid();
            operation_grid.selectRowAndCheckAssigned(0);
        }

        log.debug("Тестовый случай выполнен");
    }

}
