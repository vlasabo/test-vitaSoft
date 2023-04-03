package ru.vitasoft.testWork.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import ru.vitasoft.testWork.model.security.Role;
import ru.vitasoft.testWork.model.user.User;
import ru.vitasoft.testWork.repository.UserRepository;

import java.util.HashSet;
import java.util.Set;

@Configuration
@RequiredArgsConstructor
public class ApplicationConfig {

    private final UserRepository userRepository;

    @Bean
    public UserDetailsService userDetailsService() {
        Set<Role> roles = new HashSet<>();
        roles.add(Role.ADMIN);
        User user = new User(1L, passwordEncoder().encode("admin"), "admin", null, roles);
        userRepository.save(user);

        roles.clear();
        roles.add(Role.USER);
        User user2 = new User(2L, passwordEncoder().encode("user"), "user", null, roles);
        userRepository.save(user2);

        roles.clear();
        roles.add(Role.OPERATOR);
        User user3 = new User(3L, passwordEncoder().encode("operator"), "operator", null, roles);
        userRepository.save(user3);

        return username -> userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("no user with username ".concat(username)));
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();
        authenticationProvider.setUserDetailsService(userDetailsService());
        authenticationProvider.setPasswordEncoder(passwordEncoder());
        return authenticationProvider;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
