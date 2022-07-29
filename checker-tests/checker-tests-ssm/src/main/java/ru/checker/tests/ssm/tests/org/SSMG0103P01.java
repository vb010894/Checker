package ru.checker.tests.ssm.tests.org;

import lombok.AccessLevel;
import lombok.SneakyThrows;
import lombok.experimental.FieldDefaults;
import ru.checker.tests.desktop.test.entity.CheckerDesktopForm;
import ru.checker.tests.desktop.test.entity.CheckerDesktopWindow;
import ru.checker.tests.ssm.temp.widgets.SSMPage;
import ru.checker.tests.ssm.temp.widgets.SSMTools;

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

        SSMPage pages = org_form.widget("ssm_paging", SSMPage.class);
        pages.selectTab("Склады");
        pages.selectTab("Участки");
        pages.selectTab("Цеха");


        tools.clickButton("org_01");

        CheckerDesktopForm shop_details_form = this.ROOT_WINDOW.form("shop_details");
        shop_details_form.button("button_cancel").click();

    }
}
