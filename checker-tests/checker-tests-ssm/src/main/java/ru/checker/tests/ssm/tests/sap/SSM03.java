package ru.checker.tests.ssm.tests.sap;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.log4j.Log4j2;

import ru.checker.tests.desktop.test.entity.CheckerDesktopWindow;
import ru.checker.tests.ssm.controls.grid.SSMGrid;
import ru.checker.tests.ssm.controls.grid.SSMGridData;
import ru.checker.tests.ssm.temp.forms.SSMSapOrdersForm;

import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Тест ТС.SSM.03. Заказы SAP. Назначение мастера на операции.
 * @author vd.zinovev
 */
@FieldDefaults(level = AccessLevel.PRIVATE)
@Log4j2
public class SSM03 implements Runnable {

    /**
     * Родительский элемент.
     */
    final CheckerDesktopWindow root;

    /**
     * Форма.
     */
    SSMSapOrdersForm form;

    /**
     * Конструктор.
     * @param root Родительский элемент
     */
    public SSM03(CheckerDesktopWindow root) {
        this.root = root;
    }

    /**
     * Фильтр 'Не назначен' в таблице 'Производственные заказы SAP'.
     */
    SSMGrid.ConditionConfigurer notAssignedFilter = SSMGrid.ConditionConfigurer
            .builder()
            .column("Н")
            .value1("0")
            .condition1(SSMGrid.Condition.EQUAL)
            .build();

    /**
     * Получает конфигуратор фильтра по номеру SAP в таблице 'Производственные заказы SAP'.
     * @param number Номер SAP
     * @return Конфигуратор
     */
    private SSMGrid.ConditionConfigurer getSapOrderNumFilter(String number) {
        return SSMGrid.ConditionConfigurer
                .builder()
                .column("Заказ SAP")
                .value1(number)
                .condition1(SSMGrid.Condition.EQUAL)
                .build();
    }


    /**
     * Run.
     */
    @Override
    public void run() {
        this.form = this.root.form("mf", SSMSapOrdersForm.class);
        SSMGrid orders = this.form.getSapOrderGrid();
        orders.filterByGUI(notAssignedFilter, "С", "Н", "Т");
        int row = orders.getRowIndexRowByCondition("Н", Pattern.compile("0"));
        SSMGridData orderData =  orders.getDataFromRow(row);
        assertEquals(orderData.getRowSize(), 1, "Количество считанных строк больше 1 в таблице 'Производственные заказы SAP'");
        orders.clearFilter();
        String sapNumber = orderData.getColumnData("Заказ SAP").get(0);
        orders.filterByGUI(this.getSapOrderNumFilter(sapNumber), "С", "Н", "Т");
        orderData = orders.getAllData();
        assertEquals(orderData.getRowSize(), 1, "Количество считанных строк больше 1 в таблице 'Производственные заказы SAP'");

        SSMGrid masters = this.form.getMasterGrid();
        SSMGridData masterData = masters.selectAndAcceptCell(0);
        assertEquals(masterData.getRowSize(), 1, "Количество считанных строк больше 1 в таблице 'Мастера'");
        String FIO = masterData.getColumnData("Фамилия И.О.").get(0);

        SSMGrid operationGrid = this.form.getOperationGrid();
        SSMGridData operationData = operationGrid.selectAndAcceptCell(0);
        assertEquals(operationData.getRowSize(), 1, "Количество считанных строк больше 1 в таблице 'Операции'");

        this.form.clickAssign();

        orderData = orders.getDataFromRow(0);
        assertEquals(orderData.getRowSize(), 1, "Количество считанных строк больше 1 в таблице 'Производственные заказы SAP'");
        assertEquals(
                orderData.getColumnData("Н").get(0),
                "1",
                String.format("В таблице 'Производственные заказы SAP' статус заказа '%s' не поменял статус на 'Назначен'", sapNumber));

        this.form.selectPage("ПРБ");
        SSMGrid release = this.form.getProductionReleaseGrid();
        SSMGridData releaseData = release.getAllData();
        assertNotEquals(releaseData.getRowSize(), 0,"Таблица 'Задания ПРБ' пуста после назначения мастера");
        assertTrue(releaseData.getColumnData("Мастер").parallelStream().anyMatch(r -> r.equalsIgnoreCase(FIO)), "В таблице 'Задание ПРБ' отсутствует мастер, на которого назначена операция");

        operationData = operationGrid.getDataFromRow(0);
        assertTrue(operationData.getColumnData("Назначена").parallelStream().anyMatch(r -> r.equalsIgnoreCase(FIO)), "В таблице 'Операции' отсутствует мастер, на которого назначена операция");
    }
}
