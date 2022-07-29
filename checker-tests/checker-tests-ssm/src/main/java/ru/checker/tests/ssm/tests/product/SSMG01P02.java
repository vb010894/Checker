package ru.checker.tests.ssm.tests.product;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import mmarquee.automation.UIAutomation;
import mmarquee.automation.controls.EditBox;
import ru.checker.tests.base.enums.CheckerOCRLanguage;
import ru.checker.tests.desktop.test.entity.CheckerDesktopWindow;
import ru.checker.tests.desktop.test.temp.CheckerDesktopTest;
import ru.checker.tests.ssm.controls.grid.SSMGrid;
import ru.checker.tests.ssm.controls.grid.SSMGridData;
import ru.checker.tests.ssm.temp.forms.SSMProductReleaseForm;
import ru.checker.tests.ssm.temp.forms.templates.FilteredFormTemplate;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class SSMG01P02 implements Runnable {

    CheckerDesktopWindow ROOT_WINDOW;
    String FORM_ID;

    public SSMG01P02(CheckerDesktopWindow root, String formID) {
        this.ROOT_WINDOW = root;
        this.FORM_ID = formID;
    }

    SSMGrid.ConditionConfigurer moreZeroConfig = SSMGrid.ConditionConfigurer
            .builder()
            .column("Доступно")
            .condition1(SSMGrid.Condition.MORE_THEN)
            .value1("0").build();

    @Override
    public void run() {
        SSMProductReleaseForm template = this.ROOT_WINDOW.form(FORM_ID, SSMProductReleaseForm.class);
        //template.selectYear(String.valueOf(Calendar.getInstance().get(Calendar.YEAR)));
        template.toggleOpened(true);
        template.toggleClosed(false);
        template.selectShop("");

        SSMGrid grid = template.getFilteredGrid();
        grid.filterByGUI(moreZeroConfig);
        SSMGridData data = grid.getDataFromRow(0);
        template.clickProductionRelease();
        CheckerDesktopWindow productReleasePopup = CheckerDesktopTest.getCurrentApp().window("product_release_window");
        var edits = productReleasePopup.edits("product01");

        assertNotEquals(data.getRowSize(), 0, "В таблице найдены записи, проверка переключателя 'Открытые' провалена");

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
