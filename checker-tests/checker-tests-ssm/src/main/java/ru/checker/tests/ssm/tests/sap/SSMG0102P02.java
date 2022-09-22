package ru.checker.tests.ssm.tests.sap;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.log4j.Log4j2;
import ru.checker.tests.base.test.CheckerConstants;
import ru.checker.tests.desktop.test.entity.CheckerDesktopWindow;
import ru.checker.tests.ssm.controls.grid.SSMGrid;
import ru.checker.tests.ssm.controls.grid.SSMGridData;
import ru.checker.tests.ssm.forms.SSMSapOrdersForm;

import java.util.Map;

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

            log.info("Открытие формы 'Заказы SAP'");
            orders = SAPSSM.getSapOrdersForm(this.root);
            log.info("Форма 'Заказы SAP' успешно запущена");
            orders_grid = orders.getSapOrderGrid();
            orders_grid.filter("SSMG0102P0104_H_filter");
            SSMGridData data = orders_grid.getDataFromRow(0);
            orders_grid.hasData();
            String order_sap_number = data.getColumnData("Заказ SAP").get(0);
            CheckerConstants.saveConstant("SSMG0102P0104_SAP_order", order_sap_number);
            orders_grid.filter("SSMG0102P0104_SAP_order_filter");
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
            Map<String, String> values = Map.of(
                    "Мастер", master_fio,
                    "Дата начала", start_from,
                    "Дата окончания", start_to,
                    "Операция", operation
            );
            SAPSSM.checkPrbOrder(orders, values);
        }

        log.debug("Тестовый случай выполнен");
    }

}
