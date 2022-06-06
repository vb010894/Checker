package ru.checker.enums.workitems;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Azure activity.
 * @author vd.zinovev
 */
@AllArgsConstructor
@Getter
public enum AzureActivity {

    Requirement("Требования"),
    Project("Проектирование"),
    Development("Разработка"),
    Documentation("Документация"),
    Testing("Тестирование"),
    Deployment("Развертывание");

    String value;
}
