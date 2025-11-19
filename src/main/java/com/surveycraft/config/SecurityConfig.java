package com.surveycraft.config;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
// import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter; 
// Descomenta la línea de arriba y la del filtro abajo cuando tengas tu JwtAuthenticationFilter

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    // Inyectar tu filtro JWT aquí cuando lo crees
    // @Autowired private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            // 1. Desactivar CSRF y configurar CORS (por defecto)
            .csrf(csrf -> csrf.disable())
            
            // 2. Configurar rutas públicas y privadas
            .authorizeHttpRequests(auth -> auth
                // -- Zona Pública --
                .requestMatchers("/api/auth/**").permitAll()      // Login y Registro
                .requestMatchers(HttpMethod.GET, "/api/games/**").permitAll()   // Ver juegos
                .requestMatchers(HttpMethod.GET, "/api/surveys/**").permitAll() // Ver encuestas/resultados
                
                // -- Zona Crafters (Crear cosas) --
                .requestMatchers(HttpMethod.POST, "/api/surveys").hasRole("CRAFTER")
                .requestMatchers(HttpMethod.POST, "/api/games").hasRole("CRAFTER") // O Admin
                
                // -- Zona Players (Responder) --
                .requestMatchers(HttpMethod.POST, "/api/surveys/*/respond").hasRole("PLAYER")
                
                // -- Todo lo demás requiere autenticación --
                .anyRequest().authenticated()
            )
            
            // 3. Manejo de sesión sin estado (Stateless)
            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            );

        // 4. Añadir el filtro JWT antes del filtro de usuario/contraseña
        // http.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    // Bean para encriptar contraseñas
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // Bean para manejar la autenticación en el AuthService
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}
