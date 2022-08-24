package ru.checker.tests.ssm.tests.sap;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.log4j.Log4j2;
import ru.checker.tests.desktop.test.entity.CheckerDesktopWindow;
import ru.checker.tests.ssm.controls.grid.SSMGrid;
import ru.checker.tests.ssm.controls.grid.SSMGridData;
import ru.checker.tests.ssm.forms.SSMSapOrdersForm;
import ru.checker.tests.ssm.windows.SapFilterWindow;

import java.text.SimpleDateFormat;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

/**
 * SSM.G.01.02.P.02. Назначение мастера на заказ.
 *
 * @author vd.zinovev
 */
@Log4j2
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class SSMG0102P02 implements Runnable{

    /**
     * Главное окно ССМ.
     */
    CheckerDesktopWindow root;

    /**
     * Фильтр колонки 'ДеБлок' со значениями в диапазоне от 01.01.{Текущий год} и 31.12.{Текущий год}.
     */
    SSMGrid.ConditionConfigurer year_filter = SSMGrid
            .ConditionConfigurer
            .builder()
            .condition1(SSMGrid.Condition.LESS_THEN)
            .value1("01.01." + new SimpleDateFormat("yyyy").format(new Date()))
            .separator(SSMGrid.Separator.AND)
            .condition2(SSMGrid.Condition.MORE_THEN)
            .value2("31.12." + new SimpleDateFormat("yyyy").format(new Date()))
            .column("ДеБлок")
            .build();

    /**
     * Конструктор.
     * @param root Родительский элемент
     */
    public SSMG0102P02(CheckerDesktopWindow root) {
        this.root = root;
    }

    /**
     * Запуск.
     */
    @Override
    public void run() {
        SSMSapOrdersForm orders;
        String master_tab;
        String master_fio;

        {
            log.info("Шаг 1");
            SapFilterWindow filter_window = SAPSSM.getFilter();

            log.info("Настройка фильтров окна 'Фильтр' модуля 'Заказы SAP'");
            filter_window.toggleOpened(true);
            filter_window.clickOK();
            log.info("Фильтры настроены");

            log.info("Открытие формы 'Заказы SAP'");
            orders = this.root.form("mf", SSMSapOrdersForm.class);
            log.info("Форма 'Заказы SAP' успешно запущена");

        }

        {
            log.info("Шаг 2");
            SSMGrid master_grid = orders.getMasterGrid();
            SSMGridData master_data = master_grid.selectAndAcceptCell(0);
            master_grid.hasData();
            master_tab = master_data.getColumnData("Таб.").get(0);
            master_fio = master_data.getColumnData("Фамилия И.О.").get(0);
            log.info("Выбран мастер {}.{}", master_tab, master_fio);
        }

        {
            log.info("Шаг 3");
            SSMGrid operation_grid = orders.getOperationGrid();
            SSMGridData operation_data = operation_grid.selectAndAcceptCell(0);
            operation_grid.hasData();
            master_tab = operation_data.getColumnData("Таб.").get(0);
            master_fio = operation_data.getColumnData("Фамилия И.О.").get(0);
            log.info("Выбран мастер {}.{}", master_tab, master_fio);
        }


    }

}
