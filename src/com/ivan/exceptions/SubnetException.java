package com.ivan.exceptions;

public class SubnetException extends Exception {
    public SubnetException(String message) {
        super(message);
    }

    public SubnetException(Throwable cause) {
        super(cause);
    }

    public SubnetException(String message, Throwable cause) {
        super(message, cause);
    }
}
