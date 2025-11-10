package com.surveycraft.dto;


import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

package com.surveycraft.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.List;

public class SurveyDTO {
    private Long surveyId;
    
    @NotBlank(message = "El título es obligatorio")
    @Size(min = 5, max = 255, message = "El título debe tener entre 5 y 255 caracteres")
    private String titulo;
    
    @Size(max = 1000, message = "La descripción no puede exceder 1000 caracteres")
    private String descripcion;
    
    private Long crafterId;
    private Boolean esEspecificaJuego = false;
    private Long juegoId;
    private String tipoEncuesta; // 'compra', 'analisis', 'estado', 'actualizacion', 'general'
    private String estado = "borrador";
    private Integer recompensaPoints = 10;
    
    private List<QuestionDTO> preguntas;
    
    // Getters y Setters
    public Long getSurveyId() { return surveyId; }
    public void setSurveyId(Long surveyId) { this.surveyId = surveyId; }
    
    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }
    
    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
    
    public Long getCrafterId() { return crafterId; }
    public void setCrafterId(Long crafterId) { this.crafterId = crafterId; }
    
    public Boolean getEsEspecificaJuego() { return esEspecificaJuego; }
    public void setEsEspecificaJuego(Boolean esEspecificaJuego) { this.esEspecificaJuego = esEspecificaJuego; }
    
    public Long getJuegoId() { return juegoId; }
    public void setJuegoId(Long juegoId) { this.juegoId = juegoId; }
    
    public String getTipoEncuesta() { return tipoEncuesta; }
    public void setTipoEncuesta(String tipoEncuesta) { this.tipoEncuesta = tipoEncuesta; }
    
    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
    
    public Integer getRecompensaPoints() { return recompensaPoints; }
    public void setRecompensaPoints(Integer recompensaPoints) { this.recompensaPoints = recompensaPoints; }
    
    public List<QuestionDTO> getPreguntas() { return preguntas; }
    public void setPreguntas(List<QuestionDTO> preguntas) { this.preguntas = preguntas; }
}
