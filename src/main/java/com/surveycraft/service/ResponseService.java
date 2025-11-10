package com.surveycraft.service;

import org.apache.catalina.connector.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.surveycraft.entity.Response;
import com.surveycraft.entity.Survey;
import com.surveycraft.repository.ResponseRepository;
import com.surveycraft.repository.SurveyRepository;
import java.util.List;

@Service
public class ResponseService {

    @Autowired
    private ResponseRepository responseRepository;
    
    @Autowired
    private SurveyRepository surveyRepository;
    
    // GUARDAR RESPUESTA
    public Response guardarRespuesta(Response response) {
        // Validaciones básicas
        if (response.getPlayerId() == null) {
            throw new IllegalArgumentException("Player ID es obligatorio");
        }
        if (response.getQuestionId() == null) {
            throw new IllegalArgumentException("Question ID es obligatorio");
        }
        
        // Asignar puntos por defecto si no vienen
        if (response.getPlayerPointsEarned() == null) {
            response.setPlayerPointsEarned(10); // Puntos base por responder
        }
        
        return responseRepository.save(response);
    }
    
    // OBTENER RESPUESTAS POR ENCUESTA
    public List<Response> obtenerRespuestasPorSurvey(Long surveyId) {
        return responseRepository.findBySurveyId(surveyId);
    }
    
    // OBTENER RESPUESTAS POR JUGADOR
    public List<Response> obtenerRespuestasPorPlayer(Long playerId) {
        return responseRepository.findByPlayerId(playerId);
    }
    
    // OBTENER RESPUESTAS POR PREGUNTA
    public List<Response> obtenerRespuestasPorQuestion(Long questionId) {
        return responseRepository.findByQuestionId(questionId);
    }
    
    // VERIFICAR SI JUGADOR YA RESPONDIÓ ENCUESTA
    public boolean yaRespondioEncuesta(Long playerId, Long surveyId) {
        // Esto necesita una consulta personalizada
        List<Response> respuestas = responseRepository.findByPlayerIdAndSurveyId(playerId, surveyId);
        return !respuestas.isEmpty();
    }
    
    // CONTAR RESPUESTAS POR ENCUESTA
    public Long contarRespuestasPorSurvey(Long surveyId) {
        return responseRepository.countBySurveyId(surveyId);
    }
    
    // OBTENER ESTADÍSTICAS BÁSICAS
    public Object obtenerEstadisticasSurvey(Long surveyId) {
        Long totalRespuestas = contarRespuestasPorSurvey(surveyId);
        // Aquí puedes agregar más estadísticas
        
        return new Object() {
            public Long totalRespuestas = totalRespuestas;
            // Agregar más métricas según necesites
        };
    }
}
