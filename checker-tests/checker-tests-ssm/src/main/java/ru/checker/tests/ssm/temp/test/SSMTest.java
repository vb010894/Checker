package ru.checker.tests.ssm.temp.test;

import com.sun.jna.platform.win32.User32;
import com.sun.jna.platform.win32.WinDef;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import mmarquee.automation.AutomationException;
import mmarquee.automation.ControlType;
import mmarquee.automation.Element;
import mmarquee.automation.UIAutomation;
import mmarquee.automation.controls.ElementBuilder;
import mmarquee.automation.controls.Window;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Parameters;
import ru.checker.tests.desktop.test.entity.CheckerDesktopForm;
import ru.checker.tests.desktop.test.entity.CheckerDesktopWindow;
import ru.checker.tests.desktop.test.temp.CheckerDesktopTest;
import ru.checker.tests.ssm.temp.widgets.SSMNavigation;
import ru.checker.tests.ssm.temp.widgets.SSMTools;

@FieldDefaults(level = AccessLevel.PRIVATE)
public class SSMTest extends CheckerDesktopTest {

    @Getter
    CheckerDesktopWindow rootWindow;

    @Getter
    SSMNavigation navigation;

    WinDef.HWND rootHandle;

    @Parameters({"form.id", "form.navigation"})
    @BeforeMethod
    public void before(String id, String nav) {
        this.rootWindow = getCurrentApp().window("ssm_main");
        this.rootWindow.maximize();
        try {
            this.rootHandle = this.rootWindow.getControl().getNativeWindowHandle();
        } catch (AutomationException e) {
            System.out.println("##[warning] не удалось получить handle главного окна");
        }
        this.navigation = this.rootWindow.widget("ssm_navigation", SSMNavigation.class);
        this.navigation.selectNode(SSMNavigation.SSMNavigationEnum.valueOf(nav));
    }

    @AfterMethod
    public void after() {
        this.closePopUps();
        this.closeForm();
    }

    private void closeForm() {
        SSMTools tools = this.rootWindow.widget("ssm_menu", SSMTools.class);
        navigation.selectRoot();
        tools.clickButton("info_exit");
    }

    private void closePopUps() {
        if(this.rootHandle == null) {
            return;
        }

        WinDef.HWND popUpHandle = User32.INSTANCE.GetForegroundWindow();
        if(popUpHandle == null)
            return;

        while (popUpHandle != this.rootHandle) {
            try {
                Element el = UIAutomation.getInstance().getElementFromHandle(popUpHandle);
                if(el.getControlType() == ControlType.Window.getValue()) {
                    new Window(new ElementBuilder().element(el)).close();
                } else {
                    break;
                }

            } catch (Exception ex) {
                System.out.println("##[error] Не удалось закрыть всплывающее окно");
                break;
            }
        }
    }

}
