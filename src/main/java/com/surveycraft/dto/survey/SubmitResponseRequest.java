package com.surveycraft.dto.survey;

import java.util.Map;

public class SubmitResponseRequest {
    private Map<Long, String> respuestas; // questionId -> respuestaTexto
    
    // Constructores
    public SubmitResponseRequest() {}
    
    public SubmitResponseRequest(Map<Long, String> respuestas) {
        this.respuestas = respuestas;
    }
    
    // Getters y Setters
    public Map<Long, String> getRespuestas() { return respuestas; }
    public void setRespuestas(Map<Long, String> respuestas) { this.respuestas = respuestas; }
}