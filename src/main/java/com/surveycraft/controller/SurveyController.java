package com.surveycraft.controller;


import com.surveycraft.dto.survey.CreateSurveyRequest;
import com.surveycraft.dto.survey.SubmitResponseRequest;
import com.surveycraft.dto.survey.SurveyResultsDTO;
import com.surveycraft.service.SurveyService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/api/surveys")
public class SurveyController {

    private final SurveyService surveyService;

    // Inyección por constructor
    public SurveyController(SurveyService surveyService) {
        this.surveyService = surveyService;
    }

    /**
     * 1) Crear encuesta (Crafter)
     * POST /api/surveys
     */
    @PostMapping
    public ResponseEntity<SurveyResultsDTO> createSurvey(
            @RequestBody CreateSurveyRequest request,
            Authentication authentication
    ) {
        Long crafterId = extractUserId(authentication); 
        SurveyResultsDTO created = surveyService.createSurvey(request, crafterId);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    /**
     * 2) Obtener encuesta (reutiliza DTO de resultados para mantener consistencia)
     * GET /api/surveys/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<SurveyResultsDTO> getSurveyById(@PathVariable Long id) {
        SurveyResultsDTO survey = surveyService.getSurveyById(id);
        return ResponseEntity.ok(survey);
    }

    /**
     * 3) Responder encuesta (Player)
     * POST /api/surveys/{id}/respond
     */
    @PostMapping("/{id}/respond")
    public ResponseEntity<Void> respondSurvey(
            @PathVariable Long id,
            @RequestBody SubmitResponseRequest request,
            Authentication authentication
    ) {
        Long playerId = extractUserId(authentication);
        surveyService.submitResponses(id, playerId, request);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    /**
     * 4) Ver resultados de una encuesta
     * GET /api/surveys/{id}/results
     */
    @GetMapping("/{id}/results")
    public ResponseEntity<SurveyResultsDTO> getSurveyResults(@PathVariable Long id) {
        SurveyResultsDTO results = surveyService.getSurveyResults(id);
        return ResponseEntity.ok(results);
    }

    /**
     * 5) Buscar encuestas por tipo
     * GET /api/surveys/tipo/{tipoEncuesta}
     */
    /**
     * 5) Buscar encuestas por tipo
     * GET /api/surveys/tipo/{tipoEncuesta}
     */
    @GetMapping("/tipo/{tipoEncuesta}")
    // CAMBIO: Usamos SurveyResultsDTO en lugar de SurveyDTO
    public ResponseEntity<List<SurveyResultsDTO>> buscarPorTipo(@PathVariable String tipoEncuesta) {
        
        // El servicio ya devuelve List<SurveyResultsDTO>, así que aquí lo recibimos igual
        List<SurveyResultsDTO> encuestas = surveyService.buscarEncuestasPorTipo(tipoEncuesta);
        
        return ResponseEntity.ok(encuestas);
    }

    /**
     * Helper para extraer el ID del usuario autenticado.
     */
    private Long extractUserId(Authentication authentication) {
        // TODO: Aquí debes integrar tu lógica real de JWT/SecurityContext
        // Por ahora retornamos un ID fijo o lanzamos error si es null
        return 1L; 
    }
}