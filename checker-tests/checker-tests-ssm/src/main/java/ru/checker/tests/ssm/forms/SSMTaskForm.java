package ru.checker.tests.ssm.forms;

import lombok.Getter;
import ru.checker.tests.desktop.test.entity.CheckerDesktopForm;
import ru.checker.tests.ssm.widgets.SSMTools;

import java.awt.*;
import java.util.List;
import java.util.Map;

public class SSMTaskForm {

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
     * Конструктор.
     * @param form Форма
     */
    public SSMTaskForm(CheckerDesktopForm form) {
        this.form = form;
        this.tools = this.form.widget("ssm_menu", SSMTools.class);
    }

}
