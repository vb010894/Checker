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
public class SSMG0104P0105 implements Runnable {


    /**
     * Главное окно ССМ.
     */
    final CheckerDesktopWindow root;

    /**
     * Конструктор.
     * @param root Родительский элемент
     */
    public SSMG0104P0105(CheckerDesktopWindow root) {
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

        {
            log.info("Шаг 1");
            filter.toggleNew(true);
            filter.setShopValue("КПЦ");
            filter.toggleMaster(true);
            filter.clickOK();
            filter.checkActivity(false);
        }

        log.info("Тестовый случай завершен");
    }
}
