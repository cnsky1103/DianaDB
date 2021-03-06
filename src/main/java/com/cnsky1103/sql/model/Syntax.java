package com.cnsky1103.sql.model;

public class Syntax implements SQLModel {
    private static final long serialVersionUID = -5433793115882496403L;

    public static enum Operator {
        SELECT, UPDATE, CREATE, DELETE, INSERT
    }

    public static enum CompareOp {
        lt("<") {
            @Override
            public <T extends Comparable<T>> boolean compare(T v1, T v2) {
                return v1.compareTo(v2) < 0;
            }
        },
        gt(">") {
            @Override
            public <T extends Comparable<T>> boolean compare(T v1, T v2) {
                return v1.compareTo(v2) > 0;
            }
        },
        le("<=") {
            @Override
            public <T extends Comparable<T>> boolean compare(T v1, T v2) {
                return v1.compareTo(v2) <= 0;
            }
        },
        ge("<") {
            @Override
            public <T extends Comparable<T>> boolean compare(T v1, T v2) {
                return v1.compareTo(v2) >= 0;
            }
        },
        eq("=") {
            @Override
            public <T extends Comparable<T>> boolean compare(T v1, T v2) {
                return v1.compareTo(v2) == 0;
            }
        },
        ne("!=") {
            @Override
            public <T extends Comparable<T>> boolean compare(T v1, T v2) {
                return v1.compareTo(v2) != 0;
            }
        };

        private String op;

        private CompareOp(String op) {
            this.op = op;
        }

        public abstract <T extends Comparable<T>> boolean compare(T v1, T v2);

        public CompareOp get(String s) {
            for (CompareOp c : CompareOp.values()) {
                if (c.op.equals(s)) {
                    return c;
                }
            }
            return null;
        }
    }

    public static enum Type {
        INT, DOUBLE, CHAR
    }
}
