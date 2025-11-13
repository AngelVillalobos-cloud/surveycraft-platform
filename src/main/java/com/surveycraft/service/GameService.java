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

    /**
     * Lógica de CREACIÓN (DTO -> Entidad -> DTO)
     */
    public GameDTO createGame(GameDTO gameDTO) {
        
        // 1. Validar y buscar entidades relacionadas
        Category category = categoryRepository.findById(gameDTO.getCategoriaId())
                .orElseThrow(() -> new RuntimeException("Categoría no encontrada"));

        // 2. Mapear de DTO a Entidad (DTO -> Entity)
        Videojuego newGame = new Videojuego();
        newGame.setTitulo(gameDTO.getTitulo());
        newGame.setDescripcion(gameDTO.getDescripcion());
        newGame.setCategory(category); // Asignar la entidad completa

        // 3. Guardar la Entidad en la BD
        Videojuego savedGame = videojuegoRepository.save(newGame);

        // 4. Mapear la Entidad guardada de vuelta a un DTO (Entity -> DTO)
        //    (Este DTO SÍ tendrá el ID y el nombre de la categoría)
        return mapEntityToDTO(savedGame);
    }

    /**
     * Lógica de LECTURA (Entidad -> DTO)
     */
    public GameDTO getGameById(Long id) {
        Videojuego game = videojuegoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Juego no encontrado"));
        
        // Mapear la entidad encontrada a un DTO para devolverla
        return mapEntityToDTO(game);
    }

    /**
     * Lógica de LECTURA MÚLTIPLE (Entidad -> DTO)
     */
    public List<GameDTO> getAllGames() {
        return videojuegoRepository.findAll().stream()
                .map(this::mapEntityToDTO) // Usa el mismo mapeador
                .collect(Collectors.toList());
    }

    // --- MÉTODO AYUDANTE DE MAPEO ---

    /**
     * Mapeador privado que convierte una Entidad a un DTO.
     * Esta es la clave para rellenar los campos de "Salida".
     */
    private GameDTO mapEntityToDTO(Videojuego game) {
        GameDTO dto = new GameDTO();
        
        // Rellenar campos de Salida
        dto.setId(game.getId());
        dto.setCategoriaNombre(game.getCategory().getNombreCategoria());
        
        // Rellenar campos comunes
        dto.setTitulo(game.getTitulo());
        dto.setDescripcion(game.getDescripcion());
        
        // Rellenar campo de referencia (útil si se quiere editar)
        dto.setCategoriaId(game.getCategory().getId());
        
        return dto;
    }
}