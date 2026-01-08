package Alura.Hackaton.SentimentAPI.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(csrf -> csrf.disable())
                .cors(Customizer.withDefaults())
                .headers(h -> h.frameOptions(f -> f.sameOrigin()))
                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth

                        // 🔓 front + auth + h2
                        .requestMatchers(
                                "/", "/index.html",
                                "/**/*.html",
                                "/**/*.css",
                                "/**/*.js",
                                "/favicon.ico",
                                "/auth/**",
                                "/health",
                                "/h2-console/**"
                        ).permitAll()

                        //GET público
                        .requestMatchers(HttpMethod.GET, "/api/avaliacoes/**").permitAll()
                        //Daqui para frente precisa estar logado
                        .requestMatchers(HttpMethod.POST, "/api/avaliacoes/**").permitAll()
                        .requestMatchers(HttpMethod.PUT, "/api/avaliacoes/**").permitAll()
                        .requestMatchers(HttpMethod.DELETE, "/api/avaliacoes/**").authenticated()

                        .anyRequest().authenticated()
                )
                .build();
    }
}
