package ru.checker.tests.ssm.forms.templates;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import lombok.extern.log4j.Log4j2;
import ru.checker.tests.desktop.test.entity.CheckerDesktopForm;
import ru.checker.tests.ssm.controls.grid.SSMGrid;
import ru.checker.tests.ssm.widgets.SSMTools;

@Log4j2
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@SuppressWarnings("unused")
public class FilteredFormTemplate {

    @Getter
    CheckerDesktopForm FORM;
    @Getter
    SSMTools TOOLS;

    public FilteredFormTemplate(CheckerDesktopForm form) {
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
