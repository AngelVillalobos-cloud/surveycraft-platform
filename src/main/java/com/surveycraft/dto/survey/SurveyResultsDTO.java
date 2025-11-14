package com.surveycraft.dto.survey;

import java.time.LocalDateTime;
import java.util.Map;

public class SurveyResultsDTO {
    private Long id;
    private String titulo;
    private String descripcion;
    private String estado;
    private Long crafterId;
    private LocalDateTime fechaCreacion;
    private Map<Long, Map<String, Integer>> resultados; // questionId -> (opcion -> conteo)
    
    // Getters y Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }
    
    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
    
    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
    
    public Long getCrafterId() { return crafterId; }
    public void setCrafterId(Long crafterId) { this.crafterId = crafterId; }
    
    public LocalDateTime getFechaCreacion() { return fechaCreacion; }
    public void setFechaCreacion(LocalDateTime fechaCreacion) { this.fechaCreacion = fechaCreacion; }
    
    public Map<Long, Map<String, Integer>> getResultados() { return resultados; }
    public void setResultados(Map<Long, Map<String, Integer>> resultados) { this.resultados = resultados; }
}