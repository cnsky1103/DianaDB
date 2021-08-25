package com.cnsky1103.sql;

import java.util.List;

import com.cnsky1103.sql.Syntax.Type;

public class Record {
    List<Value> values;

    public void set(int index, Value v) {
        values.set(index, v);
    }
}
