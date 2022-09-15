package ru.checker.tests.ssm.tests.org;

import lombok.AccessLevel;
import lombok.SneakyThrows;
import lombok.experimental.FieldDefaults;
import lombok.extern.log4j.Log4j2;
import ru.checker.tests.desktop.test.entity.CheckerDesktopWindow;
import ru.checker.tests.desktop.test.temp.CheckerDesktopTest;
import ru.checker.tests.ssm.controls.grid.SSMGrid;
import ru.checker.tests.ssm.forms.SSMOrgReleaseForm;
import ru.checker.tests.ssm.windows.core.service.ConfirmWindow;
import ru.checker.tests.ssm.windows.org.OrganizationShopPopupWindow;

import java.util.Map;

@Log4j2
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class SSMG0103P01 implements Runnable {

    /**
     * Главное окно.
     */
    CheckerDesktopWindow ROOT_WINDOW;

    /**
     * ID формы.
     */
    String FORM_ID;

    /**
     * Номер цеха.
     */
    final int NUMBER = 2;

    /**
     * Имя цеха.
     */
    final String NAME = "Тестовое имя";

    /**
     * Номер SAP.
     */
    final String SAP_NUMBER = "Тестовый номер SAP";

    final int DEVIATION = 3;

    /**
     * Полное имя.
     */
    final String FULL_NAME = "Тестовое полное имя";

    public SSMG0103P01(CheckerDesktopWindow root, String formID) {
        this.ROOT_WINDOW = root;
        this.FORM_ID = formID;
    }

    @SneakyThrows
    @Override
    public void run() {


        SSMOrgReleaseForm org = this.ROOT_WINDOW.form(this.FORM_ID, SSMOrgReleaseForm.class);
        {
            log.info("Вызов окна создания цеха");
            OrganizationShopPopupWindow shop = org.clickAdd();
            log.info("Окно вызвано");
            log.info("задание параметров нового цеха");
            shop.setNumber(this.NUMBER);
            shop.setName(this.NAME);
            shop.setFullName(this.FULL_NAME);
            shop.setSapNumber(this.SAP_NUMBER);
            shop.setDeviation(this.DEVIATION);
            shop.clickOK();
            log.info("Параметры заданы");
        }

        {
            log.info("Проверка и поиск созданного цеха с номером '{}' и именем - '{}'", this.NUMBER, this.NAME);
            SSMGrid shop_grid = org.getOrganizationGrid();
            shop_grid.filter("number_filter");
            shop_grid.getDataByRow(0, true);
            shop_grid.getAllData();
            Map<String, String> columns = Map.ofEntries(
                    Map.entry("1", String.valueOf(this.NUMBER)),
                    Map.entry("2", this.NAME),
                    Map.entry("3", this.FULL_NAME),
                    Map.entry("4", this.SAP_NUMBER),
                    Map.entry("5", String.valueOf(this.DEVIATION))
            );

            shop_grid.columnsDataEqual(columns);
            log.info("Цех успешно создан и найден в таблице");
            log.info("Удаление созданного цеха - '{}. {}'", this.NUMBER, this.NAME);
            org.clickDelete();
            ConfirmWindow confirm = CheckerDesktopTest.getCurrentApp().window("ssm_core_confirm", ConfirmWindow.class);
            confirm.clickYes();
            shop_grid.getDataByRow(0, false);
            shop_grid.hasNotData();
            log.info("Цех '{}. {}' успешно удален", this.NUMBER, this.NAME);
            shop_grid.clearFilter();
        }
        log.info("Завершение тестового случая");
    }
}
