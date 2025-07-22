package com.example.backend.config;

import com.example.backend.entity.Bolum;
import com.example.backend.entity.Kullanici;
import com.example.backend.entity.Role;
import com.example.backend.repository.BolumRepository;
import com.example.backend.repository.KullaniciRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class DataInitializer implements CommandLineRunner {

    private final BolumRepository bolumRepository;
    private final KullaniciRepository kullaniciRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public DataInitializer(BolumRepository bolumRepository,
                           KullaniciRepository kullaniciRepository,
                           PasswordEncoder passwordEncoder) {
        this.bolumRepository = bolumRepository;
        this.kullaniciRepository = kullaniciRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        log.info("[DATA-INIT] Uygulama başlatılıyor - Veri kontrolü yapılıyor");

        try {
            long bolumSayisi = bolumRepository.count();
            log.info("[DATA-INIT] Mevcut bölüm sayısı: {}", bolumSayisi);

            // Daha önce bölüm eklenmemişse ekle
            if (bolumSayisi == 0) {
                log.info("[DATA-INIT] Varsayılan bölümler oluşturuluyor");
                initBolumler();
            } else {
                log.info("[DATA-INIT] Bölümler zaten mevcut, yeni bölüm eklenmeyecek");
            }

            // İlk admin ekle
            if (!kullaniciRepository.existsByEmail("emirkarabulut10@gmail.com")) {
                Kullanici admin = new Kullanici();
                admin.setEmail("emirkarabulut10@gmail.com");
                admin.setSifre(passwordEncoder.encode("admin123")); // Şifreyi güvenli şekilde hashle
                admin.setRol(Role.ADMIN); // Enum ise böyle, yoksa string olarak "ADMIN" ata
                Kullanici saved = kullaniciRepository.save(admin);
                log.info("[DATA-INIT] İlk admin kullanıcı eklendi: {} (id={})", saved.getEmail(), saved.getId());
            } else {
                log.info("[DATA-INIT] İlk ADMIN Kullanıcısı zaten mevcut, tekrar eklenmeyecek");
            }

        } catch (Exception e) {
            log.error("[DATA-INIT][HATA] Uygulama başlatılırken hata oluştu: ", e);
            throw e;
        }
    }

    private void initBolumler() {
        String[] bolumAdlari = {"Pres", "Gövde", "Boya", "Montaj", "Motor Montaj"};

        log.info("[DATA-INIT] Toplam {} varsayılan bölüm oluşturulacak", bolumAdlari.length);

        for (String bolumAdi : bolumAdlari) {
            try {
                Bolum bolum = new Bolum();
                bolum.setBolumAdi(bolumAdi);
                Bolum kaydedilenBolum = bolumRepository.save(bolum);
                log.info("[DATA-INIT] Bölüm oluşturuldu - ID: {}, Ad: {}",
                        kaydedilenBolum.getId(), kaydedilenBolum.getBolumAdi());
            } catch (Exception e) {
                log.error("[DATA-INIT][HATA] Bölüm oluşturulurken hata oluştu - Ad: {}, Hata: ", bolumAdi, e);
            }
        }

        log.info("[DATA-INIT] Varsayılan bölümler başarıyla oluşturuldu");
    }
}