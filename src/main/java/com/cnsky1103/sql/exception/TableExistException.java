package com.cnsky1103.sql.exception;

public class TableExistException extends SQLException {
    public TableExistException(String message) {
        super(message);
    }
}
