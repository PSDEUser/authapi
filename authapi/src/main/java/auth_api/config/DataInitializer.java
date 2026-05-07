package auth_api.config;

import auth_api.model.Role;
import auth_api.model.User;
import auth_api.repository.RoleRepository;
import auth_api.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;

import java.util.Set;

@Configuration
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    @Override
    public void run(String... args) {

        Role userRole = roleRepository.findByName("USER")
                .orElseGet(() -> roleRepository.save(
                        Role.builder().name("USER").build()
                ));

        if (!userRepository.existsByEmail("test@example.com")) {

            User user = User.builder()
                    .email("test@example.com")
                    .password("123456") // luego lo mejoramos
                    .roles(Set.of(userRole))
                    .build();

            userRepository.save(user);

            System.out.println("🔥 Usuario de prueba creado");
        }
    }
}