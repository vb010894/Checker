package ru.checker.tests.ssm.tests.product;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import ru.checker.tests.base.enums.CheckerOCRLanguage;
import ru.checker.tests.desktop.test.entity.CheckerDesktopWindow;
import ru.checker.tests.ssm.controls.grid.SSMGrid;
import ru.checker.tests.ssm.controls.grid.SSMGridData;
import ru.checker.tests.ssm.temp.forms.templates.FilteredFormTemplate;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class SSMG01P01 implements Runnable {

    CheckerDesktopWindow ROOT_WINDOW;
    String FORM_ID;

    public SSMG01P01(CheckerDesktopWindow root, String formID) {
        this.ROOT_WINDOW = root;
        this.FORM_ID = formID;
    }

    SSMGrid.ConditionConfigurer.ConditionConfigurerBuilder openCloseConfig = SSMGrid.ConditionConfigurer
            .builder()
            .column("С")
            .columnCondition("C|С")
            .condition1(SSMGrid.Condition.NOT_EQUAL);

    @Override
    public void run() {
        FilteredFormTemplate template = this.ROOT_WINDOW.form(FORM_ID, FilteredFormTemplate.class);
        //template.selectYear(String.valueOf(Calendar.getInstance().get(Calendar.YEAR)));
        template.toggleOpened(false);
        SSMGrid grid = template.getFilteredGrid();
        SSMGridData data = grid.getAllData();
        assertEquals(data.getRowSize(), 0, "В таблице найдены записи, проверка переключателя 'Открытые' провалена");

        template.toggleClosed(true);
        grid.filterByGUI(openCloseConfig.value1("2").build(), CheckerOCRLanguage.ENG, "С");
        data = grid.getAllData();
        assertEquals(data.getRowSize(), 0, "В таблице найдены записи, проверка переключателя 'Закрытые' провалена");
        grid.clearFilter();

        template.toggleClosed(false);
        grid.getAllData();

        template.toggleOpened(true);
        data = grid.getAllData();
        data.getColumnData("С")
                .parallelStream()
                .filter(record -> !record.equals("Открыт"))
                .findFirst().
                ifPresent(s -> fail("Найдена запись отличная от 'Открыт'. Значение - " + s));


        List.of("КМЦ", "РМЦ-1", "ФЛЦ", "ЦИ", "ЦРМО-1").forEach(shop -> this.checkShop(template, grid, shop));

    }

    private void checkShop(FilteredFormTemplate template, SSMGrid grid, String shop) {
        template.selectShop(shop);
        SSMGridData data = grid.getAllData();
        data.getColumnData("Цех")
                .parallelStream()
                .filter(record -> !record.equals(shop))
                .findFirst().
                ifPresent(s -> fail("Найдена запись отличная от 'Открыт'. Значение - " + s));
    }

}
