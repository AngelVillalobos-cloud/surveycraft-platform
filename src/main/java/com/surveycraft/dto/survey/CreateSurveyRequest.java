package com.surveycraft.dto.survey;

import java.util.List;

public class CreateSurveyRequest {

    private String titulo;
    private String descripcion;
    private Long juegoId;             // ID del videojuego asociado
    private String tipoEncuesta;      // "compra", "analisis", "estado", "general"
    private boolean esEspecificaJuego;

    // Lista de preguntas que vienen en el JSON al crear la encuesta
    private List<CreateQuestionRequest> preguntas;

    // --- Getters y Setters ---

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public Long getJuegoId() {
        return juegoId;
    }

    public void setJuegoId(Long juegoId) {
        this.juegoId = juegoId;
    }

    public String getTipoEncuesta() {
        return tipoEncuesta;
    }

    public void setTipoEncuesta(String tipoEncuesta) {
        this.tipoEncuesta = tipoEncuesta;
    }

    public boolean isEsEspecificaJuego() {
        return esEspecificaJuego;
    }

    public void setEsEspecificaJuego(boolean esEspecificaJuego) {
        this.esEspecificaJuego = esEspecificaJuego;
    }

    public List<CreateQuestionRequest> getPreguntas() {
        return preguntas;
    }

    public void setPreguntas(List<CreateQuestionRequest> preguntas) {
        this.preguntas = preguntas;
    }

    // --- Clase interna estática para las preguntas ---
    public static class CreateQuestionRequest {
        private String texto;
        private String tipoPregunta;   // "single_choice", "multiple_choice"
        private List<String> opciones; // ["Sí", "No", "Tal vez"]

        public String getTexto() {
            return texto;
        }

        public void setTexto(String texto) {
            this.texto = texto;
        }

        public String getTipoPregunta() {
            return tipoPregunta;
        }

        public void setTipoPregunta(String tipoPregunta) {
            this.tipoPregunta = tipoPregunta;
        }

        public List<String> getOpciones() {
            return opciones;
        }

        public void setOpciones(List<String> opciones) {
            this.opciones = opciones;
        }
    }
}
