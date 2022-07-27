package ru.checker.tests.ssm.temp.forms;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.log4j.Log4j2;
import ru.checker.tests.desktop.test.entity.CheckerDesktopForm;
import ru.checker.tests.ssm.controls.grid.SSMGrid;
import ru.checker.tests.ssm.temp.widgets.SSMTools;

@Log4j2
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@SuppressWarnings("unused")
public class TemplatesFilteredFormTemplate {

    CheckerDesktopForm FORM;
    SSMTools TOOLS;

    public TemplatesFilteredFormTemplate(CheckerDesktopForm form) {
        log.info("Инициализация шаблона формы для проверки фильтров");
        this.FORM = form;
        this.TOOLS = form.widget("ssm_menu", SSMTools.class);
        log.info("Форма инициализирована");
    }

    public SSMGrid getFilteredGrid() {
        log.info("Получение таблицы с данными, подлежащими фильтрации");
        return this.FORM.custom("filtered_table", -1, SSMGrid.class);
    }

    public void toggleOpened(boolean state) {
        log.info("Изменение состояния фильтра 'Открытые' на '{}'", state ? "Активен" : "Не активен" );
        TOOLS.toggle("opened", state);
    }

    public void toggleClosed(boolean state) {
        log.info("Изменение состояния фильтра 'Закрытые' на '{}'", state ? "Активен" : "Не активен" );
        TOOLS.toggle("closed", state);
    }

    public void selectShop(String name) {
        log.info("Выбор цеха '{}'", name);
        TOOLS.selectCombobox("shop", name);
    }

    public void selectYear(String year) {
        log.info("Выбор года '{}'", year);
        TOOLS.selectCombobox("year", year);
    }
}
