package com.surveycraft.service.mapper;

//src/main/java/com/surveycraft/service/mapper/SurveyMapper.java

import com.surveycraft.dto.SurveyDTO;
import com.surveycraft.dto.QuestionDTO;
import com.surveycraft.dto.OptionDTO;
import com.surveycraft.model.Survey;
import com.surveycraft.model.Question;
import com.surveycraft.model.Option;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
public class SurveyMapper {

 public Survey toEntity(SurveyDTO dto) {
     if (dto == null) {
         return null;
     }
     Survey survey = new Survey();
     survey.setSurveyId(dto.getSurveyId());
     survey.setTitulo(dto.getTitulo());
     survey.setDescripcion(dto.getDescripcion());
     survey.setFechaCreacion(dto.getFechaCreacion());
     survey.setFechaExpiracion(dto.getFechaExpiracion());
     survey.setActivo(dto.isActivo());

     // Mapear preguntas si existen
     if (dto.getPreguntas() != null) {
         survey.setPreguntas(
             dto.getPreguntas().stream()
                 .map(this::toQuestionEntity)
                 .collect(Collectors.toList())
         );
     }

     return survey;
 }

 public SurveyDTO toDTO(Survey entity) {
     if (entity == null) {
         return null;
     }

     SurveyDTO dto = new SurveyDTO();
     dto.setSurveyId(entity.getSurveyId());
     dto.setTitulo(entity.getTitulo());
     dto.setDescripcion(entity.getDescripcion());
     dto.setFechaCreacion(entity.getFechaCreacion());
     dto.setFechaExpiracion(entity.getFechaExpiracion());
     dto.setActivo(entity.isActivo());

     // Mapear preguntas si existen
     if (entity.getPreguntas() != null) {
         dto.setPreguntas(
             entity.getPreguntas().stream()
                 .map(this::toQuestionDTO)
                 .collect(Collectors.toList())
         );
     }

     return dto;
 }

 private Question toQuestionEntity(QuestionDTO dto) {
     Question question = new Question();
     question.setQuestionId(dto.getQuestionId());
     question.setTexto(dto.getTexto());
     question.setTipo(dto.getTipo());
     question.setOrden(dto.getOrden());

     // Mapear opciones si existen
     if (dto.getOpciones() != null) {
         question.setOpciones(
             dto.getOpciones().stream()
                 .map(this::toOptionEntity)
                 .collect(Collectors.toList())
         );
     }

     return question;
 }

 private QuestionDTO toQuestionDTO(Question entity) {
     QuestionDTO dto = new QuestionDTO();
     dto.setQuestionId(entity.getQuestionId());
     dto.setTexto(entity.getTexto());
     dto.setTipo(entity.getTipo());
     dto.setOrden(entity.getOrden());

     // Mapear opciones si existen
     if (entity.getOpciones() != null) {
         dto.setOpciones(
             entity.getOpciones().stream()
                 .map(this::toOptionDTO)
                 .collect(Collectors.toList())
         );
     }

     return dto;
 }

 private Option toOptionEntity(OptionDTO dto) {
     Option option = new Option();
     option.setOptionId(dto.getOptionId());
     option.setTexto(dto.getTexto());
     option.setOrden(dto.getOrden());
     return option;
 }

 private OptionDTO toOptionDTO(Option entity) {
     OptionDTO dto = new OptionDTO();
     dto.setOptionId(entity.getOptionId());
     dto.setTexto(entity.getTexto());
     dto.setOrden(entity.getOrden());
     return dto;
 }
}