package com.cnsky1103.sql.model;

import com.cnsky1103.sql.model.Syntax.Type;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class Value implements SQLModel{
    private Syntax.Type type;
    private int vINT;
    private double vDOUBLE;
    private String vString;

    public Value(int vINT) {
        this.type = Type.INT;
        this.vINT = vINT;
    }

    public Value(double vDOUBLE) {
        this.type = Type.DOUBLE;
        this.vDOUBLE = vDOUBLE;
    }

    public Value(String vString) {
        this.type = Type.CHAR;
        this.vString = vString;
    }

    @Override
    public String toString() {
        return type == Type.INT ? String.valueOf(vINT) : (type == Type.DOUBLE ? String.valueOf(vDOUBLE) : vString);
    }
}
