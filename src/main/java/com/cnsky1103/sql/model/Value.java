package com.cnsky1103.sql.model;

import java.math.BigDecimal;

import com.cnsky1103.sql.model.Syntax.Type;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class Value implements SQLModel {
    private static final long serialVersionUID = -8748452130667022511L;

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

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Value)) {
            return false;
        }
        Value other = (Value) obj;
        if (other.type != type) {
            if (type == Type.INT && other.type == Type.DOUBLE) {
                return BigDecimal.valueOf(vINT).equals(BigDecimal.valueOf(other.vDOUBLE));
            } else if (type == Type.DOUBLE && other.type == Type.INT) {
                return BigDecimal.valueOf(vDOUBLE).equals(BigDecimal.valueOf(other.vINT));
            }
            return false;
        }
        if (type == Type.INT) {
            return vINT == other.vINT;
        } else if (type == Type.DOUBLE) {
            return BigDecimal.valueOf(vDOUBLE).equals(BigDecimal.valueOf(other.vDOUBLE));
        } else {
            return vString.equals(other.vString);
        }
    }

    @Override
    public int hashCode() {
        if (type == Type.INT) {
            return vINT;
        } else if (type == Type.DOUBLE) {
            return BigDecimal.valueOf(vDOUBLE).hashCode();
        } else {
            return vString.hashCode();
        }
    }
}
