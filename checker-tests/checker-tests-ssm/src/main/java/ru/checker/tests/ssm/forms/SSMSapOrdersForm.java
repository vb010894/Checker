package ru.checker.tests.ssm.forms;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import lombok.extern.log4j.Log4j2;
import ru.checker.tests.desktop.test.entity.CheckerDesktopForm;
import ru.checker.tests.desktop.test.temp.CheckerDesktopTest;
import ru.checker.tests.desktop.utils.CheckerDesktopManipulator;
import ru.checker.tests.ssm.controls.grid.SSMGrid;
import ru.checker.tests.ssm.widgets.SSMTools;
import ru.checker.tests.ssm.windows.sap.SapPRBCreationWindow;

import java.awt.*;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Форма "Заказы SAP".
 *
 * ID - mf.
 *
 * Файл конфигураций - 'SAP_ORDERS.yaml'
 */
@Log4j2
@FieldDefaults(level = AccessLevel.PRIVATE)
@SuppressWarnings("unused")
public class SSMSapOrdersForm {

    /**
     * Компонент формы.
     */
    @Getter
    final CheckerDesktopForm form;

    /**
     * Панель инструментов формы "Заказы SAP"
     */
    final SSMTools tools;

    /**
     * Вкладки формы "Заказы SAP".
     */
    List<Map.Entry<String, Rectangle>> tabs;

    /**
     * Цвет активной вкладки.
     */
    final Color ACTIVE_TAB_COLOR = new Color(255, 255, 255);

    /**
     * Конструктор.
     * @param form Форма
     */
    public SSMSapOrdersForm(CheckerDesktopForm form) {
        this.form = form;
        this.tools = this.form.widget("ssm_menu", SSMTools.class);
    }

    /**
     * Обновляет вкладки формы.
     *
     * Следует применять если появляются новые.
     */
    public void refreshTabs() {
        this.tabs = this.tools.getTabs(150);
    }

    /**
     * Получает вкладки их положение.
     * @return Вкладки
     */
    public List<Map.Entry<String, Rectangle>> getTabs() {
        if(this.tabs == null) {
            this.refreshTabs();
        }
        return this.tabs;
    }

    /**
     * Получает активную вкладку.
     * @return Активная вкладка
     */
    public String getActiveTab() {
        log.info("Получение активной вкладки формы 'Заказы SAP'");
        AtomicReference<String> result = new AtomicReference<>(null);
        this.getTabs().parallelStream().filter(entry -> {
            try {
                Color color = new CheckerDesktopManipulator
                        .Screen()
                        .getPixelColor(
                                entry.getValue().getMaxX() + 5d,
                                entry.getValue().getMinY() - 5d);
                return color.getRGB() == this.ACTIVE_TAB_COLOR.getRGB();
            } catch (AWTException e) {
                return false;
            }
        }).findFirst().ifPresent(entry -> result.set(entry.getKey()));
        log.info("Активная вкладка {}", (result.get() == null ? "не найдена" : "найдена - " + result.get()));
        return result.get();
    }

    /**
     * Вызывает окно фильтрации.
     */
    public void callFilter() {
        log.info("Вызов окна 'Фильтр' по кнопке 'Открыть'");
        this.tools.clickButton("sap_filter");
        log.info("Кнопка 'Открыть' нажата");
    }

    /**
     * Нажатие на кнопку "Добавить"
     * @return Форма "Задание ПРБ создание..."
     */
    public SapPRBCreationWindow clickAdd() {
        log.info("Нажатие кнопки 'Добавить'");
        this.tools.clickButton("ssm_05");
        log.info("Инициализация окна 'Задание ПРБ создание'");
        SapPRBCreationWindow prbWindow = CheckerDesktopTest.getCurrentApp().window("sap_order_prb_form", SapPRBCreationWindow.class);
        log.info("Окно инициализировано");
        return prbWindow;
    }

    public void selectYear(String year) {
        System.out.println("Выбор года. Фильтр 'Год', ID - 'SSM_06'");
        this.tools.selectCombobox("ssm_06", year);
    }

    public void clickRefresh() {
        System.out.println("Нажатие кнопки 'Обновить', ID - 'SSM_07'");
        this.tools.clickButton("ssm_07");
    }

    /**
     * Нажатие кнопки "Назначить".
     *
     * ID в конфигурациях - 'SSM_04'.
     */
    public void clickAssign() {
        log.info("Нажатие кнопки 'Назначить', ID - 'SSM_04'");
        this.tools.clickButton("ssm_04");
        log.info("Кнопка 'Назначить' нажата");
    }

    /**
     * Получение таблицы "Производственные заказы SAP".
     *
     * ID в конфигурациях - "ssm_01_01".
     * @return Таблица
     */
    public SSMGrid getSapOrderGrid() {
        return this.form.custom("ssm_01_01", -1, SSMGrid.class);
    }

    /**
     * Поучение таблицы "Заказы Лоцман".
     *
     * ID в конфигурациях - "sap_lotsman_grid".
     * @return Таблица
     */
    public SSMGrid getLotsmanOrderGrid() {
        log.info("Получение таблицы 'Заказы Лоцман' формы 'Заказы SAP'");
        return this.form.custom("sap_lotsman_grid", -1, SSMGrid.class);
    }

    /**
     * Поучение таблицы "Мастера".
     *
     * ID в конфигурациях - "ssm_01_02".
     * @return Таблица
     */
    public SSMGrid getMasterGrid() {
        log.info("Получение таблицы 'Мастера' формы 'Заказы SAP'");
        return this.form.custom("ssm_01_02", -1, SSMGrid.class);
    }

    /**
     * Поучение таблицы "Операции".
     *
     * ID в конфигурациях - "ssm_01_05".
     * @return Таблица
     */
    public SSMGrid getOperationGrid() {
        log.info("Получение таблицы 'Операции' формы 'Заказы SAP'");
        return this.form.custom("ssm_01_05", -1, SSMGrid.class);
    }

    /**
     * Поучение таблицы "Выпущенная продукция".
     *
     * ID в конфигурациях - "ssm_01_03".
     * @return Таблица
     */
    public SSMGrid getProductionReleaseGrid() {
        log.info("Получение таблицы 'Заказ продукции' формы 'Заказы SAP'");
        return this.form.custom("ssm_01_03", -1, SSMGrid.class);
    }


    public SSMGrid getOrderPRBGrid() {
        log.info("Получение таблицы 'Задания ПРБ' формы 'Заказы SAP'");
        return this.form.custom("ssm_01_04", -1, SSMGrid.class);
    }


    /**
     * Переключение вкладок формы.
     * @param pageName Имя вкладки
     */
    public void selectPage(String pageName) {
        log.info("Переключение на вкладку '{}'. Форма - 'Заказы SAP'", pageName);
        Optional<Map.Entry<String, Rectangle>> opt = this.getTabs()
                .parallelStream()
                .filter(entry -> entry.getKey().equalsIgnoreCase(pageName))
                .findFirst();
        if(opt.isPresent()) {
            CheckerDesktopManipulator.Mouse.click((int) opt.get().getValue().getCenterX(), (int) opt.get().getValue().getCenterY());
            log.info("Вкладка {} переключена", pageName);
        } else {
            fail("На форме 'Заказы SAP' не найдена вкладка с именем - " + pageName);
        }
    }

}
