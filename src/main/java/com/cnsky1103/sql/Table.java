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
            /** 前四位保存下一个record在文件中是第几条 */
            recordSize = size + 4;
            return recordSize;
        }
    }
}

class Column {
    public String name;
    public Syntax.Type type;
    public boolean isPrimaryKey;
    public int length; // only meaningful when type == CHAR
}