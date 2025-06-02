package epam.finalProject.config;

import epam.finalProject.security.UserDetailsServiceImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.core.userdetails.UserDetailsService;

@Configuration
@EnableMethodSecurity(prePostEnabled = true)
public class WebSecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(auth -> auth
                        // публичные страницы
                        .requestMatchers("/login", "/register", "/css/**", "/books", "/home").permitAll()

                        // Корзина – только аутентифицированный
                        .requestMatchers("/basket/**").authenticated()

                        // История покупок – только аутентифицированный
                        .requestMatchers("/purchase-history").authenticated()

                        // Управление пользователями – только ADMIN
                        .requestMatchers("/admin/users/**").hasAuthority("ADMIN")

                        // управления библиотекой (книги, жанры, авторы) – ADMIN и LIBRARIAN
                        .requestMatchers("/admin/books/**", "/admin/genres/**", "/admin/authors/**")
                        .hasAnyAuthority("ADMIN","LIBRARIAN")

                        // остальные запросы – аутентифицированные
                        .anyRequest().authenticated()

                )
                .formLogin(form -> form
                        .loginPage("/login")
                        .defaultSuccessUrl("/profile", true)
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutSuccessUrl("/login")
                        .permitAll()
                );

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public UserDetailsService userDetailsService() {
        return new UserDetailsServiceImpl(); // ваша реализация
    }

    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}
