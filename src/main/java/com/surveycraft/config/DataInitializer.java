package com.surveycraft.config;

import com.surveycraft.entity.Role;
import com.surveycraft.repository.RoleRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    private final RoleRepository roleRepository;
    private final com.surveycraft.repository.CategoryRepository categoryRepository;
    private final com.surveycraft.repository.VideojuegoRepository videojuegoRepository;

    public DataInitializer(RoleRepository roleRepository,
            com.surveycraft.repository.CategoryRepository categoryRepository,
            com.surveycraft.repository.VideojuegoRepository videojuegoRepository) {
        this.roleRepository = roleRepository;
        this.categoryRepository = categoryRepository;
        this.videojuegoRepository = videojuegoRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        createRoleIfNotFound("ROLE_PLAYER");
        createRoleIfNotFound("ROLE_CRAFTER");

        // Seed Category
        com.surveycraft.entity.Category cat = createCategoryIfNotFound("General");

        // Seed Game (ID 1)
        createGameIfNotFound("Minecraft", "Juego por defecto", cat);
    }

    private void createRoleIfNotFound(String roleName) {
        if (roleRepository.findByNombre(roleName).isEmpty()) {
            Role role = new Role();
            role.setNombre(roleName);
            roleRepository.save(role);
            System.out.println("Rol creado: " + roleName);
        }
    }

    private com.surveycraft.entity.Category createCategoryIfNotFound(String nombre) {
        return categoryRepository.findByNombreCategoria(nombre)
                .orElseGet(() -> {
                    com.surveycraft.entity.Category c = new com.surveycraft.entity.Category();
                    c.setNombreCategoria(nombre);
                    return categoryRepository.save(c);
                });
    }

    private void createGameIfNotFound(String titulo, String desc, com.surveycraft.entity.Category cat) {
        if (videojuegoRepository.findByTitulo(titulo).isEmpty()) {
            com.surveycraft.entity.Videojuego g = new com.surveycraft.entity.Videojuego();
            g.setTitulo(titulo);
            g.setDescripcion(desc);
            g.setCategoria(cat);
            videojuegoRepository.save(g);
            System.out.println("Juego creado: " + titulo);
        }
    }
}
