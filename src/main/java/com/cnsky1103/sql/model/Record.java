package com.cnsky1103.sql.model;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.cnsky1103.sql.model.Syntax.Type;

public class Record implements SQLModel{
    List<Value> values;

    public Record() {
        values = new ArrayList<>();
    }

    public void set(int index, Value v) {
        values.set(index, v);
    }

    public byte[] toBytes(Table table, int next) {
        ByteBuffer bbf = ByteBuffer.allocate(table.getRecordSize());
        bbf.put((byte) 0b00000001); // valid bit
        bbf.putInt(next); // points to next record
        for (int i = 0; i < table.getColumns().size(); ++i) {
            Column c = table.getColumns().get(i);
            if (c.type == Type.INT) {
                bbf.putInt(values.get(i).getVINT());
            } else if (c.type == Type.DOUBLE) {
                bbf.putDouble(values.get(i).getVDOUBLE());
            } else {
                bbf.put(Arrays.copyOf(values.get(i).getVString().getBytes(), c.length));
            }
        }

        return bbf.array();
    }

    public byte[] toBytes(Table table) {
        ByteBuffer bbf = ByteBuffer.allocate(table.getRecordSize());
        bbf.put((byte) 0b00000001); // valid bit
        List<Column> columns = table.getColumns();
        for (int i = 0; i < columns.size(); ++i) {
            Column c = columns.get(i);
            if (c.type == Type.INT) {
                bbf.putInt(values.get(i).getVINT());
            } else if (c.type == Type.DOUBLE) {
                bbf.putDouble(values.get(i).getVDOUBLE());
            } else {
                bbf.put(Arrays.copyOf(values.get(i).getVString().getBytes(), c.length));
            }
        }

        return bbf.array();
    }
}
