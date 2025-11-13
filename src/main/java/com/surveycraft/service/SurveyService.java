package com.surveycraft.service;

package com.surveycraft.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.surveycraft.entity.Survey;
import com.surveycraft.exception.SurveyNotFoundException;
import com.surveycraft.entity.Question;
import com.surveycraft.entity.Option;
import com.surveycraft.repository.SurveyRepository;
import com.surveycraft.repository.QuestionRepository;
import com.surveycraft.repository.OptionRepository;
// AGREGAR ESTAS IMPORTACIONES
import com.surveycraft.dto.SurveyDTO;
import com.surveycraft.service.mapper.SurveyMapper;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class SurveyService {

    @Autowired
    private SurveyRepository surveyRepository;

    @Autowired
    private QuestionRepository questionRepository;

    @Autowired
    private OptionRepository optionRepository;

    // AGREGAR EL MAPPER
    @Autowired
    private SurveyMapper surveyMapper;

    // === MÉTODOS ACTUALIZADOS PARA USAR DTOs ===

    // CREAR ENCUESTA - ACTUALIZADO
    public SurveyDTO crearEncuesta(SurveyDTO surveyDTO) {
        // Validar que el título no esté vacío
        if (surveyDTO.getTitulo() == null || surveyDTO.getTitulo().trim().isEmpty()) {
            throw new IllegalArgumentException("El título de la encuesta es obligatorio");
        }

        // Convertir DTO a Entity
        Survey survey = surveyMapper.toEntity(surveyDTO);
        
        // Establecer estado por defecto si no viene en el DTO
        if (survey.getEstado() == null) {
            survey.setEstado("borrador");
        }

        Survey savedSurvey = surveyRepository.save(survey);
        return surveyMapper.toDTO(savedSurvey);
    }

    // EDITAR ENCUESTA - ACTUALIZADO
    public SurveyDTO editarEncuesta(Long id, SurveyDTO surveyDTO) {
        Optional<Survey> encuestaExistente = surveyRepository.findById(id);

        if (encuestaExistente.isPresent()) {
            Survey encuesta = encuestaExistente.get();

            // Actualizar campos desde el DTO
            encuesta.setTitulo(surveyDTO.getTitulo());
            encuesta.setDescripcion(surveyDTO.getDescripcion());
            encuesta.setEstado(surveyDTO.getEstado());
            encuesta.setEsEspecificaJuego(surveyDTO.getEsEspecificaJuego());
            encuesta.setJuegoId(surveyDTO.getJuegoId());
            encuesta.setTipoEncuesta(surveyDTO.getTipoEncuesta());

            Survey updatedSurvey = surveyRepository.save(encuesta);
            return surveyMapper.toDTO(updatedSurvey);
        } else {
            throw new RuntimeException("Encuesta no encontrada con ID: " + id);
        }
    }

    // OBTENER ENCUESTA POR ID - ACTUALIZADO
    public SurveyDTO obtenerEncuestaPorId(Long id) {
        Survey survey = surveyRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Encuesta no encontrada con ID: " + id));
        return surveyMapper.toDTO(survey);
    }

    // OBTENER ENCUESTAS ACTIVAS - ACTUALIZADO
    public List<SurveyDTO> obtenerEncuestasActivas() {
        List<Survey> encuestas = surveyRepository.findByEstado("activa");
        return encuestas.stream()
            .map(surveyMapper::toDTO)
            .collect(Collectors.toList());
    }

    // OBTENER ENCUESTAS POR CRAFTER - ACTUALIZADO
    public List<SurveyDTO> obtenerEncuestasPorCrafter(Long crafterId) {
        List<Survey> encuestas = surveyRepository.findByCrafterId(crafterId);
        return encuestas.stream()
            .map(surveyMapper::toDTO)
            .collect(Collectors.toList());
    }

    // ACTIVAR ENCUESTA - ACTUALIZADO
    public SurveyDTO activarEncuesta(Long id) {
        Survey survey = surveyRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Encuesta no encontrada"));
        
        survey.setEstado("activa");
        Survey updatedSurvey = surveyRepository.save(survey);
        return surveyMapper.toDTO(updatedSurvey);
    }

    // PAUSAR ENCUESTA - ACTUALIZADO
    public SurveyDTO pausarEncuesta(Long id) {
        Survey survey = surveyRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Encuesta no encontrada"));
        
        survey.setEstado("pausada");
        Survey updatedSurvey = surveyRepository.save(survey);
        return surveyMapper.toDTO(updatedSurvey);
    }

    // BUSCAR ENCUESTAS POR TIPO - ACTUALIZADO
    public List<SurveyDTO> buscarEncuestasPorTipo(String tipoEncuesta) {
        List<Survey> encuestas = surveyRepository.findByTipoEncuesta(tipoEncuesta);
        return encuestas.stream()
            .map(surveyMapper::toDTO)
            .collect(Collectors.toList());
    }

    // === MÉTODOS QUE SE QUEDAN EXACTAMENTE IGUAL ===

    // ELIMINAR ENCUESTA - NO CAMBIA
    public void eliminarEncuesta(Long id) {
        if (surveyRepository.existsById(id)) {
            surveyRepository.deleteById(id);
        } else {
            throw new RuntimeException("Encuesta no encontrada con ID: " + id);
        }
    }

    // VALIDAR ENCUESTA COMPLETA - NO CAMBIA
    public void validarSurveyCompleta(Survey survey) {
        if (survey.getPreguntas() == null || survey.getPreguntas().isEmpty()) {
            throw new IllegalArgumentException("La encuesta debe tener al menos una pregunta");
        }

        for (Question pregunta : survey.getPreguntas()) {
            if (pregunta.getOpciones() == null || pregunta.getOpciones().isEmpty()) {
                throw new IllegalArgumentException("Cada pregunta debe tener opciones de respuesta");
            }
        }
    }

    // OBTENER ESTADÍSTICAS DE ENCUESTA - NO CAMBIA
    public Map<String, Object> obtenerEstadisticasSurvey(Long surveyId) {
        Optional<Survey> encuesta = surveyRepository.findById(surveyId);
        if (!encuesta.isPresent()) {
            throw new SurveyNotFoundException(surveyId);
        }

        // NOTA: Necesitarías tener responseRepository inyectado
        // Long totalRespuestas = responseRepository.countBySurveyId(surveyId);
        // Long jugadoresUnicos = responseRepository.countUniquePlayersBySurveyId(surveyId);

        Map<String, Object> estadisticas = new HashMap<>();
        // estadisticas.put("totalRespuestas", totalRespuestas);
        // estadisticas.put("jugadoresUnicos", jugadoresUnicos);
        estadisticas.put("encuesta", encuesta.get());

        return estadisticas;
    }
}