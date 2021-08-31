package com.cnsky1103.sql.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import lombok.Getter;

public class Table implements SQLModel {

    @Getter
    private String name; // table

    @Getter
    private ArrayList<Column> columns;

    // TODO: 这玩意有啥用啊
    private transient Map<String, Integer> columnIndex;

    private transient Map<String, Column> columnName;

    private int recordSize = 0; // bytes that one record contains
    /*
     * This pointer is like a cursor.
     * It points to the first byte as if the file is an array
     */
    private int ptr = 0;

    public Table(String name, ArrayList<Column> columns) {
        this.name = name;
        this.columns = columns;
        columnIndex = new HashMap<>();
        columnName = new HashMap<>();
        for (int i = 0; i < columns.size(); ++i) {
            columnIndex.put(columns.get(i).name, i);// why
        }
        for (Column c : columns) {
            columnName.put(c.name, c);
        }
    }

    public synchronized int getRecordSize() {
        if (recordSize != 0) {
            return recordSize;
        } else {
            int size = 0;
            for (Column column : columns) {
                if (column.type == Syntax.Type.INT)
                    size += 4;
                else if (column.type == Syntax.Type.DOUBLE)
                    size += 8;
                else
                    size += column.length;
            }
            /** 第1位是有效位，后4位存下一条记录的地址 */
            recordSize = size + 1 + 4;
            return recordSize;
        }
    }

    public Column getColumnByName(String name) {
        return columnName.getOrDefault(name, null);
    }
}
