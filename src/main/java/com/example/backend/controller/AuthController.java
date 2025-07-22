package com.example.backend.controller;

import com.example.backend.entity.Kullanici;
import com.example.backend.service.KullaniciService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final KullaniciService kullaniciService;

    @Autowired
    public AuthController(KullaniciService kullaniciService) {
        this.kullaniciService = kullaniciService;
    }

    @PostMapping("/kayit")
    public ResponseEntity<?> kayitOl(@RequestBody Kullanici kullaniciReq) {
        try {
            log.info("[KULLANICI-KAYIT] Kayıt isteği alındı: {}", kullaniciReq.getEmail());
            kullaniciService.kayitOl(kullaniciReq);
            log.info("[KULLANICI-KAYIT] Kullanıcı başarıyla kaydedildi: {}", kullaniciReq.getEmail());
            Map<String, String> response = new HashMap<>();
            response.put("message", "Kayıt başarılı!");
            return ResponseEntity.ok(response);
        } catch (ConstraintViolationException e) {
            log.warn("[KULLANICI-KAYIT] Validasyon hatası: {}", e.getMessage());
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("message", "Validasyon hatası");
            errorResponse.put("errors", e.getConstraintViolations().stream()
                    .map(v -> v.getMessage()).toArray());
            return ResponseEntity.badRequest().body(errorResponse);
        } catch (IllegalArgumentException e) {
            log.warn("[KULLANICI-KAYIT] Kayıt işlemi reddedildi: {}", e.getMessage());
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        } catch (Exception e) {
            log.error("[KULLANICI-KAYIT][HATA] Beklenmeyen hata oluştu: ", e);
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("message", "Bir hata oluştu!");
            return ResponseEntity.status(500).body(errorResponse);
        }
    }

    @PostMapping("/giris")
    public ResponseEntity<?> girisYap(@RequestBody Map<String, String> loginRequest, HttpServletRequest request) {
        String email = loginRequest.get("email");
        try {
            log.info("[KULLANICI-GİRİŞ] Giriş isteği alındı: {}", email);

            String sifre = loginRequest.get("sifre");
            Kullanici kullanici = kullaniciService.girisYap(email, sifre);

            if (kullanici != null) {
                log.info("[KULLANICI-GİRİŞ] Başarılı giriş: {}", email);

                // --- KULLANICIYI SPRING SECURITY CONTEXT'E ve SESSION'A YAZ! ---
                org.springframework.security.core.userdetails.User userDetails = new org.springframework.security.core.userdetails.User(
                        kullanici.getEmail(),
                        kullanici.getSifre(),
                        java.util.List.of(new org.springframework.security.core.authority.SimpleGrantedAuthority("ROLE_" + kullanici.getRol().name()))
                );
                org.springframework.security.authentication.UsernamePasswordAuthenticationToken auth =
                        new org.springframework.security.authentication.UsernamePasswordAuthenticationToken(
                                userDetails, null, userDetails.getAuthorities()
                        );
                org.springframework.security.core.context.SecurityContextHolder.getContext().setAuthentication(auth);

                // Burası kritik: SecurityContext'i session'a kaydet!
                HttpSession session = request.getSession(true);
                session.setAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY, SecurityContextHolder.getContext());
                // ---------------------------------------------------------------

                Map<String, String> response = new HashMap<>();
                response.put("message", "Giriş başarılı!");
                return ResponseEntity.ok(response);
            } else {
                log.warn("[KULLANICI-GİRİŞ] Başarısız giriş denemesi: {}", email);
                Map<String, String> errorResponse = new HashMap<>();
                errorResponse.put("message", "E-posta veya şifre hatalı!");
                return ResponseEntity.badRequest().body(errorResponse);
            }
        } catch (Exception e) {
            log.error("[KULLANICI-GİRİŞ][HATA] Giriş sırasında beklenmeyen hata: ", e);
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("message", "Bir hata oluştu!");
            return ResponseEntity.status(500).body(errorResponse);
        }
    }

    @PostMapping("/cikis")
    public ResponseEntity<?> cikisYap(HttpServletRequest request) {
        log.info("[KULLANICI-ÇIKIŞ] Çıkış isteği alındı.");
        HttpSession session = request.getSession(false);
        if (session != null) {
            log.info("[KULLANICI-ÇIKIŞ] Oturum (session) bulundu ve geçersiz kılınıyor. Oturum ID: {}", session.getId());
            session.invalidate();
        } else {
            log.info("[KULLANICI-ÇIKIŞ] Geçerli bir oturum (session) bulunamadı.");
        }
        SecurityContextHolder.clearContext();
        log.info("[KULLANICI-ÇIKIŞ] SecurityContext temizlendi. Çıkış işlemi tamamlandı.");
        return ResponseEntity.ok(Map.of("message", "Çıkış başarılı!"));
    }

    @PostMapping("/sifremi-unuttum")
    public ResponseEntity<?> sifremiUnuttum(@RequestBody Map<String, String> req) {
        String email = req.get("email");
        log.info("[ŞİFRE-SIFIRLAMA] Şifremi unuttum isteği alındı. E-posta: {}", email);

        boolean sonuc = kullaniciService.sifremiUnuttum(email);

        if (sonuc) {
            log.info("[ŞİFRE-SIFIRLAMA] Şifre sıfırlama e-postası başarıyla gönderildi. E-posta: {}", email);
            return ResponseEntity.ok(Map.of("message", "Şifre sıfırlama e-postası gönderildi!"));
        } else {
            log.warn("[ŞİFRE-SIFIRLAMA] Şifre sıfırlama e-postası gönderilemedi! E-posta: {}", email);
            return ResponseEntity.badRequest().body(Map.of("message", "Şifre sıfırlama e-postası gönderilemedi!"));
        }
    }

    @PostMapping("/sifre-sifirla")
    public ResponseEntity<?> sifreSifirla(@RequestBody Map<String, String> req) {
        String anahtar = req.get("anahtar");
        String yeniSifre = req.get("yeniSifre");

        boolean sonuc = kullaniciService.sifreSifirla(anahtar, yeniSifre);
        if (sonuc) {
            log.info("[ŞİFRE-SIFIRLAMA] Şifre başarıyla güncellendi. Anahtar: {}", anahtar);
            return ResponseEntity.ok(Map.of("message", "Şifre başarıyla güncellendi!"));
        } else {
            log.warn("[ŞİFRE-SIFIRLAMA] Geçersiz veya süresi dolmuş anahtar kullanıldı: {}", anahtar);
            return ResponseEntity.badRequest().body(Map.of("message", "Geçersiz veya süresi dolmuş anahtar!"));
        }
    }
}