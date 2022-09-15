package ru.checker.tests.ssm.forms;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import lombok.extern.log4j.Log4j2;
import ru.checker.tests.desktop.test.entity.CheckerDesktopForm;
import ru.checker.tests.desktop.test.temp.CheckerDesktopTest;
import ru.checker.tests.ssm.controls.grid.SSMGrid;
import ru.checker.tests.ssm.widgets.SSMTools;
import ru.checker.tests.ssm.windows.org.OrganizationShopPopupWindow;

/**
 * Форма "Справочник организации".
 *
 * ID - organization_directory.
 * Файл конфигураций - /Forms/ORGANIZATION_DIRECTORY.yaml
 *
 * @author vd.zinovev
 */
@Log4j2
@FieldDefaults(level = AccessLevel.PRIVATE)
@SuppressWarnings("unused")
public class SSMOrgReleaseForm {

    /**
     * Компонент формы.
     */
    @Getter
    final CheckerDesktopForm form;

    /**
     * Панель инструментов формы "Справочник организации"
     */
    final SSMTools tools;

    /**
     * Конструктор.
     * @param form Форма
     */
    public SSMOrgReleaseForm(CheckerDesktopForm form) {
        this.form = form;
        this.tools = this.form.widget("ssm_menu", SSMTools.class);
    }

    /**
     * Нажатие на кнопку "Добавить".
     *
     * ID в конфигурациях - "org_add".
     * Конфигурационный файл - /Widgets/SSM_TOOLS.yaml.
     *
     * @return Всплывающее окно 'Цех'
     */
    public OrganizationShopPopupWindow clickAdd() {
        log.info("Нажатие на кнопку 'Добавить' формы 'Справочник организации'");
        this.tools.clickButton("org_add");
        log.info("Кнопка 'Добавить' формы 'Справочник организации' нажата");
        log.info("Инициализация всплывающего окна 'Цех'");
        OrganizationShopPopupWindow window = CheckerDesktopTest.getCurrentApp().window("org_shop_details",OrganizationShopPopupWindow.class);
        log.info("Всплывающее окно 'Цех' инициализировано");
        return window;
    }

    /**
     * Нажатие на кнопку "Добавить".
     *
     * ID в конфигурациях - "org_edit".
     * Конфигурационный файл - /Widgets/SSM_TOOLS.yaml.
     */
    public void clickEdit() {
        log.info("Нажатие на кнопку 'Изменить' формы 'Справочник организации'");
        this.tools.clickButton("org_edit");
        log.info("Кнопка 'Изменить' формы 'Справочник организации' нажата");
    }

    /**
     * Нажатие на кнопку "Блокировать".
     *
     * ID в конфигурациях - "org_block".
     * Конфигурационный файл - /Widgets/SSM_TOOLS.yaml.
     */
    public void clickBlock() {
        log.info("Нажатие на кнопку 'Блокировать' формы 'Справочник организации'");
        this.tools.clickButton("org_block");
        log.info("Кнопка 'Блокировать' формы 'Справочник организации' нажата");
    }

    /**
     * Нажатие на кнопку "Удалить".
     *
     * ID в конфигурациях - "org_delete".
     * Конфигурационный файл - /Widgets/SSM_TOOLS.yaml.
     */
    public void clickDelete() {
        log.info("Нажатие на кнопку 'Удалить' формы 'Справочник организации'");
        this.tools.clickButton("org_delete");
        log.info("Кнопка 'Удалить' формы 'Справочник организации' нажата");
    }

    /**
     * Нажатие на кнопку "Обновить".
     *
     * ID в конфигурациях - "org_refresh".
     * Конфигурационный файл - /Widgets/SSM_TOOLS.yaml.
     */
    public void clickRefresh() {
        log.info("Нажатие на кнопку 'Обновить' формы 'Справочник организации'");
        this.tools.clickButton("org_refresh");
        log.info("Кнопка 'Обновить' формы 'Справочник организации' нажата");
    }

    /**
     * Получение таблицы 'Справочник организации'.
     * @return Таблица 'Справочник организации'
     */
    public SSMGrid getOrganizationGrid() {
        log.info("Инициализация таблицы 'Справочник организации' формы 'Справочник организации'");
        return this.form.custom("org_data_table", SSMGrid.class);
    }
}
