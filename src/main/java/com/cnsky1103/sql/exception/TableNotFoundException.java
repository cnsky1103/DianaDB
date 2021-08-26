package com.cnsky1103.sql.exception;

public class TableNotFoundException extends SQLException {
    public TableNotFoundException(String message) {
        super(message);
    }
}
