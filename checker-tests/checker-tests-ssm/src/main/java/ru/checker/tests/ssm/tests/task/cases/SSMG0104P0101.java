package ru.checker.tests.ssm.tests.task.cases;

import lombok.extern.log4j.Log4j2;
import ru.checker.tests.desktop.test.entity.CheckerDesktopWindow;
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

        {
            log.info("Шаг 1");
            TaskFilter filter = SSMTaskModule.openFilter();
            filter.setYearsFromValue("2021");
            filter.setYearsToValue("2021");
            filter.toggleNew(true);
            filter.clickOK();
        }

        {
            log.info("Шаг 2");
            TaskFilter filter = SSMTaskModule.openFilter();
            filter.setYearsFromValue("2021");
            filter.setYearsToValue("2021");
            filter.toggleNew(true);
            filter.clickOK();
        }

        
        System.out.println("a");
    }
}
