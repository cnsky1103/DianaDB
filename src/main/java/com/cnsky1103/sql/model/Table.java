package com.cnsky1103.sql.model;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import com.cnsky1103.Config;
import com.cnsky1103.sql.exception.SQLModelException;
import com.cnsky1103.sql.memory.MemoryManager;
import com.cnsky1103.sql.model.Syntax.Type;

import lombok.Getter;
import lombok.Setter;

public class Table implements SQLModel {

    @Getter
    private String name;

    @Getter
    private ArrayList<Column> columns;

    // 在select等需要指定列的语句中，需要将列名转换成record中的序号
    @Getter
    private transient Map<String, Integer> columnIndex;

    private transient Map<String, Column> columnName;

    // bytes that one record contains
    private int recordSize = 0;

    // a cursor which points to next byte
    private int ptr = 0;

    @Getter
    @Setter
    private int primaryKeyIndex;

    public Table(String name, ArrayList<Column> columns) {
        this.name = name;
        this.columns = columns;
        columnIndex = new HashMap<>();
        columnName = new HashMap<>();
        for (int i = 0; i < columns.size(); ++i) {
            columnIndex.put(columns.get(i).name, i);
            if (columns.get(i).isPrimaryKey) {
                primaryKeyIndex = i;
            }
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
            // 第1位是有效位，后4位存下一条记录的地址
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
     * @throws SQLException 传入的数组长度不符
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
        record.setValid(buffer.get());
        // 再读一个int，偏移量
        record.setNext(buffer.getInt());

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

    public int getColumnIndex(String columnName) {
        return columnIndex.get(columnName);
    }

    public int getColumnIndex(Column column) {
        return columnIndex.get(column.name);
    }

    public void reset() {
        ptr = 0;
    }

    public synchronized Record getNext() throws SQLModelException, IOException {
        while (true) {
            byte[] b = MemoryManager.readARecord(this, ptr);
            if (Objects.nonNull(b)) {
                if (b[0] == Config.ValidByte) {
                    Record record = convertToRecord(b);
                    ptr = record.getNext();
                    return record;
                } else {
                    ptr += getRecordSize();
                }
            } else {
                return null;
            }
        }
    }

    public synchronized void write(Record record) throws IOException {
        // TODO 需要处理下一条记录地址的问题，否则这个函数没法用
        MemoryManager.writeARecord(this, record, record.getNext());
    }

    public synchronized void insert(Instruction ins) throws IOException, SQLModelException {
        reset();
        Record record = new Record(this);
        record.setAll(ins.getValues().toArray(new Value[0]));
        record.setNext(0);
        record.setValid(Config.ValidByte);
        while (true) {
            byte[] b = MemoryManager.readARecord(this, ptr);
            if (Objects.nonNull(b)) {
                Record curRecord = convertToRecord(b);
                if (curRecord.getValues()[primaryKeyIndex].equals(record.getValues()[primaryKeyIndex])) {
                    throw new SQLModelException("key " + columns.get(primaryKeyIndex).name + " already exists");
                }
                ptr += getRecordSize();
            } else {
                b = MemoryManager.readARecord(this, ptr - getRecordSize());
                Record lastRecord = convertToRecord(b);
                lastRecord.setNext(ptr);
                MemoryManager.writeARecord(this, lastRecord, ptr - getRecordSize());
                MemoryManager.writeARecord(this, record, ptr);
            }
        }
    }
}
