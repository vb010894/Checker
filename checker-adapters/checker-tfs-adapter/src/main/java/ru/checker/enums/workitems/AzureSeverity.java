package ru.checker.enums.workitems;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Azure Severity.
 * @author vd.zinovev
 */
@AllArgsConstructor
@Getter
public enum AzureSeverity {
    Critical("1 - критическая"),
    High("2 - высокая"),
    Middle("3 - средняя"),
    Low("4 - низкая");
    String value;
}
