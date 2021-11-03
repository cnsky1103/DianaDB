package com.cnsky1103.sql.exception;

public class TableNotFoundException extends SQLException {
    private static final long serialVersionUID = -3455221442752206476L;

    public TableNotFoundException(String message) {
        super(message);
    }
}
