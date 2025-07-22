package com.example.backend.service;

import com.example.backend.entity.Kullanici;
import com.example.backend.entity.Role;
import com.example.backend.repository.KullaniciRepository;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import com.example.backend.service.MailService;

@Slf4j
@Service
public class KullaniciService {

    private final KullaniciRepository kullaniciRepository;
    private final PasswordEncoder passwordEncoder;
    private final Validator validator;
    private final MailService mailService;

    @Autowired
    public KullaniciService(KullaniciRepository kullaniciRepository,
                            PasswordEncoder passwordEncoder,
                            Validator validator,
                            MailService mailService) {
        this.kullaniciRepository = kullaniciRepository;
        this.passwordEncoder = passwordEncoder;
        this.validator = validator;
        this.mailService = mailService;
    }

    // Email ile giriş
    public Kullanici girisYap(String email, String sifre) {
        log.info("[KULLANICI-GİRİŞ] Giriş denemesi başlatıldı: {}", email);

        try {
            Kullanici kullanici = kullaniciRepository.findByEmail(email)
                    .filter(k -> {
                        boolean sifreDogruMu = passwordEncoder.matches(sifre, k.getSifre());
                        log.debug("[KULLANICI-GİRİŞ] Şifre kontrolü sonucu: {}", sifreDogruMu);
                        return sifreDogruMu;
                    })
                    .orElse(null);

            if (kullanici != null) {
                log.info("[KULLANICI-GİRİŞ] Başarılı giriş: {}", email);
            } else {
                log.warn("[KULLANICI-GİRİŞ] Başarısız giriş denemesi: {}", email);
            }

            return kullanici;

        } catch (Exception e) {
            log.error("[KULLANICI-GİRİŞ][HATA] Giriş kontrolü sırasında hata: ", e);
            throw e;
        }
    }

    // Email ile kayıt
    public Kullanici kayitOl(Kullanici kullanici) {
        log.info("[KULLANICI-KAYIT] Yeni kullanıcı kayıt işlemi başlatıldı: {}", kullanici.getEmail());

        try {
            Set<ConstraintViolation<Kullanici>> violations = validator.validate(kullanici);
            if (!violations.isEmpty()) {
                log.warn("[KULLANICI-KAYIT] Validasyon hatası: {} için {} hata",
                        kullanici.getEmail(), violations.size());
                violations.forEach(v -> log.debug("[KULLANICI-KAYIT] Validasyon hatası: {}", v.getMessage()));
                throw new ConstraintViolationException(violations);
            }

            if (kullaniciRepository.findByEmail(kullanici.getEmail()).isPresent()) {
                log.warn("[KULLANICI-KAYIT] E-posta zaten kayıtlı: {}", kullanici.getEmail());
                throw new IllegalArgumentException("Bu e-posta adresi zaten kayıtlı!");
            }

            kullanici.setRol(Role.USER); // Burada otomatik USER atanır!

            String hashlenmemisSifre = kullanici.getSifre();
            String hashlenmisSifre = passwordEncoder.encode(hashlenmemisSifre);
            kullanici.setSifre(hashlenmisSifre);
            log.debug("[KULLANICI-KAYIT] Şifre hashlendi: {}", kullanici.getEmail());

            Kullanici kaydedilenKullanici = kullaniciRepository.save(kullanici);
            log.info("[KULLANICI-KAYIT] Kullanıcı başarıyla kaydedildi - ID: {}, Email: {}",
                    kaydedilenKullanici.getId(), kaydedilenKullanici.getEmail());

            return kaydedilenKullanici;

        } catch (Exception e) {
            log.error("[KULLANICI-KAYIT][HATA] Kullanıcı kayıt sırasında hata: ", e);
            throw e;
        }
    }

    // Şifre sıfırlama
    public boolean sifremiUnuttum(String email) {
        Optional<Kullanici> opt = kullaniciRepository.findByEmail(email);
        if (opt.isEmpty()) {
            log.warn("[ŞIFRE-SIFIRLAMA] E-posta bulunamadı: {}", email);
            return true; // Güvenlik için her zaman true dön
        }

        Kullanici kullanici = opt.get();
        String anahtar = UUID.randomUUID().toString();
        kullanici.setSifreSifirlamaAnahtari(anahtar);
        kullanici.setSifreSifirlamaAnahtariBitis(LocalDateTime.now().plusHours(1));
        kullaniciRepository.save(kullanici);

        String sifirlamaLinki = "http://localhost:4200/sifre-sifirla?anahtar=" + anahtar;
        mailService.sendMail(email, "Şifre Sıfırlama",
                "Şifrenizi sıfırlamak için tıklayın: " + sifirlamaLinki);
        log.info("[ŞIFRE-SIFIRLAMA] Link gönderildi: {}", email);
        return true;
    }

    // Şifre sıfırlama işlemi
    public boolean sifreSifirla(String anahtar, String yeniSifre) {
        Optional<Kullanici> opt = kullaniciRepository.findBySifreSifirlamaAnahtari(anahtar);
        if (opt.isEmpty()) {
            log.warn("[ŞIFRE-SIFIRLAMA] Geçersiz anahtar: {}", anahtar);
            return false;
        }

        Kullanici kullanici = opt.get();
        if (kullanici.getSifreSifirlamaAnahtariBitis().isBefore(LocalDateTime.now())) {
            log.warn("[ŞIFRE-SIFIRLAMA] Süre dolmuş anahtar: {}", anahtar);
            return false;
        }

        kullanici.setSifre(passwordEncoder.encode(yeniSifre));
        kullanici.setSifreSifirlamaAnahtari(null);
        kullanici.setSifreSifirlamaAnahtariBitis(null);
        kullaniciRepository.save(kullanici);

        log.info("[ŞIFRE-SIFIRLAMA] Şifre güncellendi: {}", kullanici.getEmail());
        return true;
    }
}