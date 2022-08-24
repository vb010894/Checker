package ru.checker.tests.ssm.tests.org;

import lombok.AccessLevel;
import lombok.SneakyThrows;
import lombok.experimental.FieldDefaults;
import mmarquee.automation.controls.EditBox;
import ru.checker.tests.desktop.test.entity.CheckerDesktopForm;
import ru.checker.tests.desktop.test.entity.CheckerDesktopWindow;
import ru.checker.tests.desktop.test.temp.CheckerDesktopTest;
import ru.checker.tests.ssm.widgets.SSMTools;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class SSMG0103P01 implements Runnable{

    CheckerDesktopWindow ROOT_WINDOW;
    String FORM_ID;

    public SSMG0103P01(CheckerDesktopWindow root, String formID) {
        this.ROOT_WINDOW = root;
        this.FORM_ID = formID;
    }

    @SneakyThrows
    @Override
    public void run() {

        CheckerDesktopForm org_form = this.ROOT_WINDOW.form(this.FORM_ID);
        SSMTools tools = org_form.widget("ssm_menu", SSMTools.class);

        /*
        SSMPage pages = org_form.widget("ssm_paging", SSMPage.class);
        pages.selectTab("Склады");
        pages.selectTab("Участки");
        pages.selectTab("Цеха");
*/

        tools.clickButton("org_01");

        CheckerDesktopWindow shop_details_form = CheckerDesktopTest.getCurrentApp().window("shop_details");

        shop_details_form.edit("edit_number", -1).setValue("11");
        shop_details_form.edit("edit_name",-1).setValue("22");
        shop_details_form.edit("edit_full_name",-1).setValue("33");
        shop_details_form.edit("edit_number_sap",-1).setValue("44");

        EditBox edit_otkl = shop_details_form.edit("edit_otkl", -1);
        edit_otkl.setValue("55");

/*
        assertDoesNotThrow(() -> Thread.sleep(1000));
        assertDoesNotThrow(() -> {
            Robot robot = new Robot();
            robot.keyPress(KeyEvent.VK_ENTER);
            robot.keyRelease(KeyEvent.VK_ENTER);
        });
        assertDoesNotThrow(() -> Thread.sleep(1000));
*/


        //List<EditBox> edits = shop_details_form.edits("edit_name");

        shop_details_form.button("button_ok").click();

    }
}
