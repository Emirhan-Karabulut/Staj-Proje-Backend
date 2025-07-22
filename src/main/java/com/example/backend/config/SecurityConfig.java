package com.example.backend.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
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
            CookieCsrfTokenRepository repo = CookieCsrfTokenRepository.withHttpOnlyFalse();
            repo.setCookiePath("/");
            log.debug("[GÜVENLİK] CSRF repository ayarlandı: {}", repo);

            http
                    .cors(cors -> {
                        log.debug("[GÜVENLİK-CORS] CORS konfigürasyonu uygulanıyor");
                        cors.configurationSource(corsConfigurationSource());
                    })
                    .csrf(csrf -> {
                        log.debug("[GÜVENLİK-CSRF] CSRF konfigürasyonu uygulanıyor");
                        csrf.csrfTokenRepository(repo);

                        csrf.csrfTokenRequestHandler((request, response, deferredCsrfToken) -> {
                            String uri = request.getRequestURI();
                            String method = request.getMethod();
                            log.debug("[GÜVENLİK-CSRF] CSRF token isteği alındı: Method={}, URI={}", method, uri);
                            try {
                                var token = deferredCsrfToken.get();
                                boolean tokenInCookie = false;
                                if (request.getCookies() != null) {
                                    tokenInCookie = Arrays.stream(request.getCookies())
                                            .anyMatch(c -> c.getName().equals(token.getParameterName()));
                                }
                                log.debug("[GÜVENLİK-CSRF] Token parametre adı: {}, header adı: {}, değer: {}",
                                        token.getParameterName(), token.getHeaderName(), token.getToken());
                                if (tokenInCookie) {
                                    log.info("[GÜVENLİK-CSRF] CSRF token mevcut ve kullanıldı - {}: {}", uri, token.getToken());
                                } else {
                                    log.info("[GÜVENLİK-CSRF] CSRF token yeni oluşturuldu - {}: {}", uri, token.getToken());
                                }
                            } catch (Exception e) {
                                log.error("[GÜVENLİK-CSRF][HATA] CSRF token alınırken hata - Method: {}, URI: {}: ", method, uri, e);
                            }
                        });
                    })
                    .authorizeHttpRequests(auth -> {
                        auth.requestMatchers("/api/auth/giris", "/api/auth/kayit", "/api/csrf").permitAll();
                        auth.requestMatchers(HttpMethod.PUT, "/api/isciler/**").hasRole("ADMIN");
                        auth.requestMatchers(HttpMethod.POST, "/api/isciler/ekle").hasRole("ADMIN");
                        auth.requestMatchers(HttpMethod.DELETE, "/api/isciler/sil/**").hasRole("ADMIN");
                        auth.requestMatchers(HttpMethod.GET, "/api/isciler/hepsi").authenticated();
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

        try {
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
            log.debug("[GÜVENLİK-CORS] İzin verilen method'lar: {}", configuration.getAllowedMethods());
            log.debug("[GÜVENLİK-CORS] İzin verilen header'lar: {}", configuration.getAllowedHeaders());
            log.debug("[GÜVENLİK-CORS] Credentials izni: {}", configuration.getAllowCredentials());

            UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
            source.registerCorsConfiguration("/**", configuration);

            log.info("[GÜVENLİK-CORS] CORS konfigürasyonu başarıyla oluşturuldu");
            return source;

        } catch (Exception e) {
            log.error("[GÜVENLİK-CORS][HATA] CORS konfigürasyonu oluşturulurken hata: ", e);
            throw e;
        }
    }
}