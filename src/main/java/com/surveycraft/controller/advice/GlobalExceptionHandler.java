package com.surveycraft.controller.advice;

//src/main/java/com/surveycraft/controller/advice/GlobalExceptionHandler.java

import com.surveycraft.exception.SurveyNotFoundException;
import com.surveycraft.exception.ResponseValidationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {

 @ExceptionHandler(SurveyNotFoundException.class)
 public ResponseEntity<ErrorResponse> handleSurveyNotFound(SurveyNotFoundException ex) {
     ErrorResponse error = new ErrorResponse(
         HttpStatus.NOT_FOUND.value(),
         "Encuesta no encontrada",
         ex.getMessage(),
         LocalDateTime.now()
     );
     return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
 }

 @ExceptionHandler(ResponseValidationException.class)
 public ResponseEntity<ErrorResponse> handleResponseValidation(ResponseValidationException ex) {
     ErrorResponse error = new ErrorResponse(
         HttpStatus.BAD_REQUEST.value(),
         "Error de validación en respuesta",
         ex.getMessage(),
         LocalDateTime.now()
     );
     return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
 }

 @ExceptionHandler(MethodArgumentNotValidException.class)
 public ResponseEntity<ErrorResponse> handleValidationExceptions(MethodArgumentNotValidException ex) {
     Map<String, String> errors = new HashMap<>();
     ex.getBindingResult().getAllErrors().forEach((error) -> {
         String fieldName = ((FieldError) error).getField();
         String errorMessage = error.getDefaultMessage();
         errors.put(fieldName, errorMessage);
     });

     ErrorResponse error = new ErrorResponse(
         HttpStatus.BAD_REQUEST.value(),
         "Error de validación",
         errors.toString(),
         LocalDateTime.now()
     );
     return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
 }

 @ExceptionHandler(Exception.class)
 public ResponseEntity<ErrorResponse> handleGenericException(Exception ex) {
     ErrorResponse error = new ErrorResponse(
         HttpStatus.INTERNAL_SERVER_ERROR.value(),
         "Error interno del servidor",
         "Ocurrió un error inesperado",
         LocalDateTime.now()
     );
     return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
 }

 // Clase interna para la respuesta de error
 public static class ErrorResponse {
     private int status;
     private String error;
     private String message;
     private LocalDateTime timestamp;

     public ErrorResponse(int status, String error, String message, LocalDateTime timestamp) {
         this.status = status;
         this.error = error;
         this.message = message;
         this.timestamp = timestamp;
     }

     // Getters y setters
     public int getStatus() { return status; }
     public void setStatus(int status) { this.status = status; }
     
     public String getError() { return error; }
     public void setError(String error) { this.error = error; }
     
     public String getMessage() { return message; }
     public void setMessage(String message) { this.message = message; }
     
     public LocalDateTime getTimestamp() { return timestamp; }
     public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }
 }
}
