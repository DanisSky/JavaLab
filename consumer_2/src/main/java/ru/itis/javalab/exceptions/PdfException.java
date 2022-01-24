package ru.itis.javalab.exceptions;

public class PdfException extends RuntimeException {
    public PdfException(String message, Throwable cause) {
        super(message, cause);
    }
}
