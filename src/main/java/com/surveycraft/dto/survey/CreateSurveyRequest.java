package com.surveycraft.dto.survey;

import java.util.List;

public class CreateSurveyRequest {
    private String titulo;
    private String descripcion;
    private Long juegoId;
    private String tipoEncuesta;
    private Boolean esEspecificaJuego;
    private List<PreguntaRequest> preguntas;
    
    // Clase interna para preguntas
    public static class PreguntaRequest {
        private String texto;
        private String tipoPregunta;
        private List<String> opciones;
        
        // Getters y Setters
        public String getTexto() { return texto; }
        public void setTexto(String texto) { this.texto = texto; }
        
        public String getTipoPregunta() { return tipoPregunta; }
        public void setTipoPregunta(String tipoPregunta) { this.tipoPregunta = tipoPregunta; }
        
        public List<String> getOpciones() { return opciones; }
        public void setOpciones(List<String> opciones) { this.opciones = opciones; }
    }
    
    // Getters y Setters
    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }
    
    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
    
    public Long getJuegoId() { return juegoId; }
    public void setJuegoId(Long juegoId) { this.juegoId = juegoId; }
    
    public String getTipoEncuesta() { return tipoEncuesta; }
    public void setTipoEncuesta(String tipoEncuesta) { this.tipoEncuesta = tipoEncuesta; }
    
    public Boolean getEsEspecificaJuego() { return esEspecificaJuego; }
    public void setEsEspecificaJuego(Boolean esEspecificaJuego) { this.esEspecificaJuego = esEspecificaJuego; }
    
    public List<PreguntaRequest> getPreguntas() { return preguntas; }
    public void setPreguntas(List<PreguntaRequest> preguntas) { this.preguntas = preguntas; }
}