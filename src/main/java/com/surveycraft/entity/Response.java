package com.surveycraft.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "responses")
public class Response {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private Long surveyId;
    
    @Column(nullable = false)
    private Long questionId;
    
    @Column(nullable = false)
    private Long playerId;
    
    @Column(nullable = false)
    private String respuestaTexto;
    
    private Integer playerPointsEarned;
    
    private LocalDateTime fechaRespuesta;
    
    // Constructores
    public Response() {
        this.fechaRespuesta = LocalDateTime.now();
        this.playerPointsEarned = 10; // Puntos base por responder
    }
    
    public Response(Long surveyId, Long questionId, Long playerId, String respuestaTexto) {
        this();
        this.surveyId = surveyId;
        this.questionId = questionId;
        this.playerId = playerId;
        this.respuestaTexto = respuestaTexto;
    }
    
    // Getters y Setters (OBLIGATORIOS)
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public Long getSurveyId() { return surveyId; }
    public void setSurveyId(Long surveyId) { this.surveyId = surveyId; }
    
    public Long getQuestionId() { return questionId; }
    public void setQuestionId(Long questionId) { this.questionId = questionId; }
    
    public Long getPlayerId() { return playerId; }
    public void setPlayerId(Long playerId) { this.playerId = playerId; }
    
    public String getRespuestaTexto() { return respuestaTexto; }
    public void setRespuestaTexto(String respuestaTexto) { this.respuestaTexto = respuestaTexto; }
    
    public Integer getPlayerPointsEarned() { return playerPointsEarned; }
    public void setPlayerPointsEarned(Integer playerPointsEarned) { this.playerPointsEarned = playerPointsEarned; }
    
    public LocalDateTime getFechaRespuesta() { return fechaRespuesta; }
    public void setFechaRespuesta(LocalDateTime fechaRespuesta) { this.fechaRespuesta = fechaRespuesta; }
}