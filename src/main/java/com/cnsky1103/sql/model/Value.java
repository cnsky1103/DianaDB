package com.cnsky1103.sql.model;

import com.cnsky1103.sql.model.Syntax.Type;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Value implements SQLModel{
    private Syntax.Type type;
    private int vINT;
    private double vDOUBLE;
    private String vString;

    @Override
    public String toString() {
        return type == Type.INT ? String.valueOf(vINT) : (type == Type.DOUBLE ? String.valueOf(vDOUBLE) : vString);
    }
}
