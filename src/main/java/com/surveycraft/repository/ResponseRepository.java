package com.surveycraft.repository;


import com.surveycraft.entity.Option;
import com.surveycraft.entity.Response;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface ResponseRepository extends JpaRepository<Response, Long> {
    
    // Consultas básicas
    List<Response> findByPlayerId(Long playerId);
    List<Response> findByQuestionId(Long questionId);
    
    // Consultas personalizadas
    @Query("SELECT r FROM Response r JOIN Question q ON r.questionId = q.questionId WHERE q.surveyId = :surveyId")
    List<Response> findBySurveyId(@Param("surveyId") Long surveyId);
    
    @Query("SELECT COUNT(r) FROM Response r JOIN Question q ON r.questionId = q.questionId WHERE q.surveyId = :surveyId")
    Long countBySurveyId(@Param("surveyId") Long surveyId);
    
    @Query("SELECT r FROM Response r JOIN Question q ON r.questionId = q.questionId WHERE r.playerId = :playerId AND q.surveyId = :surveyId")
    List<Response> findByPlayerIdAndSurveyId(@Param("playerId") Long playerId, @Param("surveyId") Long surveyId);
    
    @Query("SELECT COUNT(DISTINCT r.playerId) FROM Response r JOIN Question q ON r.questionId = q.questionId WHERE q.surveyId = :surveyId")
    Long countUniquePlayersBySurveyId(@Param("surveyId") Long surveyId);
    
    @Query("SELECT r FROM Response r WHERE r.playerId = :playerId AND r.questionId IN (SELECT q.questionId FROM Question q WHERE q.surveyId = :surveyId)")
    List<Response> findPlayerResponsesForSurvey(@Param("playerId") Long playerId, @Param("surveyId") Long surveyId);
    
    long countBySelectedOption(Option selectedOption);
}
