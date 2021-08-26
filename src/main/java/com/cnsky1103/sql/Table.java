package com.cnsky1103.sql;

import java.util.List;
import java.util.Map;

public class Table {

    public String name; // table

    public List<Column> columns;
    private Map<Column, Integer> columnIndex;

    public Table(String name, List<Column> columns) {
        this.name = name;
        this.columns = columns;
        for (int i = 0; i < columns.size(); ++i) {
            columnIndex.put(columns.get(i), i);
        }
    }

    private int recordSize = 0; // bytes that one record contains
    /*
     * this pointer is like a cursor
     * it points to the first byte as if the file is an array
     */
    private int ptr = 0;

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
}

class Column {
    public String name;
    public Syntax.Type type;
    public boolean isPrimaryKey;
    public int length; // 4 if INT, 8 if DOUBLE, variable if CHAR
}