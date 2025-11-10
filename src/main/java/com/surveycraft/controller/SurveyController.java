package com.surveycraft.controller;



import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import com.surveycraft.entity.Survey;
import com.surveycraft.entity.Response;
import com.surveycraft.dto.SurveyDTO;
import com.surveycraft.service.SurveyService;
import com.surveycraft.service.ResponseService;
import com.surveycraft.service.mapper.SurveyMapper;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/surveys")
public class SurveyController {

    @Autowired
    private SurveyService surveyService;
    
    @Autowired
    private ResponseService responseService;

    @Autowired
    private SurveyMapper surveyMapper;
    
    // LISTAR ENCUESTAS ACTIVAS (PARA PLAYERS)
    @GetMapping
    public String listarEncuestas(Model model) {
        List<SurveyDTO> encuestasActivas = surveyService.obtenerEncuestasActivas();
        model.addAttribute("encuestas", encuestasActivas);
        return "player/dashboard";
    }
    
    // MOSTRAR FORMULARIO CREAR ENCUESTA
    @GetMapping("/create")
    public String mostrarFormularioCrear(Model model) {
        model.addAttribute("survey", new SurveyDTO());
        return "crafter/create-survey";
    }
    
    // CREAR NUEVA ENCUESTA - ACTUALIZADO
    @PostMapping("/create")
    public String crearEncuesta(@ModelAttribute SurveyDTO surveyDTO) {
        try {
            surveyService.crearEncuesta(surveyDTO);
            return "redirect:/crafter/dashboard?success=Encuesta creada";
        } catch (Exception e) {
            return "redirect:/surveys/create?error=" + e.getMessage();
        }
    }
    
    // MOSTRAR ENCUESTA PARA RESPONDER - ACTUALIZADO
    @GetMapping("/{id}/take")
    public String tomarEncuesta(@PathVariable Long id, Model model) {
        try {
            SurveyDTO encuesta = surveyService.obtenerEncuestaPorId(id);
            model.addAttribute("survey", encuesta);
            return "player/take-survey";
        } catch (Exception e) {
            return "redirect:/surveys?error=Encuesta no encontrada";
        }
    }
    
    // GUARDAR RESPUESTA (ESTE MÉTODO NO CAMBIA - ResponseService maneja sus propios DTOs)
    @PostMapping("/{surveyId}/respond")
    public String responderEncuesta(@PathVariable Long surveyId, 
                                   @ModelAttribute Response response) {
        try {
            responseService.guardarRespuesta(response);
            return "redirect:/surveys/" + surveyId + "/results?success=Respuesta guardada";
        } catch (Exception e) {
            return "redirect:/surveys/" + surveyId + "/take?error=" + e.getMessage();
        }
    }
    
    // VER RESULTADOS DE ENCUESTA - ACTUALIZADO
    @GetMapping("/{id}/results")
    public String verResultados(@PathVariable Long id, Model model) {
        try {
            SurveyDTO encuesta = surveyService.obtenerEncuestaPorId(id);
            List<Response> respuestas = responseService.obtenerRespuestasPorSurvey(id);
            Object estadisticas = responseService.obtenerEstadisticasSurvey(id);
            
            model.addAttribute("survey", encuesta);
            model.addAttribute("responses", respuestas);
            model.addAttribute("stats", estadisticas);
            
            return "crafter/survey-results";
        } catch (Exception e) {
            return "redirect:/crafter/dashboard?error=Encuesta no encontrada";
        }
    }
    
    // EDITAR ENCUESTA (FORMULARIO) - ACTUALIZADO
    @GetMapping("/{id}/edit")
    public String mostrarFormularioEditar(@PathVariable Long id, Model model) {
        try {
            SurveyDTO encuesta = surveyService.obtenerEncuestaPorId(id);
            model.addAttribute("survey", encuesta);
            return "crafter/edit-survey";
        } catch (Exception e) {
            return "redirect:/crafter/dashboard?error=Encuesta no encontrada";
        }
    }
    
    // ACTUALIZAR ENCUESTA - ACTUALIZADO
    @PostMapping("/{id}/edit")
    public String actualizarEncuesta(@PathVariable Long id, 
                                    @ModelAttribute SurveyDTO surveyDTO) {
        try {
            surveyService.editarEncuesta(id, surveyDTO);
            return "redirect:/crafter/dashboard?success=Encuesta actualizada";
        } catch (Exception e) {
            return "redirect:/surveys/" + id + "/edit?error=" + e.getMessage();
        }
    }
    
    // ELIMINAR ENCUESTA - NO CAMBIA
    @PostMapping("/{id}/delete")
    public String eliminarEncuesta(@PathVariable Long id) {
        try {
            surveyService.eliminarEncuesta(id);
            return "redirect:/crafter/dashboard?success=Encuesta eliminada";
        } catch (Exception e) {
            return "redirect:/crafter/dashboard?error=" + e.getMessage();
        }
    }
    
    // ACTIVAR ENCUESTA - ACTUALIZADO
    @PostMapping("/{id}/activate")
    public String activarEncuesta(@PathVariable Long id) {
        try {
            surveyService.activarEncuesta(id);
            return "redirect:/crafter/dashboard?success=Encuesta activada";
        } catch (Exception e) {
            return "redirect:/crafter/dashboard?error=" + e.getMessage();
        }
    }
    
    // PAUSAR ENCUESTA - ACTUALIZADO
    @PostMapping("/{id}/pause")
    public String pausarEncuesta(@PathVariable Long id) {
        try {
            surveyService.pausarEncuesta(id);
            return "redirect:/crafter/dashboard?success=Encuesta pausada";
        } catch (Exception e) {
            return "redirect:/crafter/dashboard?error=" + e.getMessage();
        }
    }

    // LISTAR ENCUESTAS POR CRAFTER - NUEVO MÉTODO (si lo necesitas)
    @GetMapping("/crafter/{crafterId}")
    public String listarEncuestasPorCrafter(@PathVariable Long crafterId, Model model) {
        List<SurveyDTO> encuestas = surveyService.obtenerEncuestasPorCrafter(crafterId);
        model.addAttribute("encuestas", encuestas);
        return "crafter/dashboard";
    }

    // BUSCAR ENCUESTAS POR TIPO - NUEVO MÉTODO (si lo necesitas)
    @GetMapping("/search")
    public String buscarEncuestasPorTipo(@RequestParam String tipo, Model model) {
        List<SurveyDTO> encuestas = surveyService.buscarEncuestasPorTipo(tipo);
        model.addAttribute("encuestas", encuestas);
        model.addAttribute("tipoBuscado", tipo);
        return "player/dashboard";
    }
}