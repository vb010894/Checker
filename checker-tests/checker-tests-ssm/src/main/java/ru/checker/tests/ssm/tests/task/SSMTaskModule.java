package ru.checker.tests.ssm.tests.task;

import lombok.extern.log4j.Log4j2;
import org.testng.annotations.Test;
import ru.checker.tests.desktop.test.temp.CheckerDesktopTest;
import ru.checker.tests.ssm.test.SSMTest;
import ru.checker.tests.ssm.tests.task.cases.SSMG0104P0101;
import ru.checker.tests.ssm.tests.task.cases.SSMG0104P0102;
import ru.checker.tests.ssm.windows.task.TaskFilter;

/**
 * Тесты модуля "Управление заданиями".
 *
 * @author vd.zinovev
 */
@Log4j2
public class SSMTaskModule extends SSMTest {

    /**
     * Получает окно фильтра.
     * @return Окно фильтра
     */
    public static TaskFilter openFilter() {
        log.info("Инициализация фильтра модуля 'Управление заданиями'");
        TaskFilter window = CheckerDesktopTest.getCurrentApp().window("TASK_FILTER_WINDOW", TaskFilter.class);
        log.info("Окно фильтра инициализированно");
        return window;
    }

    @Test(
            testName = "SSM.G.01.04.P.01.01",
            description = "SSM.G.01.04.P.01. Работа с фильтрами. Даты")
    public void SSMG0104P0101() {
        new SSMG0104P0101(getRootWindow()).run();

    }

    @Test(
            testName = "SSM.G.01.04.P.01.02",
            description = "SSM.G.01.04.P.02. Работа с фильтрами. Цех",
            groups = "broken")
    public void SSMG0104P0102() {
        new SSMG0104P0102(getRootWindow()).run();

    }

}
