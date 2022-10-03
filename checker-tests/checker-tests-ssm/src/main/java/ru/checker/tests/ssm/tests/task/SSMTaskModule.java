package ru.checker.tests.ssm.tests.task;

import lombok.extern.log4j.Log4j2;
import org.testng.annotations.Test;
import ru.checker.tests.desktop.test.temp.CheckerDesktopTest;
import ru.checker.tests.ssm.test.SSMTest;
import ru.checker.tests.ssm.tests.task.cases.SSMG0104P01.*;
import ru.checker.tests.ssm.tests.task.cases.SSMG0104P02;
import ru.checker.tests.ssm.tests.task.cases.SSMG0104P03;
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
            description = "SSM.G.01.04.P.01.01 Работа с фильтрами. Даты")
    public void SSMG0104P0101() {
        new SSMG0104P0101(getRootWindow()).run();

    }

    @Test(
            testName = "SSM.G.01.04.P.01.01.02",
            description = "SSM.G.01.04.P.01.02. Работа с фильтрами. Цех",
            groups = "broken")
    public void SSMG0104P0102() {
        new SSMG0104P0102(getRootWindow()).run();

    }

    @Test(
            testName = "SSM.G.01.04.P.01.01.03",
            description = "SSM.G.01.04.P.01.03. Работа с фильтрами. C")
    public void SSMG0104P0103() {
        new SSMG0104P0103(getRootWindow()).run();

    }

    @Test(
            testName = "SSM.G.01.04.P.01.01.04",
            description = "SSM.G.01.04.P.01.04. Работа с фильтрами. Заказ Лоцман")
    public void SSMG0104P0104() {
        new SSMG0104P0104(getRootWindow()).run();

    }

    @Test(
            testName = "SSM.G.01.04.P.01.01.05",
            description = "SSM.G.01.04.P.01.05. Работа с фильтрами. Мастер",
    groups = "inWork")
    public void SSMG0104P0105() {
        new SSMG0104P0105(getRootWindow()).run();

    }

    @Test(
            testName = "SSM.G.01.04.P.01.02",
            description = "SSM.G.01.04.P.02. Подтверждения. Работа с фильтрами",
            groups = "broken")
    public void SSMG0104P02() {
        new SSMG0104P02(getRootWindow()).run();
    }

    @Test(
            testName = "SSM.G.01.04.P.01.03",
            description = "SSM.G.01.04.P.03. Назначение исполнителя")
    public void SSMG0104P03() {
        new SSMG0104P03(getRootWindow()).run();
    }

}
