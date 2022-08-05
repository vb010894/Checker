package ru.checker.tests.ssm.temp.test;

import com.sun.jna.platform.win32.User32;
import com.sun.jna.platform.win32.WinDef;
import junit.framework.AssertionFailedError;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import lombok.extern.log4j.Log4j2;
import mmarquee.automation.AutomationException;
import mmarquee.automation.ControlType;
import mmarquee.automation.Element;
import mmarquee.automation.UIAutomation;
import mmarquee.automation.controls.ElementBuilder;
import mmarquee.automation.controls.Window;
import org.junit.jupiter.api.Assertions;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Parameters;
import ru.checker.tests.base.utils.CheckerTools;
import ru.checker.tests.desktop.test.entity.CheckerDesktopWindow;
import ru.checker.tests.desktop.test.temp.CheckerDesktopTest;
import ru.checker.tests.ssm.temp.widgets.SSMNavigation;
import ru.checker.tests.ssm.temp.widgets.SSMTools;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

@Log4j2
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SSMTest extends CheckerDesktopTest {

    @Getter
    CheckerDesktopWindow rootWindow;

    @Getter
    SSMNavigation navigation;

    WinDef.HWND rootHandle;

    /**
     * Init test case.
     * @param id From ID.
     */
    @Parameters({"form.id"})
    @BeforeMethod
    public void before(String id) {
        log.info("Инициализация тестового случая");
        this.rootWindow = getCurrentApp().window("ssm_main");
        this.rootWindow.maximize();
        this.navigation = this.rootWindow.widget("ssm_navigation", SSMNavigation.class);
        try {
            this.rootHandle = this.rootWindow.getControl().getNativeWindowHandle();
        } catch (AutomationException e) {
            System.out.println("##[warning] не удалось получить handle главного окна");
        }

        if(!this.getFormNeedCheckActivity(id)) {
            log.info("Открытие формы без проверки активности");
            Assertions.assertDoesNotThrow(() -> this.openFormByNavigation(id), "Не удалось открыть форму с помощью навигации");
            log.info("Навигация к форме успешно выполнена. Начало тестового случая");
            return;
        } else {

            log.info("Проверка активности формы");
            try {
                if (!this.rootWindow.checkFormExist(id))
                    throw new AutomationException("Открытие через навигацию");
            } catch (AssertionFailedError | AutomationException ex) {
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
        log.info("Закрытие всплывающих окон");
        //System.out.println(" ---- this.rootHandle = " + this.rootHandle);
        if(this.rootHandle == null) {
            return;
        }

        assertDoesNotThrow(() -> Thread.sleep(1000));
        WinDef.HWND popUpHandle = User32.INSTANCE.GetForegroundWindow();
/*        System.out.println(" ---- popUpHandle = " + popUpHandle);
        try {
            Element el0 = UIAutomation.getInstance().getElementFromHandle(popUpHandle);
            System.out.println(" ---- el0.getName() = " + el0.getName());
            System.out.println(" ---- el0.getClassName() = " + el0.getClassName());
            System.out.println(" ---- ControlType = " + ControlType.fromValue(el0.getControlType()).name());
        } catch (AutomationException e) {
            throw new RuntimeException(e);
        }*/

        if(popUpHandle == null)
            return;

        while (popUpHandle != this.rootHandle) {
            try {
                Element el = UIAutomation.getInstance().getElementFromHandle(popUpHandle);
                if(el.getControlType() == ControlType.Window.getValue()) {
                    log.debug("Закрытие окна с handle - " + popUpHandle);
                    new Window(new ElementBuilder().element(el)).close();

                    assertDoesNotThrow(() -> Thread.sleep(500));
                    popUpHandle = User32.INSTANCE.GetForegroundWindow();
                } else {
                    break;
                }

            } catch (Exception ex) {
                log.warn("Не удалось закрыть всплывающее окно c handle - '{}'", popUpHandle);
                break;
            }
        }
    }

}
