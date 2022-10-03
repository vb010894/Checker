package ru.checker.tests.ssm.tests.task.cases;

import lombok.extern.log4j.Log4j2;
import ru.checker.tests.desktop.test.entity.CheckerDesktopWindow;
import ru.checker.tests.desktop.utils.CheckerDesktopMarker;
import ru.checker.tests.ssm.controls.grid.SSMGrid;
import ru.checker.tests.ssm.forms.SSMTaskForm;
import ru.checker.tests.ssm.tests.task.SSMTaskModule;
import ru.checker.tests.ssm.windows.task.TaskFilter;

/**
 * SSM.G.01.04.P.01.04. Работа с фильтрами. Заказ Лоцман.
 *
 * @author vd.zinovev
 */
@Log4j2
public class SSMG0104P03 implements Runnable {


    /**
     * Главное окно ССМ.
     */
    final CheckerDesktopWindow root;

    /**
     * Конструктор.
     * @param root Родительский элемент
     */
    public SSMG0104P03(CheckerDesktopWindow root) {
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

        TaskFilter filter = SSMTaskModule.openFilter();

        {
            log.info("Шаг 1");
            filter.toggleNew(true);
            filter.setShopValue("КПЦ");
            filter.clickOK();
            filter.checkActivity(false);
            form = this.root.form("task_control", SSMTaskForm.class);
            task_grid = form.getTaskGrid();
            SSMGrid staff = form.getPRBGrid();
            new CheckerDesktopMarker(staff.getControl()).draw();
        }

        log.info("Тестовый случай завершен");
    }
}
