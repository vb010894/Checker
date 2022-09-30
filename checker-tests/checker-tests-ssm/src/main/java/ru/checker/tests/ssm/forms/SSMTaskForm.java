package ru.checker.tests.ssm.forms;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import lombok.extern.log4j.Log4j2;
import ru.checker.tests.desktop.test.entity.CheckerDesktopForm;
import ru.checker.tests.desktop.test.temp.CheckerDesktopTest;
import ru.checker.tests.desktop.utils.CheckerDesktopMarker;
import ru.checker.tests.ssm.controls.grid.SSMGrid;
import ru.checker.tests.ssm.widgets.SSMTools;
import ru.checker.tests.ssm.windows.task.TaskFilter;

import java.awt.*;
import java.util.List;
import java.util.Map;

/**
 * Форма "Управления заданиями".
 *
 * Файл - "Forms/TASK_CONTROL.yaml".
 * ID - "task_control"
 */
@Log4j2
@FieldDefaults(level = AccessLevel.PRIVATE)
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

    /**
     * Получение таблицы "Задания".
     *
     * ID - "task_control_tasks_table".
     *
     * @return Таблицы "Задания"
     */
    public SSMGrid getTaskGrid() {
        SSMGrid grid =  this.form.custom("task_control_tasks_table", SSMGrid.class);
        return grid;
    }

    /**
     * Вызывает окно фильтрации.
     * @return Окно фильтрации
     */
    public TaskFilter callTaskFilter() {
        log.info("Вызов окна фильтрации модуля 'Управления заданиями'");
        this.tools.clickButton("sap_filter");
        TaskFilter filter = CheckerDesktopTest.getCurrentApp().window("TASK_FILTER_WINDOW", TaskFilter.class);
        log.info("Окно фильтрации вызвано");
        return filter;
    }

}
