package ru.checker.tests.ssm.tests.product;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.log4j.Log4j2;
import ru.checker.tests.base.test.CheckerConstants;
import ru.checker.tests.desktop.test.entity.CheckerDesktopWindow;
import ru.checker.tests.desktop.test.temp.CheckerDesktopTest;
import ru.checker.tests.ssm.controls.grid.SSMGrid;
import ru.checker.tests.ssm.forms.SSMProductReleaseForm;
import ru.checker.tests.ssm.windows.product.ProductReleasePopupWindow;

import java.text.SimpleDateFormat;
import java.util.Date;

@Log4j2
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class SSMG01P02 implements Runnable {

    CheckerDesktopWindow ROOT_WINDOW;
    String FORM_ID;

    public SSMG01P02(CheckerDesktopWindow root, String formID) {
        this.ROOT_WINDOW = root;
        this.FORM_ID = formID;
    }


    @Override
    public void run() {
        SSMProductReleaseForm template = this.ROOT_WINDOW.form(FORM_ID, SSMProductReleaseForm.class);
        SSMGrid grid;
        ProductReleasePopupWindow productReleasePopup;

        {
            log.info("Шаг 1");
            log.info("Выставление фильтров");
            template.toggleOpened(true);
            template.toggleClosed(false);
            template.selectShop("");
            log.info("Фильтры выставлены");
        }

        {
            log.info("Шаг 2");
            grid = template.getFilteredGrid();
            grid.filter("dostupno_more_0_filter");
            grid.getDataByRow(0, true);
        }

        {
            log.info("Шаг 3");
            log.info("Вызов окна 'Выпуск продукции'");
            template.clickProductionRelease();
            productReleasePopup = CheckerDesktopTest.getCurrentApp().window("product_release_window", ProductReleasePopupWindow.class);
            log.info("Окно 'Выпуск продукции' вызвано");
        }

        {
            log.info("Шаг 4");
            productReleasePopup.setInvoiceNumber("Тест");
        }

        {
            log.info("Шаг 5");
            productReleasePopup.setReleaseCount(1);
        }

        {
            log.info("Шаг 5");
            Date date = new Date();
            productReleasePopup.clickOK();

            log.info("Проверка данных записи таблицы 'Подтвержденные операции'");
            SSMGrid accepted_grid = template.getAcceptedGrid();
            accepted_grid.filter("accepted_test_number_filter");
            accepted_grid.getDataByRow(0, true);
            CheckerConstants.saveConstant("dateNow", new SimpleDateFormat("dd.MM.yyyy HH:mm:ss").format(date));
            accepted_grid.filter("years_more_now_filter");
            accepted_grid.getDataByRow(0, true);

            accepted_grid.filter("release_count_equal_1_filter");
            accepted_grid.getDataByRow(0, true);

            log.info("Данные соответствуют ожидаемым");
        }

        log.info("Тест выполнился успешно");
    }
}
