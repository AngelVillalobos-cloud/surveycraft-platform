package com.surveycraft.service;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.surveycraft.entity.Survey;
import com.surveycraft.entity.Question;
import com.surveycraft.entity.Option;
import java.util.stream.Collectors;
import com.surveycraft.dto.survey.CreateSurveyRequest;
import com.surveycraft.dto.survey.SubmitResponseRequest;
import com.surveycraft.dto.survey.SurveyResultsDTO;
import com.surveycraft.entity.*;
import com.surveycraft.repository.*;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;


@Service
public class SurveyService {

    @Autowired
    private SurveyRepository surveyRepository;
    @Autowired
    private QuestionRepository questionRepository;
    @Autowired
    private OptionRepository optionRepository;
    @Autowired
    private ResponseRepository responseRepository; 
    @Autowired
    private UserRepository userRepository; 

    // 1. CREAR ENCUESTA
    @Transactional
    public SurveyResultsDTO createSurvey(CreateSurveyRequest request, Long crafterId) {
        User crafter = userRepository.findById(crafterId)
                .orElseThrow(() -> new RuntimeException("Crafter no encontrado"));

        Survey survey = new Survey();
        survey.setTitulo(request.getTitulo());
        survey.setDescripcion(request.getDescripcion());
        survey.setJuegoId(request.getJuegoId());
        survey.setTipoEncuesta(request.getTipoEncuesta());
        survey.setEstado("activa"); 
        survey.setCrafter(crafter);

        Survey savedSurvey = surveyRepository.save(survey);

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
        return mapToResultsDTO(savedSurvey);
    }

    // 2. OBTENER POR ID
    public SurveyResultsDTO getSurveyById(Long id) {
        Survey survey = surveyRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Encuesta no encontrada"));
        return mapToResultsDTO(survey);
    }

    // 3. OBTENER RESULTADOS
    public SurveyResultsDTO getSurveyResults(Long surveyId) {
        Survey survey = surveyRepository.findById(surveyId)
                .orElseThrow(() -> new RuntimeException("Encuesta no encontrada"));
        return mapToResultsDTO(survey);
    }

    // 4. RESPONDER ENCUESTA
    @Transactional
    public void submitResponses(Long surveyId, Long playerId, SubmitResponseRequest request) {
        // Validamos que existan
        if (!surveyRepository.existsById(surveyId)) {
            throw new RuntimeException("Encuesta no encontrada");
        }
        
        User player = userRepository.findById(playerId)
                .orElseThrow(() -> new RuntimeException("Player no encontrado"));

        for (SubmitResponseRequest.AnswerRequest answer : request.getRespuestas()) {
            Response response = new Response();
            response.setPlayer(player);
            response.setQuestion(questionRepository.getReferenceById(answer.getQuestionId()));
            response.setSelectedOption(optionRepository.getReferenceById(answer.getOptionId()));
            
            response.setTiempoJugadoJuego(request.getTiempoJugadoJuego()); // <--- Coincide con tu DTO
            response.setPlataformaUsada(request.getPlataformaUsada());
            
            responseRepository.save(response);
        }
    }

    // 5. BUSCAR POR TIPO
    public List<SurveyResultsDTO> buscarEncuestasPorTipo(String tipoEncuesta) {
        List<Survey> surveys = surveyRepository.findByTipoEncuesta(tipoEncuesta);
        
        return surveys.stream().map(s -> {
            SurveyResultsDTO dto = new SurveyResultsDTO();
            dto.setSurveyId(s.getSurveyId()); 
            dto.setTitulo(s.getTitulo());
            dto.setDescripcion(s.getDescripcion());
            dto.setJuegoId(s.getJuegoId());
            dto.setEstado(s.getEstado());
            return dto;
        }).collect(Collectors.toList());
    }

    // === MAPPER PRIVADO ===
    private SurveyResultsDTO mapToResultsDTO(Survey survey) {
        SurveyResultsDTO dto = new SurveyResultsDTO();
        dto.setSurveyId(survey.getSurveyId());
        dto.setTitulo(survey.getTitulo());
        dto.setDescripcion(survey.getDescripcion());
        dto.setJuegoId(survey.getJuegoId());
        dto.setEstado(survey.getEstado()); // Aseguramos mapear el estado también

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

                        // NECESITAS AGREGAR ESTE MÉTODO EN TU REPOSITORIO (VER ABAJO)
                        long votos = responseRepository.countBySelectedOption(o); 
                        oDto.setVotos(votos);
                        votosPregunta += votos;
                        oDtos.add(oDto);
                    }
                }

                for (SurveyResultsDTO.OptionResultDTO oDto : oDtos) {
                    if (votosPregunta > 0) {
                        double pct = (double) oDto.getVotos() / votosPregunta * 100;
                        oDto.setPorcentaje(Math.round(pct * 10.0) / 10.0);
                    } else {
                        oDto.setPorcentaje(0);
                    }
                }
                qDto.setOpciones(oDtos);
                qDtos.add(qDto);
                if (totalEncuesta == 0) totalEncuesta = votosPregunta;
            }
        }
        dto.setTotalRespuestas(totalEncuesta);
        dto.setPreguntas(qDtos);
        return dto;
    }
}