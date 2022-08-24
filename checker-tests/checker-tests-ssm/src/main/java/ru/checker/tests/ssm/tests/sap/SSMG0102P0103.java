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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

/**
 * SSM.G.01.02.P.03. Работа с фильтрами. Фильтр 'Год'
 * @author vd.zinovev
 */
@Log4j2
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class SSMG0102P0103 implements Runnable {

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
    public SSMG0102P0103(CheckerDesktopWindow root) {
        this.root = root;
    }

    /**
     * Запуск.
     */
    @Override
    public void run() {
        SapFilterWindow filter_window = SAPSSM.getFilter();

        log.info("Настройка фильтров");
        log.info("Включение переключателя 'Открытые'");
        filter_window.toggleOpened(true);
        log.info("Выбор значения 'КПЦ' поля 'Цех'");
        filter_window.selectShop("");
        filter_window.selectShop("КПЦ");
        log.info("Выбор 2021 года поля 'Год'");
        filter_window.selectYear("2021");
        log.info("Нажатие на кнопку 'OK'");
        filter_window.clickOK();
        log.info("Фильтры настроены");

        log.info("Открытие формы 'Заказы SAP'");
        SSMSapOrdersForm orders = this.root.form("mf", SSMSapOrdersForm.class);
        log.info("Форма 'Заказы SAP' успешно запущена");
        SSMGrid orders_grid = orders.getSapOrderGrid();
        log.info("Фильтрация колонки 'ДеБлок' по условию меньше 01.01.2021 года или больше 31.12.2021");
        orders_grid.filterByGUI(year_filter);
        log.info("Проверка данных");
        SSMGridData data = orders_grid.getDataFromRow(0);
        assertEquals(data.getRowSize(), 0, "Найдены записи не соответствующие условию меньше 01.01.2021 года или больше 31.12.2021");
        log.info("Данные колонки 'ДеБлок' соответствуют условию меньше 01.01.2021 года или больше 31.12.2021");
        orders_grid.clearFilter();
        orders.callFilter();

        filter_window.refresh();
        filter_window.selectYear("Все года");
        filter_window.clickOK();
        data = orders_grid.getDataFromRow(0);
        log.info("Проверка таблицы 'Производственные заказы SAP' на наличие данных");
        assertNotEquals(
                data.getRowSize(),
                0, "После выбора значения 'Все года' поля 'Год' в таблице 'Производственные заказы SAP' отсутствуют данные");
        log.info("Таблица содержит данные");
        log.info("Тестовый случай выполнен");
    }
}
