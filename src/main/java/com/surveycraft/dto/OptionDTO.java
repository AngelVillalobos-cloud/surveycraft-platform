package com.surveycraft.dto;

o;

import javax.validation.constraints.NotBlank;

public class OptionDTO {
    private Long optionId;
    
    @NotBlank(message = "El texto de la opción es obligatorio")
    private String opcionTexto;
    
    private Integer orden = 0;
    private String imagenUrl;
    
    // Getters y Setters
    public Long getOptionId() { return optionId; }
    public void setOptionId(Long optionId) { this.optionId = optionId; }
    
    public String getOpcionTexto() { return opcionTexto; }
    public void setOpcionTexto(String opcionTexto) { this.opcionTexto = opcionTexto; }
    
    public Integer getOrden() { return orden; }
    public void setOrden(Integer orden) { this.orden = orden; }
    
    public String getImagenUrl() { return imagenUrl; }
    public void setImagenUrl(String imagenUrl) { this.imagenUrl = imagenUrl; }
}