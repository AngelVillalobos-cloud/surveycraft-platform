package com.surveycraft.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "surveys")
public class Survey {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String titulo;
    
    private String descripcion;
    
    @Column(nullable = false)
    private String estado; // "borrador", "activa", "pausada"
    
    private Boolean esEspecificaJuego;
    private Long juegoId;
    private String tipoEncuesta;
    
    @Column(nullable = false)
    private Long crafterId;
    
    @OneToMany(mappedBy = "survey", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Question> preguntas;
    
    private LocalDateTime fechaCreacion;
    
    // Constructores
    public Survey() {
        this.fechaCreacion = LocalDateTime.now();
        this.estado = "borrador";
    }
    
    public Survey(String titulo, String descripcion, Long crafterId) {
        this();
        this.titulo = titulo;
        this.descripcion = descripcion;
        this.crafterId = crafterId;
    }
    
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
    
    public List<Question> getPreguntas() { return preguntas; }
    public void setPreguntas(List<Question> preguntas) { this.preguntas = preguntas; }
    
    public LocalDateTime getFechaCreacion() { return fechaCreacion; }
    public void setFechaCreacion(LocalDateTime fechaCreacion) { this.fechaCreacion = fechaCreacion; }
}