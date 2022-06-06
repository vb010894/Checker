package ru.checker.workitems;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;
import lombok.extern.log4j.Log4j2;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.checker.AzureConnection;
import ru.checker.annotation.AzureField;
import ru.checker.enums.workitems.AzureActivity;
import ru.checker.enums.workitems.AzureWorkItemState;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

@Component
@Log4j2
@FieldDefaults(level = AccessLevel.PRIVATE)
@SuperBuilder
public abstract class AzureWorkItem {

    private AzureConnection connection;

    @Autowired
    public void setConnection(AzureConnection connection) {
        this.connection = connection;
    }

    /**
     * get work item type.
     * @return Work item type
     */
    abstract String getWorkItemType();

    @AzureField("/fields/System.Title")
    String title;

    @AzureField("/fields/System.AreaPath")
    String area;

    @AzureField("/fields/System.IterationPath")
    String iteration;

    @AzureField("/fields/System.State")
    AzureWorkItemState state;

    @AzureField("/fields/System.Reason")
    String reason;

    @AzureField("/fields/System.AssignedTo")
    String assignedTo;

    @AzureField("/fields/Microsoft.VSTS.Common.Priority")
    Integer priority;

    @AzureField("/fields/Microsoft.VSTS.Common.Severity")
    String severity;

    @AzureField("/fields/Microsoft.VSTS.Common.Activity")
    AzureActivity activity;

    List<File> attachment = new LinkedList<>();

    public void create() {
        List<Field> fields = new ArrayList<>();
        Collections.addAll(fields, this.getClass().getDeclaredFields());
        Collections.addAll(fields, this.getClass().getSuperclass().getDeclaredFields());
        AtomicReference<JSONArray> result = new AtomicReference<>(new JSONArray());
        fields.stream()
                .parallel()
                .filter(field -> field.isAnnotationPresent(AzureField.class))
                .forEach(field -> this.createFieldJson(field, result));
        try {
            System.out.println(this.sendCreateRequest(result.get().toString()).body().string());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Response sendCreateRequest(String body) {
        String url = this.connection.getUrl()
                + "/" + this.connection.getCollection()
                + "/" + this.connection.getProject()
                + "/_apis/wit/workitems?" +
                "type=" + this.getWorkItemType() +
                "&api-version=6.0";

        Request request = this.connection.getRequest()
                .header("Content-Type", "application/json-patch+json")
                .url(url)
                .post(RequestBody.create(
                        MediaType.parse("application/json-patch+json"),
                        body))
                .build();

        return this.connection.getResponse(request);
    }

    public void createFieldJson(Field field, AtomicReference<JSONArray> result) {
        JSONObject temp = new JSONObject();
        field.setAccessible(true);
        try {
            String path = field.getAnnotation(AzureField.class).value();
            Object value = field.get(this);
            if(value != null) {
                temp.put("op", "add");
                temp.put("path", path);
                temp.put("from", (String) null);
                temp.put("value", value);
                result.get().put(temp);
            }
        } catch (IllegalAccessException e) {
            log.error("Не удалось создать ошибку в azure");
        }
        field.setAccessible(false);
    }

}
