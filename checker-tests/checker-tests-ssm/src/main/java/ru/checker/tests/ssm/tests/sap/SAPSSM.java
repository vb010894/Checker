package ru.checker.tests.ssm.tests.sap;

import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.DisplayName;
import org.testng.annotations.Test;
import ru.checker.tests.desktop.test.temp.CheckerDesktopTest;
import ru.checker.tests.ssm.test.SSMTest;
import ru.checker.tests.ssm.windows.SapFilterWindow;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

/**
 * SSM 'SAP Orders' form testing.
 * Test case file - SSM_SAP.yaml
 * @author vd.zinovev
 */
@DisplayName("Тесты ССМ. Форма Заказы SAP")
@Log4j2(topic = "TEST CASE")
public class SAPSSM extends SSMTest {

    /**
     * Получение окна фильтрации модуля "Заказы SAP".
     * @return  окна фильтрации модуля "Заказы SAP"
     */
    public static SapFilterWindow getFilter() {
        SapFilterWindow filter_window = CheckerDesktopTest.getCurrentApp().window("SAP_FILTER_FORM", SapFilterWindow.class);
        filter_window.refresh();
        log.info("Ожидание инициализации компонентов окна 'Фильтр'");
        assertDoesNotThrow(() -> Thread.sleep(2000), "Не удалось выполнить ожидание инициализации компонентов окна 'Фильтр'");
        log.info("Компоненты инициализированы.");
        return filter_window;
    }

    /**
     * SSM.G.01.02.P.01. Работа с фильтрами. Настройки по умолчанию
     */
    @Test(
            testName = "SSM.G.01.02.P.01.01",
            description = "SSM.G.01.02.P.01. Работа с фильтрами. Настройки по умолчанию")
    public void SSMG0102P0101() {
        new SSMG0102P0101(getRootWindow()).run();
    }

    /**
     * SSM.G.01.02.P.02. Работа с фильтрами. Фильтр 'ЦЕХ'
     */
    @Test(
            testName = "SSM.G.01.02.P.01.02",
            description = "SSM.G.01.02.P.02. Работа с фильтрами. Фильтр 'ЦЕХ'")
    public void SSMG0102P0102() {
        new SSMG0102P0102(getRootWindow()).run();
    }

    /**
     * SSM.G.01.02.P.02. Работа с фильтрами. Фильтр 'Год'
     */
    @Test(
            testName = "SSM.G.01.02.P.01.03",
            description = "SSM.G.01.02.P.03. Работа с фильтрами. Фильтр 'Год'")
    public void SSMG0102P0103() {
        new SSMG0102P0103(getRootWindow()).run();
    }

    /**
     * SSM.G.01.02.P.04. Работа с фильтрами. Фильтр 'C'
     */
    @Test(
            testName = "SSM.G.01.02.P.01.04",
            description = "SSM.G.01.02.P.01.04. Работа с фильтрами. Фильтр 'C'")
    public void SSMG0102P0104() {
        new SSMG0102P0104(getRootWindow()).run();
    }

    /**
     * SSM.G.01.02.P.04. Работа с фильтрами. Фильтр 'Клиент'
     */
    // TODO: 15.08.2022 Доделать после комментария от менеджеров
    @Test(
            testName = "SSM.G.01.02.P.01.05",
            description = "SSM.G.01.02.P.01.05. Работа с фильтрами. Фильтр 'Клиент'",
    groups = {"broken"})
    public void SSMG0102P0105() {
        new SSMG0102P0105(getRootWindow()).run();
    }

    /**
     * SSM.G.01.02.P.06. Работа с фильтрами. Фильтр 'Заказ Лоцман'
     */
    @Test(
            testName = "SSM.G.01.02.P.01.06",
            description = "SSM.G.01.02.P.01.06. Работа с фильтрами. Фильтр 'Заказ Лоцман'")
    public void SSMG0102P0106() {
        new SSMG0102P0106(getRootWindow()).run();
    }

    /**
     * SSM.G.01.02.P.02. Назначение мастера на заказ.
     */
    @Test(
            testName = "SSM.G.01.02.P.02",
            description = "SSM.G.01.02.P.02. Назначение мастера на заказ",
            groups = {"inWork"})
    public void SSMG0102P02() {
        new SSMG0102P02(getRootWindow()).run();
    }

}
