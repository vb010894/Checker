package ru.checker.tests.ssm.tests.product;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.log4j.Log4j2;
import ru.checker.tests.desktop.test.entity.CheckerDesktopWindow;
import ru.checker.tests.ssm.controls.grid.SSMGrid;
import ru.checker.tests.ssm.controls.grid.SSMGridData;
import ru.checker.tests.ssm.forms.SSMProductReleaseForm;
import ru.checker.tests.ssm.forms.templates.FilteredFormTemplate;

import java.util.List;

import static org.junit.jupiter.api.Assertions.fail;

/**
 * С.SSM.G.01.01.P.01. Выпуск продукции SAP. Работа с фильтрами.
 *
 * @author vd.zinovev
 */
@Log4j2
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class SSMG01P01 implements Runnable {

    /**
     * Главное окно.
     */
    CheckerDesktopWindow ROOT_WINDOW;

    /**
     * ID формы.
     */
    String FORM_ID;

    /**
     * Конструктор.
     * @param root Главная форма
     * @param formID ID тестируемой формы.
     */
    public SSMG01P01(CheckerDesktopWindow root, String formID) {
        this.ROOT_WINDOW = root;
        this.FORM_ID = formID;
    }

    /**
     * Сценарий.
     */
    @Override
    public void run() {
        SSMProductReleaseForm template;
        SSMGrid grid;

        {
            log.info("Шаг 1");
            template = this.ROOT_WINDOW.form(FORM_ID, SSMProductReleaseForm.class);
            template.toggleOpened(false);
            grid = template.getFilteredGrid();
            grid.getDataByRow(0, true);
        }

        {
            log.info("Шаг 2");
            template.toggleClosed(true);
            grid.filter("open_not_2_filter");
            grid.getDataByRow(0);
            grid.hasNotData();
            grid.clearFilter();
        }

        {
            log.info("Шаг 3");
            template.toggleClosed(false);
            template.toggleOpened(true);

            grid.filter("open_2_filter");
            grid.getDataByRow(0);
            grid.hasNotData();
            grid.clearFilter();
        }

        {
            log.info("Шаг 4");
            List.of("КМЦ", "РМЦ-1", "ФЛЦ", "КПЦ", "ЦРМО-1").forEach(shop -> this.checkShop(template, grid, shop));
        }

    }

    /**
     * Проверяет фильтр цехов.
     * @param template Форма
     * @param grid Таблица
     * @param shop Цех
     */
    private void checkShop(FilteredFormTemplate template, SSMGrid grid, String shop) {
        log.info("Проверка цеха '{}'", shop);
        template.selectShop(shop);
        SSMGridData data = grid.getAllData();
        grid.hasData();
        data.getColumnData("Цех")
                .parallelStream()
                .filter(record -> !record.equals(shop))
                .findFirst().
                ifPresent(s -> fail("Найдена запись отличная от 'Открыт'. Значение - " + s));
        log.info("Цех '{}' проверен. Данные таблицы соответствует", shop);
    }

}
