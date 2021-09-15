package com.cnsky1103.sql.model;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;

import com.cnsky1103.Config;
import com.cnsky1103.sql.model.Syntax.Type;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Record implements SQLModel{
    private Value[] values;

    private transient String tableName;

    // which table this record belongs to
    private transient Table table;

    private byte valid;
    private int next;

    public Record(Table table) {
        this.table = table;
        values = new Value[table.getColumns().size()];
    }

    public void set(int index, Value v) {
        values[index]=v;
    }

    // TODO 是否要增加错误判断？
    public void setAll(Value[] values) {
        this.values = values;
    }

    public byte[] toBytes(int nextOffset) {
        ByteBuffer bbf = ByteBuffer.allocate(table.getRecordSize());
        bbf.put(Config.ValidByte); // valid bit
        bbf.putInt(nextOffset); // points to next record
        this.next = nextOffset;
        for (int i = 0; i < table.getColumns().size(); ++i) {
            Column c = table.getColumns().get(i);
            if (c.type == Type.INT) {
                bbf.putInt(values[i].getVINT());
            } else if (c.type == Type.DOUBLE) {
                bbf.putDouble(values[i].getVDOUBLE());
            } else {
                bbf.put(Arrays.copyOf(values[i].getVString().getBytes(), c.length));
            }
        }

        return bbf.array();
    }

    /**
     * @deprecated
     * @see toBytes(int next)
     * @return
     */
    public byte[] toBytes() {
        ByteBuffer bbf = ByteBuffer.allocate(table.getRecordSize());
        bbf.put(Config.ValidByte); // valid bit
        ArrayList<Column> columns = table.getColumns();
        for (int i = 0; i < columns.size(); ++i) {
            Column c = columns.get(i);
            if (c.type == Type.INT) {
                bbf.putInt(values[i].getVINT());
            } else if (c.type == Type.DOUBLE) {
                bbf.putDouble(values[i].getVDOUBLE());
            } else {
                bbf.put(Arrays.copyOf(values[i].getVString().getBytes(), c.length));
            }
        }

        return bbf.array();
    }
}
