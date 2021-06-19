package com.cnsky1103.sql.Exception;

public class TableNotFoundException extends SQLException {
    public TableNotFoundException(String message) {
        super(message);
    }
}
