package ru.checker.tests.ssm.controls.grid;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import lombok.extern.log4j.Log4j2;

import java.util.*;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Table data.
 * @author vd.zinovev
 */
@Log4j2(topic = "TEST CASE")
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@SuppressWarnings("unused")
public class SSMGridData {

    /**
     * String data.
     */
    final String stringData;

    /**
     * Table headers.
     */
    List<String> headers = new LinkedList<>();

    /**
     * Mapped data.
     */
    Map<String, List<String>> mappedData = new LinkedHashMap<>();

    /**
     * Row size.
     */
    long rowSize = 0;

    /**
     * Header size.
     */
    long headerSize = 0;

    /**
     * Grid data
     * @param data String data
     */
    public SSMGridData(String data) {
        this.stringData = data;
    }

    /**
     * Convert string data to mapped data,
     * with 1 row columns.
     */
    public void convert() {
        this.convert(1);
    }

    /**
     * Convert string data to mapped data.
     * @param headerCount Header count
     */
    public void convert(int headerCount) {
        if(this.stringData.equals(""))
            return;
        String[] rows = this.stringData.split("\n");
        assertNotNull(rows, "Не удалось получить данные из таблицы");
        assertTrue(rows.length > 0, "Не удалось получить данные из таблицы");
        this.rowSize = rows.length - 1;
        for (int i = 0; i < rows.length; i++) {
            if (headerCount > 0) {
                if (i == headerCount - 1) {
                    this.addHeader(rows[i].split("\t"));
                    continue;
                }
            } else {
                String[] data = new String[rows[i].split("\t").length - 1];
                for (int j = 0; j < rows[i].split("\t").length; j++) {
                    data[j] = String.valueOf(j);
                }
                this.addHeader(data);
                continue;
            }
            this.addData(rows[i].split("\t"));
        }
    }

    /**
     * Add table data.
     *
     * @param row Data row
     */
    private void addData(String[] row) {
        for (int i = 0; i < row.length; i++) {
            this.mappedData.get(this.headers.get(i)).add(row[i]);
        }
    }

    /**
     * Add headers to table.
     *
     * @param headers Header array
     */
    private void addHeader(String[] headers) {
        log.debug("Заполняем заголовки");
        log.debug("Обнаружено {} заголовков", (headerSize = headers.length));
        Arrays.stream(headers).parallel().forEachOrdered(header -> {
            this.headers.add(header);
            this.mappedData.put(header, new LinkedList<>());
        });
        log.debug("Заголовки добавлены");
    }

    /**
     * Column data.
     * @param columnName Column name
     * @return  Column data
     */
    public List<String> getColumnData(String columnName) {
        return this.mappedData.get(columnName);
    }

    /**
     * Column data by index
     * @param index Column index
     * @return Column data
     */
    public List<String> getColumnDataByIndex(int index) {
        return this.mappedData.get(this.headers.get(index));
    }

}
