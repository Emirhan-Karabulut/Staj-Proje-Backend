package com.example.backend.controller;

import com.example.backend.entity.Bolum;
import com.example.backend.service.BolumService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/bolumler")
public class BolumController {

    private final BolumService bolumService;

    @Autowired
    public BolumController(BolumService bolumService) {
        this.bolumService = bolumService;
    }

    @GetMapping("/hepsi")
    public List<Bolum> tumBolumleriGetir() {
        log.info("[BÖLÜM-LİSTELE] Tüm bölümler listeleme isteği alındı");

        try {
            List<Bolum> bolumler = bolumService.tumBolumleriGetir();
            log.info("[BÖLÜM-LİSTELE] Toplam {} bölüm listelendi", bolumler.size());
            return bolumler;
        } catch (Exception e) {
            log.error("[BÖLÜM-LİSTELE][HATA] Bölümleri listelerken hata oluştu: ", e);
            throw e;
        }
    }

    @PostMapping("/ekle")
    public ResponseEntity<?> ekleBolum(@Valid @RequestBody Bolum bolum) {
        log.info("[BÖLÜM-EKLE] Yeni bölüm ekleme isteği alındı: {}", bolum.getBolumAdi());

        try {
            if (bolumService.bolumAdiVarMi(bolum.getBolumAdi())) {
                log.warn("[BÖLÜM-EKLE] Bölüm adı zaten mevcut: {}", bolum.getBolumAdi());
                return ResponseEntity.badRequest().body("Bu bölüm adı zaten kayıtlı");
            }

            Bolum kaydedilenBolum = bolumService.bolumKaydet(bolum);
            log.info("[BÖLÜM-EKLE] Bölüm başarıyla kaydedildi - ID: {}, Ad: {}",
                    kaydedilenBolum.getId(), kaydedilenBolum.getBolumAdi());

            return ResponseEntity.ok(kaydedilenBolum);

        } catch (Exception e) {
            log.error("[BÖLÜM-EKLE][HATA] Bölüm eklerken hata oluştu: ", e);
            throw e;
        }
    }
}