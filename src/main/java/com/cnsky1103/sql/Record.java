package com.cnsky1103.sql;

import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.List;

import com.cnsky1103.sql.Syntax.Type;

public class Record {
    List<Value> values;

    public void set(int index, Value v) {
        values.set(index, v);
    }

    public byte[] toBytes(Table table, int next) {
        ByteBuffer bbf = ByteBuffer.allocate(table.getRecordSize());
        bbf.put((byte) 0b00000001); //valid bit
        bbf.putInt(next); // points to next record
        for (int i = 0; i < table.columns.size(); ++i) {
            Column c = table.columns.get(i);
            if (c.type == Type.INT) {
                bbf.putInt(values.get(i).vINT);
            } else if (c.type == Type.DOUBLE) {
                bbf.putDouble(values.get(i).vDOUBLE);
            } else {
                bbf.put(Arrays.copyOf(values.get(i).vString.getBytes(), c.length));
            }
        }

        return bbf.array();
    }

    public byte[] toBytes(Table table) {
        ByteBuffer bbf = ByteBuffer.allocate(table.getRecordSize());
        bbf.put((byte) 0b00000001); //valid bit
        for (int i = 0; i < table.columns.size(); ++i) {
            Column c = table.columns.get(i);
            if (c.type == Type.INT) {
                bbf.putInt(values.get(i).vINT);
            } else if (c.type == Type.DOUBLE) {
                bbf.putDouble(values.get(i).vDOUBLE);
            } else {
                bbf.put(Arrays.copyOf(values.get(i).vString.getBytes(), c.length));
            }
        }

        return bbf.array();
    }
}
