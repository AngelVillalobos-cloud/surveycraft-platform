package com.surveycraft.repository;

import com.surveycraft.entity.Videojuego;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VideojuegoRepository extends JpaRepository<Videojuego, Long> {
    
    // Fundamental para el filtro: "Ver todos los juegos de RPG"
    List<Videojuego> findByCategoriaId(Long categoriaId);

    // Buscador por título (ej. buscador en el navbar)
    List<Videojuego> findByTituloContainingIgnoreCase(String titulo);
}
