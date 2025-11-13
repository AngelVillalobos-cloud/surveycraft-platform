package com.surveycraft.controller;

import com.surveycraft.dto.SurveyDTO;
import com.surveycraft.dto.survey.CreateSurveyRequest;
import com.surveycraft.dto.survey.SubmitResponseRequest;
import com.surveycraft.dto.survey.SurveyResultsDTO;
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
     *
     * El cuerpo debe ser un JSON que mapee a CreateSurveyRequest.
     * Ejemplo (simplificado):
     * {
     *   "titulo": "¿Vale la pena el DLC?",
     *   "descripcion": "Encuesta sobre el DLC de Elden Ring",
     *   "juegoId": 123,
     *   "tipoEncuesta": "compra",
     *   "esEspecificaJuego": true,
     *   "preguntas": [
     *     {
     *       "texto": "¿Lo recomiendas?",
     *       "tipoPregunta": "single_choice",
     *       "opciones": ["Sí", "No", "Ver resultados"]
     *     }
     *   ]
     * }
     *
     * Devuelve un SurveyResultsDTO con los votos en 0,
     * listo para ser utilizado en gráficos cuando empiecen a llegar respuestas.
     */
    @PostMapping
    public ResponseEntity<SurveyResultsDTO> createSurvey(
            @RequestBody CreateSurveyRequest request,
            Authentication authentication
    ) {
        Long crafterId = extractUserId(authentication); // TODO: sacar de tu UserDetails/JWT
        SurveyResultsDTO created = surveyService.createSurvey(request, crafterId);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    /**
     * 2) Obtener encuesta + resultados actuales
     * GET /api/surveys/{id}
     *
     * Devuelve SurveyResultsDTO: estructura de la encuesta +
     * conteos actuales (pueden ser 0 si nadie ha respondido).
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
        Long playerId = extractUserId(authentication); // TODO: sacar de tu UserDetails/JWT
        surveyService.submitResponses(id, playerId, request);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    /**
     * 4) Ver resultados de una encuesta (solo resultados,
     * pero reutilizamos el mismo DTO por consistencia)
     * GET /api/surveys/{id}/results
     */
    @GetMapping("/{id}/results")
    public ResponseEntity<SurveyResultsDTO> getSurveyResults(@PathVariable Long id) {
        SurveyResultsDTO results = surveyService.getSurveyResults(id);
        return ResponseEntity.ok(results);
    }

    /**
     * 5) Buscar encuestas por tipo (ya tienes el método en SurveyService)
     * GET /api/surveys/tipo/{tipoEncuesta}
     *
     * Ejemplo: /api/surveys/tipo/compra
     */
    @GetMapping("/tipo/{tipoEncuesta}")
    public ResponseEntity<List<SurveyDTO>> buscarPorTipo(@PathVariable String tipoEncuesta) {
        List<SurveyDTO> encuestas = surveyService.buscarEncuestasPorTipo(tipoEncuesta);
        return ResponseEntity.ok(encuestas);
    }

    /**
     * Helper para extraer el ID del usuario autenticado.
     * Adáptalo a tu implementación real de seguridad.
     */
    private Long extractUserId(Authentication authentication) {
        // TODO: implementar con tu UserDetails o JWT
        return 1L; // placeholder
    }
}
