package ru.checker.tests.ssm;

import com.sun.jna.platform.win32.User32;
import com.sun.jna.platform.win32.WinDef;
import lombok.extern.log4j.Log4j2;
import mmarquee.automation.AutomationException;
import mmarquee.automation.ControlType;
import mmarquee.automation.Element;
import mmarquee.automation.UIAutomation;
import mmarquee.automation.controls.AutomationBase;
import mmarquee.automation.controls.Panel;
import mmarquee.uiautomation.TreeScope;
import net.sourceforge.tess4j.ITessAPI;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.*;
import org.springframework.stereotype.Component;
import ru.checker.tests.base.utils.CheckerOCRUtils;
import ru.checker.tests.desktop.base.robot.CheckerDesktopMarker;
import ru.checker.tests.ssm.base.SSMTestCase;
import ru.checker.tests.ssm.controls.grid.SSMGrid;
import ru.checker.tests.ssm.controls.grid.SSMGridData;
import ru.checker.tests.ssm.tests.SSMSapTests;

import java.awt.*;
import java.util.List;

/**
 * SSM 'SAP Orders' form testing.
 * Test case file - SSM_SAP.yaml
 * @author vd.zinovev
 */
@DisplayName("Тесты ССМ. Форма Заказы SAP")
@Component("SAP_SSM")
@Log4j2(topic = "TEST CASE")
public class SAPSSM extends SSMTestCase {

    // prepare test case.
    static {
        prepare("SSM_SAP");
    }

    /**
     * ТС.SSM.01 test
     */
    @DisplayName("ТС.SSM.01.Заказы SAP. Работа с фильтрами")
    @Test
    void ssm01() {
        SSMSapTests.SSM01(this.getRootWindow(), this.getForm());
    }

    /**
     * ТС.SSM.02 test
     */
    @DisplayName("ТС.SSM.01.Заказы SAP. Работа с фильтрами")
    @Test
    void ssm02() {
        SSMSapTests.SSM02(this.getRootWindow(), this.getForm());
    }

    /**
     * ТС.SSM.03 test
     */
    @DisplayName("ТС.SSM.01.Заказы SAP. Работа с фильтрами")
    @Test
    void ssm03() {
        SSMSapTests.SSM03(this.getRootWindow(), this.getForm());
    }

    /**
     * ТС.SSM.04 test
     */
    @DisplayName("ТС.ССМ.4.Заказы SAP. Ручное назначение мастера на операцию")
    @Test
    void ssm04() {
        SSMSapTests.SSM04(this.getRootWindow(), this.getForm());
    }

    /**
     * Draft
     */
    @DisplayName("ТС.SSM.01.Заказы SAP. Работа с фильтрами")
    @Test
    void draft() throws AWTException, AutomationException, InterruptedException {

    }


}
