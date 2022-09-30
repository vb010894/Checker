package ru.checker.tests.ssm.tests.task.cases;

import lombok.extern.log4j.Log4j2;
import ru.checker.tests.base.test.CheckerConstants;
import ru.checker.tests.desktop.test.entity.CheckerDesktopWindow;
import ru.checker.tests.ssm.controls.grid.SSMGrid;
import ru.checker.tests.ssm.forms.SSMTaskForm;
import ru.checker.tests.ssm.tests.task.SSMTaskModule;
import ru.checker.tests.ssm.windows.task.TaskFilter;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

/**
 * SSM.G.01.04.P.01. Работа с фильтрами. Цех.
 *
 * @author vd.zinovev
 */
@Log4j2
public class SSMG0104P0102 implements Runnable {


    /**
     * Главное окно ССМ.
     */
    final CheckerDesktopWindow root;

    /**
     * Конструктор.
     * @param root Родительский элемент
     */
    public SSMG0104P0102(CheckerDesktopWindow root) {
        this.root = root;
    }


    /**
     * Run scenario.
     */
    @Override
    public void run() {
        List<String> shops = List.of("КПЦ", "РМЦ-1");
        AtomicInteger step = new AtomicInteger(1);

        AtomicReference<TaskFilter> filter = new AtomicReference<>(SSMTaskModule.openFilter());
        shops.forEach(shop -> {
            log.info("Шаг " + step.getAndIncrement());

            filter.get().setShopValue(shop);
            filter.get().toggleNew(true);
            filter.get().clickOK();

            SSMTaskForm form = this.root.form("task_control", SSMTaskForm.class);
            SSMGrid task_grid = form.getTaskGrid();
            CheckerConstants.saveConstant("shop", shop);
            task_grid.filter("shop_filter");
            task_grid.getDataByRow(0);
            task_grid.hasNotData();
            task_grid.clearFilter();

            filter.set(form.callTaskFilter());
        });


        log.info("Тестовый случай завершен");
    }
}
