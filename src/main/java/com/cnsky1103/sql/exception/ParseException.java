package com.cnsky1103.sql.exception;

public class ParseException extends SQLException {
    private static final long serialVersionUID = -6734107482720711932L;

    public ParseException(String message) {
        super(message);
    }
}
