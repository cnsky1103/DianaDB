package com.cnsky1103.sql.model;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class Column implements SQLModel {
    public String name;
    public Syntax.Type type;
    public boolean isPrimaryKey;
    public int length; // 4 if INT, 8 if DOUBLE, variable if CHAR

    public Column(String name, Syntax.Type type, boolean isPrimaryKey) {
        this.name = name;
        this.type = type;
        this.isPrimaryKey = isPrimaryKey;

        if (type.equals(Syntax.Type.INT)) {
            this.length = 4;
        } else if (type.equals(Syntax.Type.DOUBLE)) {
            this.length = 8;
        } else {
            this.length = 255; //default length of type char
        }
    }
}
