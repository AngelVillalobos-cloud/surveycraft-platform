package com.surveycraft.controller;


import com.surveycraft.dto.game.GameDTO;
import com.surveycraft.service.GameService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/games")
public class GameController {

    private final GameService gameService;

    public GameController(GameService gameService) {
        this.gameService = gameService;
    }

    /**
     * 1) Crear videojuego (Admin/Crafter)
     * POST /api/games
     *
     * Recibe un GameDTO (con id=null y categoriaId=NN)
     * Devuelve un GameDTO (con id=NN y categoriaNombre="...")
     */
    @PostMapping
    public ResponseEntity<GameDTO> createGame(@RequestBody GameDTO gameDTO) {
        GameDTO createdGame = gameService.createGame(gameDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdGame);
    }

    /**
     * 2) Obtener detalle de videojuego por id
     * GET /api/games/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<GameDTO> getGameById(@PathVariable Long id) {
        GameDTO game = gameService.getGameById(id);
        return ResponseEntity.ok(game);
    }

    /**
     * 3) Listar todos los videojuegos
     * GET /api/games
     */
    @GetMapping
    public ResponseEntity<List<GameDTO>> getAllGames() {
        List<GameDTO> games = gameService.getAllGames();
        return ResponseEntity.ok(games);
    }

    // ... (Tu otro método de getGamesByCategory también funciona igual)
}
