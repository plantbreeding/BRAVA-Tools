package org.bravatools.exceptions;

public class BravaToolsServerError extends RuntimeException {
    public BravaToolsServerError(String message, Exception e) {
        super(message, e);
    }

    public BravaToolsServerError(String message) {
        super(message);
    }
}
