package com.example.backend.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Slf4j
@Configuration
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        log.info("[GÜVENLİK] BCryptPasswordEncoder bean'i oluşturuluyor");
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        log.info("[GÜVENLİK] Security filter chain konfigürasyonu başlatıldı");

        try {
            http
                    .cors(cors -> {
                        log.debug("[GÜVENLİK-CORS] CORS konfigürasyonu uygulanıyor");
                        cors.configurationSource(corsConfigurationSource());
                    })
                    .csrf(csrf -> {
                        log.info("[GÜVENLİK-CSRF] CSRF KAPATILDI (api için)");
                        csrf.disable();
                    })
                    .authorizeHttpRequests(auth -> {
                        auth.requestMatchers("/api/**").permitAll(); // Tüm api endpointlerine izin ver
                        auth.anyRequest().authenticated();
                    });

            log.info("[GÜVENLİK] Security filter chain başarıyla konfigüre edildi");
            return http.build();

        } catch (Exception e) {
            log.error("[GÜVENLİK][HATA] Security filter chain konfigürasyonunda hata oluştu: ", e);
            throw e;
        }
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        log.info("[GÜVENLİK-CORS] CORS konfigürasyonu oluşturuluyor");
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of(
                "http://localhost:4200",
                "https://staj-proje-frontend.up.railway.app",
                "https://emirhan-karabulut.github.io"
        ));
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setAllowCredentials(true);

        log.debug("[GÜVENLİK-CORS] İzin verilen origin: {}", configuration.getAllowedOrigins());
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);

        log.info("[GÜVENLİK-CORS] CORS konfigürasyonu başarıyla oluşturuldu");
        return source;
    }
}