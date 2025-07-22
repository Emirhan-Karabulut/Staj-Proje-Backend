package com.example.backend.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

import java.util.Date;

@Entity
@Data
public class Isci {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "İsim zorunludur")
    @Pattern(regexp = "^[a-zA-ZçÇğĞıİöÖşŞüÜ\\s-]+$",
            message = "İsim sadece harf, boşluk ve tire (-) içerebilir")
    @Column(nullable = false)
    private String isim;

    @NotBlank(message = "Soyisim zorunludur")
    @Pattern(regexp = "^[a-zA-ZçÇğĞıİöÖşŞüÜ\\s-]+$",
            message = "Soyisim sadece harf, boşluk ve tire (-) içerebilir")
    @Column(nullable = false)
    private String soyisim;

    @NotBlank(message = "TC kimlik numarası zorunludur")
    @Pattern(regexp = "^[1-9][0-9]{10}$", message = "TC kimlik numarası 11 haneli olmalı ve 0 ile başlamamalıdır")
    @Column(unique = true)
    private String tcKimlikNo;

    @NotNull(message = "Doğum tarihi zorunludur")
    @Temporal(TemporalType.DATE)
    private Date dogumTarihi;

    // ManyToOne ilişkisi
    @ManyToOne
    @JoinColumn(name = "bolum_id", nullable = false)
    private Bolum bolum;
}