package com.cnsky1103.sql.model;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.cnsky1103.sql.exception.SQLModelException;
import com.cnsky1103.sql.model.Syntax.Type;

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

    /**
     * 将从内存中读到的字符数组转换成本表的一条记录
     * @param b 一条记录的byte数组表示
     * @return 该记录的Record表示
     */
    public Record convertToRecord(byte[] b) throws SQLModelException {
        if (b.length != getRecordSize()) {
            throw new SQLModelException("cannot convert byte array with length " + b.length
                    + " to a record with length " + getRecordSize());
        }
        Record record = new Record(this);
        record.setTableName(this.name);
        ByteBuffer buffer = ByteBuffer.allocate(b.length);
        buffer.put(b);

        // 切换为读取模式
        buffer.flip();

        // 读第一个字节，有效位
        buffer.get();
        // 再读一个int，偏移量
        buffer.getInt();

        // 记录的列号
        int index = 0;
        for (Column c : columns) {
            if (c.type == Type.INT) {
                record.set(index, new Value(buffer.getInt()));
            } else if (c.type == Type.DOUBLE) {
                record.set(index, new Value(buffer.getDouble()));
            } else {
                byte[] stringByte = new byte[c.length];
                buffer.get(stringByte, 0, c.length);
                record.set(index, new Value(new String(stringByte)));
            }
            ++index;
        }

        return record;
    }
}
