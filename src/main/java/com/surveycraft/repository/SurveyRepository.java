package com.surveycraft.repository;

import com.surveycraft.entity.Survey;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;
import org.springframework.data.repository.query.Param;
import java.util.Optional;

public interface SurveyRepository extends JpaRepository<Survey, Long> {
    
    // Consultas básicas
    List<Survey> findByEstado(String estado);
    List<Survey> findByCrafterId(Long crafterId);
    Optional<Survey> findByTitulo(String titulo);
    
    // Consultas personalizadas
    @Query("SELECT s FROM Survey s WHERE s.esEspecificaJuego = true AND s.juegoId = :juegoId")
    List<Survey> findSurveysByJuegoId(@Param("juegoId") Long juegoId);
    
    @Query("SELECT COUNT(s) FROM Survey s WHERE s.crafterId = :crafterId")
    Long countByCrafterId(@Param("crafterId") Long crafterId);
    
    @Query("SELECT s FROM Survey s WHERE s.estado = 'activa' AND s.fechaLimite > CURRENT_TIMESTAMP")
    List<Survey> findEncuestasActivasYVigentes();
    
    @Query("SELECT s FROM Survey s WHERE s.tipoEncuesta = :tipoEncuesta AND s.estado = 'activa'")
    List<Survey> findByTipoEncuesta(@Param("tipoEncuesta") String tipoEncuesta);
}
