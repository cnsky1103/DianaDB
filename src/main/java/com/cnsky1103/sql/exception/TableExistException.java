package com.cnsky1103.sql.exception;

public class TableExistException extends SQLException {
    private static final long serialVersionUID = 979285423330155752L;

    public TableExistException(String message) {
        super(message);
    }
}
