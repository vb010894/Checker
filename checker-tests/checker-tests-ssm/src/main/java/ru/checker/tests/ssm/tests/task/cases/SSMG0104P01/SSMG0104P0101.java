package ru.checker.tests.ssm.tests.task.cases.SSMG0104P01;

import lombok.extern.log4j.Log4j2;
import ru.checker.tests.desktop.test.entity.CheckerDesktopWindow;
import ru.checker.tests.ssm.controls.grid.SSMGrid;
import ru.checker.tests.ssm.forms.SSMTaskForm;
import ru.checker.tests.ssm.tests.task.SSMTaskModule;
import ru.checker.tests.ssm.windows.task.TaskFilter;

/**
 * SSM.G.01.04.P.01. Работа с фильтрами. Даты.
 *
 * @author vd.zinovev
 */
@Log4j2
public class SSMG0104P0101 implements Runnable {


    /**
     * Главное окно ССМ.
     */
    final CheckerDesktopWindow root;

    /**
     * Конструктор.
     * @param root Родительский элемент
     */
    public SSMG0104P0101(CheckerDesktopWindow root) {
        this.root = root;
    }


    /**
     * Run scenario.
     */
    @Override
    public void run() {
        TaskFilter filter;
        {
            log.info("Шаг 1");
            filter = SSMTaskModule.openFilter();
            filter.setYearsFromValue("2021");
            filter.setYearsToValue("2021");
            filter.toggleNew(true);
            filter.clickOK();
        }

        {
            log.info("Шаг 2");
            SSMTaskForm form = this.root.form("task_control", SSMTaskForm.class);
            SSMGrid task_grid = form.getTaskGrid();
            task_grid.filter("date_start_diapason");
            task_grid.getDataByRow(0);
            task_grid.hasNotData();
            task_grid.clearFilter();
        }

        log.info("Тестовый случай завершен");
    }
}
