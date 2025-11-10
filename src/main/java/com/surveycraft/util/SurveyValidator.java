package com.surveycraft.util;



import com.surveycraft.entity.Survey;
import org.springframework.stereotype.Component;

@Component
public class SurveyValidator {
    
    public void validarSurvey(Survey survey) {
        if (survey.getTitulo() == null || survey.getTitulo().trim().isEmpty()) {
            throw new IllegalArgumentException("El título de la encuesta es obligatorio");
        }
        
        if (survey.getTitulo().length() < 5) {
            throw new IllegalArgumentException("El título debe tener al menos 5 caracteres");
        }
        
        // Más validaciones según necesites
    }
}