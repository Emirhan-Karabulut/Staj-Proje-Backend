package com.example.backend.entity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Email;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Data
public class Kullanici {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Pattern(
            regexp = "^[a-zA-Z0-9_çğıöşüÇĞİÖŞÜ]{3,32}@.+$",
            message = "E-posta adresinin @ öncesi kısmı karakter, harf, rakam, alt çizgi veya Türkçe karakter olmalı"
    )
    @Email(message = "Geçerli bir e-posta adresi giriniz")
    @NotBlank(message = "E-posta adresi zorunludur")
    @Column(unique = true, nullable = false)
    private String email;

    @NotBlank(message = "Şifre zorunludur")
    @Column(nullable = false)
    private String sifre;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role rol = Role.USER; // Varsayılan olarak USER

    // Şifremi unuttum işlemi için
    private String sifreSifirlamaAnahtari;
    private LocalDateTime sifreSifirlamaAnahtariBitis;
}