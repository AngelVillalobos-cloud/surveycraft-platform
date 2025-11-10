package com.surveycraft.exception;



import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;


@ResponseStatus(HttpStatus.NOT_FOUND)
public class SurveyNotFoundException extends RuntimeException {
    public SurveyNotFoundException(String message) {
        super(message);
    }
    
    public SurveyNotFoundException(Long surveyId) {
        super("Encuesta no encontrada con ID: " + surveyId);
    }
}
