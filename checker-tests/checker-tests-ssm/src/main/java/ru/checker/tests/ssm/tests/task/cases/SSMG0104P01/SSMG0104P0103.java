package ru.checker.tests.ssm.tests.task.cases.SSMG0104P01;

import lombok.extern.log4j.Log4j2;
import ru.checker.tests.base.test.CheckerConstants;
import ru.checker.tests.desktop.test.entity.CheckerDesktopWindow;
import ru.checker.tests.desktop.test.temp.CheckerDesktopTest;
import ru.checker.tests.ssm.controls.grid.SSMGrid;
import ru.checker.tests.ssm.forms.SSMTaskForm;
import ru.checker.tests.ssm.tests.task.SSMTaskModule;
import ru.checker.tests.ssm.windows.task.TaskFilter;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

/**
 * SSM.G.01.04.P.01.02. Работа с фильтрами. С.
 *
 * @author vd.zinovev
 */
@Log4j2
public class SSMG0104P0103 implements Runnable {


    /**
     * Главное окно ССМ.
     */
    final CheckerDesktopWindow root;

    /**
     * Конструктор.
     * @param root Родительский элемент
     */
    public SSMG0104P0103(CheckerDesktopWindow root) {
        this.root = root;
    }


    /**
     * Run scenario.
     */
    @Override
    public void run() {

        SSMTaskForm form;
        SSMGrid task_grid;

        TaskFilter filter = SSMTaskModule.openFilter();
        {
            log.info("Шаг 1");
            filter.toggleNew(true);
            filter.clickOK();

            form = this.root.form("task_control", SSMTaskForm.class);
            task_grid = form.getTaskGrid();
            task_grid.filter("c_not_new_filter");
            task_grid.hasNotData();
            task_grid.clearFilter();
            filter = form.callTaskFilter();

        }

        {
            log.info("Шаг 2");
            filter.toggleNew(false);
            filter.clickOK();
            task_grid.hasNotData();
            filter = form.callTaskFilter();
        }

        {
            log.info("Шаг 3");
            filter.toggleInWork(true);
            filter.clickOK();
            CheckerDesktopTest.getCurrentApp().waitApp();
            task_grid.filter("c_not_new_filter");
            task_grid.hasNotData();
            task_grid.clearFilter();
            filter = form.callTaskFilter();
        }

        {
            log.info("Шаг 4");
            filter.toggleInWork(false);
            filter.clickOK();
            task_grid.hasNotData();
            filter = form.callTaskFilter();
        }

        {
            log.info("Шаг 5");
            filter.toggleClosed(true);
            filter.setShopValue("КПЦ");
            filter.clickOK();
            task_grid.filter("c_not_closed_filter");
            task_grid.hasNotData();
            task_grid.clearFilter();
        }

        log.info("Тестовый случай завершен");
    }
}
