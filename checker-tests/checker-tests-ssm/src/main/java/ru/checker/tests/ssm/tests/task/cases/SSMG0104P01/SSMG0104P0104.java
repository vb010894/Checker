package ru.checker.tests.ssm.tests.task.cases.SSMG0104P01;

import lombok.extern.log4j.Log4j2;
import ru.checker.tests.base.test.CheckerConstants;
import ru.checker.tests.desktop.test.entity.CheckerDesktopWindow;
import ru.checker.tests.ssm.controls.grid.SSMGrid;
import ru.checker.tests.ssm.forms.SSMTaskForm;
import ru.checker.tests.ssm.tests.task.SSMTaskModule;
import ru.checker.tests.ssm.windows.sap.SapLotsmanFilterWindow;
import ru.checker.tests.ssm.windows.task.TaskFilter;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * SSM.G.01.04.P.01.04. Работа с фильтрами. Заказ Лоцман.
 *
 * @author vd.zinovev
 */
@Log4j2
public class SSMG0104P0104 implements Runnable {


    /**
     * Главное окно ССМ.
     */
    final CheckerDesktopWindow root;

    /**
     * Конструктор.
     * @param root Родительский элемент
     */
    public SSMG0104P0104(CheckerDesktopWindow root) {
        this.root = root;
    }


    /**
     * Run scenario.
     */
    @Override
    public void run() {

        SSMTaskForm form;
        SSMGrid task_grid;
        SSMGrid lotsman_grid;
        String empty_lotsman_order = "3-06-9330/00";
        String lotsman_order_with_data = "5-148-2022/0";

        TaskFilter filter = SSMTaskModule.openFilter();
        SapLotsmanFilterWindow lotsman;
        {
            log.info("Шаг 1");
            lotsman = filter.callLotsmanOrderWindow();
            lotsman.setSearchValue(empty_lotsman_order);
            lotsman.clickSearch();
            lotsman_grid = lotsman.getLotsmanOrderGrid();
            lotsman_grid.hasNotData();
        }

        {
            log.info("Шаг 2");
            lotsman.setSearchValue(lotsman_order_with_data);
            lotsman.clickSearch();
            lotsman_grid = lotsman.getLotsmanOrderGrid();
            lotsman_grid.getAllData();
            lotsman_grid.columnDataEquals("Заказ", lotsman_order_with_data);
            lotsman.clickOK();
            lotsman.checkActivity(false);
            assertEquals(
                    filter.getLotsmanOrderValue(),
                    lotsman_order_with_data,
                    "Значение поля 'Заказ Лоцман' не соответствует ожидаемому");
            filter.toggleNew(true);
            filter.clickOK();
        }

        {
            log.info("Шаг 2");
            form = this.root.form("task_control", SSMTaskForm.class);
            task_grid = form.getTaskGrid();
            CheckerConstants.saveConstant("order_lotsman", lotsman_order_with_data);
            task_grid.filter("lotsman_not_equal");
            task_grid.hasNotData();
        }

        {
            log.info("Шаг 3");
            filter = form.callTaskFilter();
            lotsman = filter.callLotsmanOrderWindow();
            lotsman.setSearchValue("test");
            lotsman.clickSearch();
            lotsman.clickCancel();
            lotsman.checkActivity(false);
            filter.clickOK();
            task_grid.hasData();

        }

        {
            log.info("Шаг 4");
            filter = form.callTaskFilter();
            filter.clearLotsmanOrder();
            filter.clickOK();
            task_grid.hasData();
            task_grid.clearFilter();
        }

        form.callTaskFilter();

        log.info("Тестовый случай завершен");
    }
}
