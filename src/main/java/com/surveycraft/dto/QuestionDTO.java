package com.surveycraft.dto;


import javax.validation.constraints.NotBlank;
import java.util.List;

public class QuestionDTO {
    private Long questionId;
    
    @NotBlank(message = "El texto de la pregunta es obligatorio")
    private String preguntaTexto;
    
    private String tipoPregunta = "opcion_multiple"; // 'opcion_multiple', 'texto_libre', 'escala'
    private Integer orden = 1;
    private Boolean esObligatoria = true;
    private List<OptionDTO> opciones;
    
    // Getters y Setters
    public Long getQuestionId() { return questionId; }
    public void setQuestionId(Long questionId) { this.questionId = questionId; }
    
    public String getPreguntaTexto() { return preguntaTexto; }
    public void setPreguntaTexto(String preguntaTexto) { this.preguntaTexto = preguntaTexto; }
    
    public String getTipoPregunta() { return tipoPregunta; }
    public void setTipoPregunta(String tipoPregunta) { this.tipoPregunta = tipoPregunta; }
    
    public Integer getOrden() { return orden; }
    public void setOrden(Integer orden) { this.orden = orden; }
    
    public Boolean getEsObligatoria() { return esObligatoria; }
    public void setEsObligatoria(Boolean esObligatoria) { this.esObligatoria = esObligatoria; }
    
    public List<OptionDTO> getOpciones() { return opciones; }
    public void setOpciones(List<OptionDTO> opciones) { this.opciones = opciones; }
}
