package ru.checker.tests.ssm.forms;

import lombok.extern.log4j.Log4j2;
import ru.checker.tests.desktop.test.entity.CheckerDesktopForm;
import ru.checker.tests.ssm.controls.grid.SSMGrid;
import ru.checker.tests.ssm.forms.templates.FilteredFormTemplate;

@Log4j2
public class SSMProductReleaseForm extends FilteredFormTemplate {

    public SSMProductReleaseForm(CheckerDesktopForm form) {
        super(form);
    }

    public void clickProductionRelease() {
        log.info("Нажатие на кнопку 'Выпуск продукции'");
        this.getTOOLS().clickButton("product_release");
        log.info("Кнопка успешно нажата");
    }

    public SSMGrid getAcceptedGrid() {
        return this.getFORM().custom("operation_table", SSMGrid.class);
    }


}
