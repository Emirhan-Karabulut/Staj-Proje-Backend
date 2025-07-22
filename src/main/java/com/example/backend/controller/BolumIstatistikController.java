package com.example.backend.controller;

import com.example.backend.service.BolumIstatistikService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/istatistikler")
public class BolumIstatistikController {

    private final BolumIstatistikService bolumIstatistikService;

    @Autowired
    public BolumIstatistikController(BolumIstatistikService bolumIstatistikService) {
        this.bolumIstatistikService = bolumIstatistikService;
    }

    @GetMapping("/bolumler")
    public ResponseEntity<Map<String, Long>> getBolumIstatistikleri() {
        log.info("[İSTATİSTİK-LİSTELE] Bölüm istatistikleri isteği alındı");

        try {
            Map<String, Long> istatistikler = bolumIstatistikService.getBolumIscileriSayisi();
            log.info("[İSTATİSTİK-LİSTELE] Bölüm istatistikleri başarıyla hesaplandı - {} bölüm", istatistikler.size());
            log.debug("[İSTATİSTİK-LİSTELE] İstatistik detayları: {}", istatistikler);

            return ResponseEntity.ok(istatistikler);

        } catch (Exception e) {
            log.error("[İSTATİSTİK-LİSTELE][HATA] Bölüm istatistikleri hesaplanırken hata oluştu: ", e);
            throw e;
        }
    }
}