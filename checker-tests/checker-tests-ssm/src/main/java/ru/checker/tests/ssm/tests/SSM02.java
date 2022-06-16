package ru.checker.tests.ssm.tests;

import ru.checker.tests.desktop.test.entity.CheckerDesktopWindow;
import ru.checker.tests.ssm.controls.grid.SSMGrid;
import ru.checker.tests.ssm.controls.grid.SSMGridData;
import ru.checker.tests.ssm.temp.forms.SSMSapOrdersForm;

public class SSM02 implements Runnable {

    final CheckerDesktopWindow root;

    SSMSapOrdersForm form;

    public SSM02(CheckerDesktopWindow root) {
        this.root = root;
    }

    /**
     * When an object implementing interface {@code Runnable} is used
     * to create a thread, starting the thread causes the object's
     * {@code run} method to be called in that separately executing
     * thread.
     * <p>
     * The general contract of the method {@code run} is that it may
     * take any action whatsoever.
     *
     * @see Thread#run()
     */
    @Override
    public void run() {
        this.form = this.root.form("mf", SSMSapOrdersForm.class);
        SSMGrid ordersGrid = this.form.getSapOrderGrid();
        SSMGridData acceptedOrder = ordersGrid.selectAndAcceptCell(0);
        SSMGrid masterGrid = this.form.getMasterGrid();
        SSMGridData acceptedMaster = masterGrid.selectAndAcceptCell(0);
        this.form.clickAssign();
        this.form.selectPage("ПРБ");
        // TODO: 16.06.2022 Уточнить сценрий !!! 
    }
}
