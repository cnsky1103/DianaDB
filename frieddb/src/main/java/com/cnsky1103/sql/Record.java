package com.cnsky1103.sql;

import java.util.List;

import com.cnsky1103.sql.Syntax.Type;

public class Record {
    List<Value> values;

    public void set(int index, Value v) {
        values.set(index, v);
    }
}

class Value {
    Syntax.Type type;
    int vINT;
    double vDOUBLE;
    String vString;

    @Override
    public String toString() {
        return type == Type.INT ? String.valueOf(vINT) : (type == Type.DOUBLE ? String.valueOf(vDOUBLE) : vString);
    }
}