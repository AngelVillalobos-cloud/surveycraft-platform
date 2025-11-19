package com.surveycraft.controller;


import com.surveycraft.dto.auth.LoginRequest;
import com.surveycraft.dto.auth.RegisterRequest;
import com.surveycraft.service.AuthService; // Asumo que existe o lo crearás
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    /**
     * POST /api/auth/register
     * Crea un nuevo usuario (Player o Crafter).
     */
    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody RegisterRequest request) {
        // El servicio se encarga de validar y guardar
        authService.register(request); 
        return ResponseEntity.ok(Map.of("message", "Usuario registrado exitosamente"));
    }

    /**
     * POST /api/auth/login
     * Recibe email/password y devuelve un JWT.
     */
    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@RequestBody LoginRequest request) {
        // El servicio valida credenciales y genera el token JWT
        String token = authService.login(request);
        
        // Devolvemos el token en un JSON
        return ResponseEntity.ok(Map.of(
            "token", token,
            "type", "Bearer"
        ));
    }
}