package com.surveycraft.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.surveycraft.entity.Survey;

import com.surveycraft.entity.Question;
import com.surveycraft.entity.Option;


import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;


import com.surveycraft.dto.survey.CreateSurveyRequest;
import com.surveycraft.dto.survey.SubmitResponseRequest;
import com.surveycraft.dto.survey.SurveyResultsDTO;
import com.surveycraft.entity.*;
import com.surveycraft.repository.*;

import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class SurveyService {

    @Autowired
    private SurveyRepository surveyRepository;
    @Autowired
    private QuestionRepository questionRepository;
    @Autowired
    private OptionRepository optionRepository;
    @Autowired
    private ResponseRepository responseRepository; // Necesario para contar votos
    @Autowired
    private UserRepository userRepository; // Necesario para vincular al Crafter/Player

    // 1. CREAR ENCUESTA (Lógica manual sin Mapper)
    @Transactional
    public SurveyResultsDTO createSurvey(CreateSurveyRequest request, Long crafterId) {
        // 1. Buscar al Crafter
        User crafter = userRepository.findById(crafterId)
                .orElseThrow(() -> new RuntimeException("Crafter no encontrado"));

        // 2. Crear entidad Survey
        Survey survey = new Survey();
        survey.setTitulo(request.getTitulo());
        survey.setDescripcion(request.getDescripcion());
        survey.setJuegoId(request.getJuegoId());
        survey.setTipoEncuesta(request.getTipoEncuesta());
        survey.setEstado("activa"); // Por defecto activa para el MVP
        survey.setCrafter(crafter);

        // Guardar Survey primero para tener ID
        Survey savedSurvey = surveyRepository.save(survey);

        // 3. Procesar Preguntas y Opciones
        if (request.getPreguntas() != null) {
            for (CreateSurveyRequest.QuestionRequest qReq : request.getPreguntas()) {
                Question question = new Question();
                question.setPreguntaTexto(qReq.getTexto());
                question.setTipoPregunta(qReq.getTipoPregunta());
                question.setSurvey(savedSurvey);
                
                Question savedQuestion = questionRepository.save(question);

                if (qReq.getOpciones() != null) {
                    for (String optText : qReq.getOpciones()) {
                        Option option = new Option();
                        option.setOpcionTexto(optText);
                        option.setQuestion(savedQuestion);
                        optionRepository.save(option);
                    }
                }
            }
        }

        // 4. Devolver el DTO de resultados (vacío, porque es nueva)
        return mapToResultsDTO(savedSurvey);
    }

    // 2. OBTENER POR ID (Sin resultados calculados complejos, solo estructura)
    public SurveyResultsDTO getSurveyById(Long id) {
        Survey survey = surveyRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Encuesta no encontrada"));
        return mapToResultsDTO(survey);
    }

    // 3. OBTENER RESULTADOS (Con conteo de votos)
    public SurveyResultsDTO getSurveyResults(Long surveyId) {
        Survey survey = surveyRepository.findById(surveyId)
                .orElseThrow(() -> new RuntimeException("Encuesta no encontrada"));
        
        // Reutilizamos la lógica de mapeo que ya calcula votos
        return mapToResultsDTO(survey);
    }

    // 4. RESPONDER ENCUESTA
    @Transactional
    public void submitResponses(Long surveyId, Long playerId, SubmitResponseRequest request) {
        Survey survey = surveyRepository.findById(surveyId)
                .orElseThrow(() -> new RuntimeException("Encuesta no encontrada"));
        
        User player = userRepository.findById(playerId)
                .orElseThrow(() -> new RuntimeException("Player no encontrado"));

        for (SubmitResponseRequest.AnswerRequest answer : request.getRespuestas()) {
            Response response = new Response();
            response.setPlayer(player);
            // Asignar relaciones usando solo IDs (getReferenceById es más eficiente que findById si solo necesitas linkear)
            response.setQuestion(questionRepository.getReferenceById(answer.getQuestionId()));
            response.setSelectedOption(optionRepository.getReferenceById(answer.getOptionId()));
            
            // Campos extra
            response.setTiempoJugadoJuego(request.getTiempoJugado());
            response.setPlataformaUsada(request.getPlataforma());
            
            responseRepository.save(response);
        }
    }

    // 5. BUSCAR POR TIPO (Devuelve lista simple SurveyDTO)
 // 5. BUSCAR POR TIPO - CORREGIDO
    // Cambiamos List<SurveyDTO> por List<SurveyResultsDTO>
    public List<SurveyResultsDTO> buscarEncuestasPorTipo(String tipoEncuesta) {
        List<Survey> surveys = surveyRepository.findByTipoEncuesta(tipoEncuesta);
        
        // Mapeo manual a SurveyResultsDTO
        return surveys.stream().map(s -> {
            SurveyResultsDTO dto = new SurveyResultsDTO();
            // Usamos los setters que YA tienes en SurveyResultsDTO
            dto.setSurveyId(s.getSurveyId()); 
            dto.setTitulo(s.getTitulo());
            dto.setDescripcion(s.getDescripcion());
            dto.setJuegoId(s.getJuegoId());
            dto.setEstado(s.getEstado()); // <--- Usamos el campo nuevo
            
            // NO seteamos ni preguntas ni totalRespuestas, se irán como null/0
            // lo cual está perfecto para una lista resumen.
            
            return dto;
        }).collect(Collectors.toList());
    }

    // === MÉTODO PRIVADO: CONVERTIR ENTIDAD A RESULTSDTO (SIN MAPPER) ===
    private SurveyResultsDTO mapToResultsDTO(Survey survey) {
        SurveyResultsDTO dto = new SurveyResultsDTO();
        dto.setSurveyId(survey.getSurveyId());
        dto.setTitulo(survey.getTitulo());
        dto.setDescripcion(survey.getDescripcion());
        dto.setJuegoId(survey.getJuegoId());

        // Calcular total de respuestas de la encuesta (basado en la primera pregunta, aprox)
        // Nota: Para MVP simple, asumimos que contar respuestas de la primera pregunta da el total aprox.
        long totalEncuesta = 0;

        List<SurveyResultsDTO.QuestionResultDTO> qDtos = new ArrayList<>();
        
        if (survey.getQuestions() != null) {
            for (Question q : survey.getQuestions()) {
                SurveyResultsDTO.QuestionResultDTO qDto = new SurveyResultsDTO.QuestionResultDTO();
                qDto.setQuestionId(q.getQuestionId());
                qDto.setTextoPregunta(q.getPreguntaTexto());
                qDto.setTipoPregunta(q.getTipoPregunta());

                List<SurveyResultsDTO.OptionResultDTO> oDtos = new ArrayList<>();
                long votosPregunta = 0;

                if (q.getOptions() != null) {
                    for (Option o : q.getOptions()) {
                        SurveyResultsDTO.OptionResultDTO oDto = new SurveyResultsDTO.OptionResultDTO();
                        oDto.setOptionId(o.getOptionId());
                        oDto.setTextoOpcion(o.getOpcionTexto());

                        // AQUÍ OCURRE LA MAGIA DEL CONTEO
                        // Usamos el responseRepository para contar cuántas veces se eligió esta opción
                        long votos = responseRepository.countBySelectedOption(o); 
                        oDto.setVotos(votos);
                        votosPregunta += votos;
                        
                        oDtos.add(oDto);
                    }
                }

                // Calcular porcentajes
                for (SurveyResultsDTO.OptionResultDTO oDto : oDtos) {
                    if (votosPregunta > 0) {
                        double pct = (double) oDto.getVotos() / votosPregunta * 100;
                        oDto.setPorcentaje(Math.round(pct * 10.0) / 10.0); // Redondear 1 decimal
                    } else {
                        oDto.setPorcentaje(0);
                    }
                }
                
                qDto.setOpciones(oDtos);
                qDtos.add(qDto);
                
                // Usamos los votos de la primera pregunta como referencia del total de la encuesta
                if (totalEncuesta == 0) totalEncuesta = votosPregunta;
            }
        }
        
        dto.setTotalRespuestas(totalEncuesta);
        dto.setPreguntas(qDtos);
        return dto;
    }
}