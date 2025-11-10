package com.surveycraft.dto;



import javax.validation.constraints.NotNull;

public class ResponseDTO {
    private Long responseId;
    
    @NotNull(message = "Player ID es obligatorio")
    private Long playerId;
    
    @NotNull(message = "Question ID es obligatorio")
    private Long questionId;
    
    private Long optionId;
    private String respuestaTexto;
    private String tiempoJugado = "no_jugado"; // 'no_jugado', '0-10h', '10-50h', '50+h'
    private String plataforma;
    private Integer puntosGanados = 10;
    
    // Getters y Setters
    public Long getResponseId() { return responseId; }
    public void setResponseId(Long responseId) { this.responseId = responseId; }
    
    public Long getPlayerId() { return playerId; }
    public void setPlayerId(Long playerId) { this.playerId = playerId; }
    
    public Long getQuestionId() { return questionId; }
    public void setQuestionId(Long questionId) { this.questionId = questionId; }
    
    public Long getOptionId() { return optionId; }
    public void setOptionId(Long optionId) { this.optionId = optionId; }
    
    public String getRespuestaTexto() { return respuestaTexto; }
    public void setRespuestaTexto(String respuestaTexto) { this.respuestaTexto = respuestaTexto; }
    
    public String getTiempoJugado() { return tiempoJugado; }
    public void setTiempoJugado(String tiempoJugado) { this.tiempoJugado = tiempoJugado; }
    
    public String getPlataforma() { return plataforma; }
    public void setPlataforma(String plataforma) { this.plataforma = plataforma; }
    
    public Integer getPuntosGanados() { return puntosGanados; }
    public void setPuntosGanados(Integer puntosGanados) { this.puntosGanados = puntosGanados; }
}