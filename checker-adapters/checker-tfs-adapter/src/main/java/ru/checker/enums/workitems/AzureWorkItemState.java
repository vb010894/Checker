package ru.checker.enums.workitems;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Work item state.
 * @author vd.zinovev
 */
@AllArgsConstructor
@Getter
public enum AzureWorkItemState {

    Allowed("Разрешено"),
    New("Новый"),
    Active("Активный"),
    Close("Закрыто");
    String value;
}
