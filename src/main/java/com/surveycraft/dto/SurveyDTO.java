package com.surveycraft.dto;

import java.time.LocalDateTime;

public class SurveyDTO {
    private Long id;
    private String titulo;
    private String descripcion;
    private String estado;
    private Boolean esEspecificaJuego;
    private Long juegoId;
    private String tipoEncuesta;
    private Long crafterId;
    private LocalDateTime fechaCreacion;
    
    // Constructores
    public SurveyDTO() {}
    
    // Getters y Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }
    
    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
    
    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
    
    public Boolean getEsEspecificaJuego() { return esEspecificaJuego; }
    public void setEsEspecificaJuego(Boolean esEspecificaJuego) { this.esEspecificaJuego = esEspecificaJuego; }
    
    public Long getJuegoId() { return juegoId; }
    public void setJuegoId(Long juegoId) { this.juegoId = juegoId; }
    
    public String getTipoEncuesta() { return tipoEncuesta; }
    public void setTipoEncuesta(String tipoEncuesta) { this.tipoEncuesta = tipoEncuesta; }
    
    public Long getCrafterId() { return crafterId; }
    public void setCrafterId(Long crafterId) { this.crafterId = crafterId; }
    
    public LocalDateTime getFechaCreacion() { return fechaCreacion; }
    public void setFechaCreacion(LocalDateTime fechaCreacion) { this.fechaCreacion = fechaCreacion; }
}