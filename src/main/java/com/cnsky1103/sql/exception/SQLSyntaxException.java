package com.cnsky1103.sql.exception;

public class SQLSyntaxException extends SQLException {
    private static final long serialVersionUID = -4111761503345653151L;

    public SQLSyntaxException(String message) {
        super(message);
    }
}
