package ru.checker.tests.ssm.test;

import com.sun.jna.Pointer;
import com.sun.jna.platform.win32.User32;
import com.sun.jna.platform.win32.WinDef;
import com.sun.jna.ptr.IntByReference;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import lombok.extern.log4j.Log4j2;
import mmarquee.automation.ControlType;
import mmarquee.automation.Element;
import mmarquee.automation.UIAutomation;
import mmarquee.automation.controls.ElementBuilder;
import mmarquee.automation.controls.Search;
import mmarquee.automation.controls.Window;
import org.junit.jupiter.api.Assertions;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Parameters;
import ru.checker.tests.base.logger.CheckerLogAppender;
import ru.checker.tests.base.test.CheckerConstants;
import ru.checker.tests.base.utils.CheckerTools;
import ru.checker.tests.desktop.test.entity.CheckerDesktopWindow;
import ru.checker.tests.desktop.test.temp.CheckerDesktopTest;
import ru.checker.tests.ssm.widgets.SSMNavigation;
import ru.checker.tests.ssm.widgets.SSMTools;

import java.util.List;
import java.util.Map;

@Log4j2
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SSMTest extends CheckerDesktopTest {

    @Getter
    CheckerDesktopWindow rootWindow;

    @Getter
    SSMNavigation navigation;

    /**
     * Init test case.
     *
     * @param id From ID.
     */
    @Parameters({"form.id"})
    @BeforeMethod
    public void before(String id) {
        log.info("Инициализация тестового случая");
        CheckerLogAppender.clearStatistic();
        CheckerConstants.clearConstants();
        this.rootWindow = getCurrentApp().window("ssm_main");
        this.rootWindow.maximize();
        this.navigation = this.rootWindow.widget("ssm_navigation", SSMNavigation.class);

        if (!this.getFormNeedCheckActivity(id)) {
            log.info("Открытие формы без проверки активности");
            Assertions.assertDoesNotThrow(() -> this.openFormByNavigation(id), "Не удалось открыть форму с помощью навигации");
            log.info("Навигация к форме успешно выполнена. Начало тестового случая");
        } else {

            log.info("Проверка активности формы");
            try {
                if (!this.rootWindow.checkFormExist(id))
                    throw new Exception("Открытие через навигацию");
            } catch (Exception ex) {
                log.info("Открытие формы через навигацию");
                Assertions.assertDoesNotThrow(() -> this.openFormByNavigation(id), "Не удалось открыть форму с помощью навигации");
                log.info("Повторная проверка активности формы");
                boolean enabled = Assertions.assertDoesNotThrow(() -> this.rootWindow.form(id).getControl().isEnabled(), "Не удалось получить состояние главной формы");
                Assertions.assertTrue(enabled, "Главная форма не доступна");
            }

            log.info("Главная форма активна. Начало тестового случая");
        }
    }

    /**
     * Open form by navigation.
     *
     * @param formID Form ID
     */
    private void openFormByNavigation(String formID) {
        Map<String, Object> formDefinition = this.getFormDefinition(formID);
        Assertions.assertTrue(formDefinition.containsKey("navigation"), "Не найден ключ 'navigation' в описании формы. Не удалось открыть форму с помощью навигации");
        String navigation = CheckerTools.castDefinition(formDefinition.get("navigation"));
        this.navigation = this.rootWindow.widget("ssm_navigation", SSMNavigation.class);
        this.navigation.selectNode(SSMNavigation.SSMNavigationEnum.valueOf(navigation));
    }

    private boolean getFormNeedCheckActivity(String formID) {
        Map<String, Object> formDefinition = this.getFormDefinition(formID);
        return CheckerTools.castDefinition(formDefinition.getOrDefault("needCheck", true));
    }

    private Map<String, Object> getFormDefinition(String formID) {
        Assertions.assertTrue(this.rootWindow.getFormsDefinitions().containsKey(formID), "Не найдена форма с ID - " + formID);
        return this.rootWindow.getFormsDefinitions().get(formID);
    }

    /**
     * Close test case.
     */
    @AfterMethod
    public void after() {
        log.info("Завершение тестового случая");
        //Map<String, List<LogEvent>> test = CheckerLogAppender.getLogging();
        this.closePopUps();
        this.closeForm();
        log.info("Тестовый случай завершен");
    }

    /**
     * Close main form.
     */
    private void closeForm() {
        log.info("Закрытие главной формы");
        SSMTools tools = this.rootWindow.widget("ssm_menu", SSMTools.class);
        navigation.selectRoot();
        tools.clickButton("info_exit");
        log.info("Главная форма закрыта");
    }

    /**
     * Close popup windows.
     */
    private void closePopUps() {
        User32.INSTANCE.EnumWindows((hwnd, pointer) -> {
            try {
                if (User32.INSTANCE.IsWindowVisible(hwnd)) {
                    IntByReference reference = new IntByReference(0);
                    User32.INSTANCE.GetWindowThreadProcessId(hwnd, reference);
                    if (reference.getValue() == rootWindow.getControl().getElement().getProcessId() & !hwnd.equals(rootWindow.getControl().getNativeWindowHandle()))
                        this.closeWindowByHandle(hwnd);

                }
            } catch (Exception ex) {
                log.error("Не удалось закрыть окно с handle - " + hwnd);
            }

            return true;
        }, new Pointer(0));
    }

    /**
     * Закрытие окна по Handle
     *
     * @param handle Handle окна
     * @throws Exception Возникает при невозможности доступа к элементу
     */
    private void closeWindowByHandle(WinDef.HWND handle) throws Exception {

        List<String> buttons = List.of("Отмена", "Закрыть");

        Element el = UIAutomation.getInstance().getElementFromHandle(handle);
        if (el.getClassName().equals("TApplication"))
            return;

        if (el.getControlType() == ControlType.Window.getValue()) {
            Window window = new Window(new ElementBuilder().element(el));
            log.debug("Попытка закрыть окно - '{}'. (Класс - '{}')", window.getName(), window.getClassName());
            boolean result = buttons.stream().anyMatch(button -> {
                try {
                    log.debug("Попытка закрытия через кнопку - '{}'", button);
                    window.getButton(Search.getBuilder().name(button).build()).click();
                    log.debug("Окно закрыто через кнопку - '{}'. Проверка состояния", button);
                    Thread.sleep(1000);
                    boolean check = window.isEnabled();
                    log.debug("Окно {}активно", (check ? "" : "не"));
                    return check;
                } catch (Exception e) {
                    log.debug("Не удалось закрыть окно с помощью кнопки '{}'", button);
                    return false;
                }
            });
            if (!result) {
                log.debug("Закрытие с помощью UIAutomation");
                window.close();
                boolean check = UIAutomation.getInstance().getElementFromHandle(window.getNativeWindowHandle()).isEnabled();
                log.debug("Окно {}активно", (check ? "" : "не"));
            }
        }
    }

}
