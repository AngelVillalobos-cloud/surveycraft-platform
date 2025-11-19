package com.surveycraft.service;


import com.surveycraft.dto.auth.LoginRequest;
import com.surveycraft.dto.auth.RegisterRequest;
import com.surveycraft.entity.Role;
import com.surveycraft.entity.User;
import com.surveycraft.repository.RoleRepository;
import com.surveycraft.repository.UserRepository;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    // private final JwtProvider jwtProvider; // Descomentar cuando tengas la clase JWT

    public AuthService(UserRepository userRepository, 
                       RoleRepository roleRepository, 
                       PasswordEncoder passwordEncoder,
                       AuthenticationManager authenticationManager) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
    }

    // LÓGICA DE REGISTRO
    public void register(RegisterRequest request) {
        // 1. Validar si ya existe
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("El email ya está registrado");
        }

        // 2. Buscar el rol (El frontend manda "CRAFTER", buscamos "ROLE_CRAFTER")
        // Asegúrate que en tu BD los roles se llamen "ROLE_CRAFTER" y "ROLE_PLAYER"
        String nombreRol = "ROLE_" + request.getRole().toUpperCase();
        Role role = roleRepository.findByNombre(nombreRol)
                .orElseThrow(() -> new RuntimeException("Rol no encontrado: " + nombreRol));

        // 3. Crear el usuario
        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setRole(role);
        // ¡IMPORTANTE! Encriptar la contraseña antes de guardar
        user.setPassword(passwordEncoder.encode(request.getPassword()));

        userRepository.save(user);
    }

    // LÓGICA DE LOGIN
    public String login(LoginRequest request) {
        // 1. Autenticar usando el Manager de Spring (esto verifica el password automáticamente)
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );

        // 2. Si pasa, generar el Token
        // Por ahora retornamos un texto dummy hasta que configuremos JWT
        return "TOKEN_FALSO_TEMPORAL_12345"; 
        // return jwtProvider.generateToken(authentication); // Lo correcto con JWT
    }
}
