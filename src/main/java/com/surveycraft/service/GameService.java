package com.surveycraft.service;

import com.surveycraft.dto.game.GameDTO;
import com.surveycraft.entity.Category;
import com.surveycraft.entity.Videojuego;
import com.surveycraft.repository.CategoryRepository;
import com.surveycraft.repository.VideojuegoRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class GameService {

    private final VideojuegoRepository videojuegoRepository;
    private final CategoryRepository categoryRepository;

    public GameService(VideojuegoRepository videojuegoRepository, CategoryRepository categoryRepository) {
        this.videojuegoRepository = videojuegoRepository;
        this.categoryRepository = categoryRepository;
    }

 // Método modificado para crear categorías al vuelo
    public GameDTO createGame(GameDTO gameDTO) {
        Category category = null;

        // 1. ¿El Frontend nos mandó un NOMBRE de categoría? (Ej: "Terror")
        if (gameDTO.getNombreCategoria() != null && !gameDTO.getNombreCategoria().isEmpty()) {
            String nombreCat = gameDTO.getNombreCategoria();
            
            // Buscamos si ya existe en la BD
            category = categoryRepository.findByNombreCategoria(nombreCat)
                    .orElseGet(() -> {
                        // SI NO EXISTE -> ¡LA CREAMOS AQUÍ MISMO!
                        Category newCat = new Category();
                        newCat.setNombreCategoria(nombreCat);
                        return categoryRepository.save(newCat);
                    });
        } 
        // 2. ¿O nos mandó un ID? (Lógica de respaldo)
        else if (gameDTO.getCategoriaId() != null) {
            category = categoryRepository.findById(gameDTO.getCategoriaId())
                    .orElseThrow(() -> new RuntimeException("Categoría no encontrada por ID"));
        } else {
            throw new RuntimeException("Debes escribir una categoría");
        }

        // 3. Crear el videojuego con esa categoría
        Videojuego newGame = new Videojuego();
        newGame.setTitulo(gameDTO.getTitulo());
        newGame.setDescripcion(gameDTO.getDescripcion());
        newGame.setCategoria(category); // Asignamos la categoría encontrada o creada

        
        Videojuego savedGame = videojuegoRepository.save(newGame);
        
        // Devolver el DTO
        return mapEntityToDTO(savedGame);
    }   

    public GameDTO getGameById(Long id) {
        Videojuego game = videojuegoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Juego no encontrado"));
        return mapEntityToDTO(game);
    }

    public List<GameDTO> getAllGames() {
        return videojuegoRepository.findAll().stream()
                .map(this::mapEntityToDTO)
                .collect(Collectors.toList());
    }

    public Videojuego updateGame(Long id, Videojuego gameData) {
        Videojuego game = videojuegoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Game not found"));

        game.setTitulo(gameData.getTitulo());
        game.setDescripcion(gameData.getDescripcion());
        if (gameData.getCategoria() != null) {
            game.setCategoria(gameData.getCategoria());
        }

        return videojuegoRepository.save(game);
    }

    public void deleteGame(Long id) {
        if (!videojuegoRepository.existsById(id)) {
            throw new RuntimeException("Game not found");
        }
        videojuegoRepository.deleteById(id);
    }

    private GameDTO mapEntityToDTO(Videojuego game) {
        GameDTO dto = new GameDTO();
        dto.setId(game.getId());
        dto.setNombreCategoria(game.getCategoria().getNombreCategoria());
        dto.setTitulo(game.getTitulo());
        dto.setDescripcion(game.getDescripcion());
        dto.setCategoriaId(game.getCategoria().getId());
        return dto;
    }
}