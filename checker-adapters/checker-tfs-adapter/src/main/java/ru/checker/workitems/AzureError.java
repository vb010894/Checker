package ru.checker.workitems;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;
import ru.checker.annotation.AzureField;

@SuperBuilder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AzureError extends AzureWorkItem {

    /**
     * get work item type.
     *
     * @return Work item type
     */
    @Override
    public String getWorkItemType() {
        return "Ошибка";
    }

    @AzureField("/fields/Microsoft.VSTS.TCM.ReproSteps")
    String reproSteps;
}
