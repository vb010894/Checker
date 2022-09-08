package ru.checker.tests.ssm.tests.product;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.log4j.Log4j2;
import mmarquee.automation.controls.EditBox;
import mmarquee.automation.controls.mouse.AutomationMouse;
import ru.checker.tests.desktop.utils.CheckerFieldsUtils;
import ru.checker.tests.desktop.test.entity.CheckerDesktopWindow;
import ru.checker.tests.desktop.test.temp.CheckerDesktopTest;
import ru.checker.tests.ssm.controls.grid.SSMGrid;
import ru.checker.tests.ssm.controls.grid.SSMGridData;
import ru.checker.tests.ssm.forms.SSMProductReleaseForm;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.*;

@Log4j2
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


    SSMGrid.ConditionConfigurer accepted_number_config = SSMGrid.ConditionConfigurer
            .builder()
            .column("Номер")
            .condition1(SSMGrid.Condition.CONTAINS)
            .value1("Тест").build();

    SSMGrid.ConditionConfigurer.ConditionConfigurerBuilder date_config = SSMGrid.ConditionConfigurer
            .builder()
            .column("Дата подтв.")
            .condition1(SSMGrid.Condition.MORE_THEN);

    @Override
    public void run() {
        SSMProductReleaseForm template = this.ROOT_WINDOW.form(FORM_ID, SSMProductReleaseForm.class);

        log.info("Выставление фильтров");
        template.toggleOpened(true);
        template.toggleClosed(false);
        template.selectShop("");
        log.info("Фильтры выставлены");

        SSMGrid grid = template.getFilteredGrid();
        grid.filterByGUI(moreZeroConfig);
        grid.selectRow(0);

        log.info("Вызов окна 'Выпуск продукции'");
        template.clickProductionRelease();
        CheckerDesktopWindow productReleasePopup = CheckerDesktopTest.getCurrentApp().window("product_release_window");
        var edits = productReleasePopup.edits("product01");
        assertNotNull(edits, "Не удалось получить поля формы 'Выпуск продукции'");
        AtomicReference<EditBox> invoice_number = new AtomicReference<>();
        AtomicReference<EditBox> available = new AtomicReference<>();

        log.info("Получение полей для заполнения");
        edits.forEach(ed -> {
            if(CheckerFieldsUtils.getLabel(ed.getElement()).contains("Номер накладной"))
                invoice_number.set(ed);
            if(CheckerFieldsUtils.getLabel(ed.getElement()).contains("Выпустить, шт"))
                available.set(ed);
        });
        log.info("Поля получены");

        assertNotNull(invoice_number.get(), "Не удалось получить поле 'Номер накладной'");
        assertNotNull(invoice_number.get(), "Выпустить, шт'");
        AtomicReference<String> invoice_value = new AtomicReference<>();
        AtomicReference<String> available_value = new AtomicReference<>();

        log.info("Вставка значения 'Тест' в поле 'Номер накладной'");
        assertDoesNotThrow(() -> {
            invoice_number.get().setValue("Тест");
            invoice_value.set(invoice_number.get().getValue());
        }, "Не удалось вставить значение в поле 'Номер накладной'");
        log.info("Значение успешно вставлено");

        log.info("Вставка значения '1' в поле 'Выпустить, шт'");
        assertDoesNotThrow(() -> {
            AutomationMouse.getInstance().setLocation(available.get().getClickablePoint());
            AutomationMouse.getInstance().leftClick();
            Robot robot = new Robot();
            robot.keyPress(KeyEvent.VK_UP);
            robot.keyRelease(KeyEvent.VK_UP);
            Thread.sleep(500);
            available_value.set(available.get().getValue());
        }, "Не удалось вставить значение в поле 'Выпустить, шт'");

        log.info("Проверка введенных значений");
        assertEquals(invoice_value.get(), "Тест", "После ввода в поле 'Номер накладной' не поменялось значение 'Тест'");
        log.info("Значения соответствуют введенным");

        log.info("Нажатие кнопки 'OK'");
        Date date_start = new Date();
        assertDoesNotThrow(() -> productReleasePopup.button("ok_button").click(), "Не удалось нажать кнопку 'Ok'");
        log.info("Кнопка 'OK' нажата");

        log.info("Фильтрация таблицы 'Подтвержденные операции' по колонке 'Номер'");
        SSMGrid accepted_grid = template.getAcceptedGrid();
        accepted_grid.filterByGUI(accepted_number_config);

        accepted_grid.filterByGUI(
                date_config
                .value1(new SimpleDateFormat("dd.MM.yyyy HH:mm:ss").format(date_start))
                .build());
        SSMGridData accepted_data = accepted_grid.getAllData();

        log.info("Проверка данных записи таблицы 'Подтвержденные операции'");
        assertTrue(
                accepted_data.getColumnData("Номер").parallelStream().anyMatch(row -> row.startsWith("Тест")),
                "Не найдена запись в колоне 'Номер' начинающейся с 'Тест'");

        assertTrue(
                accepted_data.getColumnData("Кол-во").parallelStream().anyMatch(row -> row.trim().equalsIgnoreCase("1")),
                "Не найдена запись в колоне 'Кол-во' равной '1'");

        String date_now = new SimpleDateFormat("dd.MM.yyyy HH").format(new Date());
        assertTrue(accepted_data.getColumnData("Дата подтв.").parallelStream().anyMatch(row -> row.trim().contains(date_now.trim())), "Найдены данные не начинающиеся на " + date_now);
        log.info("Данные соответствуют ожидаемым");

        log.info("Тест выполнился успешно");
    }
}
