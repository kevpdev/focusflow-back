package fr.focusflow.exceptions;

import lombok.Data;

import java.util.Date;

@Data
public class ErrorDetails {
    private Date timestamp;
    private int statusCode;
    private String message;
    private Object details;
    private String errorCode;  // Code d'erreur spécifique


    public ErrorDetails(int statusCode, String message, Object details) {
        this.timestamp = new Date();
        this.statusCode = statusCode;
        this.message = message;
        this.details = details;
    }

    public ErrorDetails(int statusCode, String message, Object details, String errorCode) {
        this(statusCode, message, details);
        this.errorCode = errorCode;  // Ajout du code d'erreur spécifique
    }
}
