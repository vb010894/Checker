package ru.checker.tests.ssm.tests.org;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import ru.checker.tests.desktop.test.entity.CheckerDesktopForm;
import ru.checker.tests.desktop.test.entity.CheckerDesktopWindow;
import ru.checker.tests.ssm.temp.widgets.SSMTools;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class SSMG0103P01 implements Runnable{

    CheckerDesktopWindow ROOT_WINDOW;
    String FORM_ID;

    public SSMG0103P01(CheckerDesktopWindow root, String formID) {
        this.ROOT_WINDOW = root;
        this.FORM_ID = formID;
    }

    @Override
    public void run() {

        CheckerDesktopForm org_form = this.ROOT_WINDOW.form(this.FORM_ID);
        SSMTools tools = org_form.widget("ssm_menu", SSMTools.class);

        tools.clickButton("org_01");

    }
}
