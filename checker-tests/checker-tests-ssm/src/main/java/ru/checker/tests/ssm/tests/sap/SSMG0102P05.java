package ru.checker.tests.ssm.tests.sap;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.log4j.Log4j2;
import ru.checker.tests.desktop.test.entity.CheckerDesktopWindow;
import ru.checker.tests.desktop.utils.CheckerDesktopManipulator;
import ru.checker.tests.ssm.controls.grid.SSMGrid;
import ru.checker.tests.ssm.forms.SSMSapOrdersForm;

import java.util.Locale;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * SSM.G.01.02.P.05. Поиск заказов SAP в структуре заказа Лоцман.
 *
 * @author vd.zinovev
 */
@Log4j2
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class SSMG0102P05 implements Runnable {

    /**
     * Главное окно ССМ.
     */
    CheckerDesktopWindow root;


    /**
     * Конструктор.
     *
     * @param root Родительский элемент
     */
    public SSMG0102P05(CheckerDesktopWindow root) {
        this.root = root;
    }

    /**
     * Запуск.
     */
    @Override
    public void run() {
        SSMGrid orders_grid;
        SSMSapOrdersForm orders;

        {
            log.info("Шаг 1");
            orders = SAPSSM.getSapOrdersForm(this.root);
            orders_grid = orders.getSapOrderGrid();
            orders_grid.getDataFromRow(0);
            orders_grid.hasData();
        }

        {
            log.info("Шаг 2");
            orders_grid.moveToCell("Заказ");
            CheckerDesktopManipulator.Keyboard.sendKeys("ENTER");
            assertEquals(
                    orders.getActiveTab().trim().toLowerCase(Locale.ROOT),
                    "заказ лоцман",
                    "Вкладка 'ЗАКАЗ ЛОЦМАН' формы 'Заказы SAP' не активна");
            SSMGrid lotsman_grid = orders.getLotsmanOrderGrid();
            lotsman_grid.getDataFromRow(0);
            lotsman_grid.hasData();
        }

        log.info("Тестовый случай выполнен");
    }

}
