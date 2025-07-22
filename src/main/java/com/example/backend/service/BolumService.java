package com.example.backend.service;

import com.example.backend.entity.Bolum;
import com.example.backend.repository.BolumRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class BolumService {

    private final BolumRepository bolumRepository;

    @Autowired
    public BolumService(BolumRepository bolumRepository) {
        this.bolumRepository = bolumRepository;
    }

    public List<Bolum> tumBolumleriGetir() {
        log.debug("[BÖLÜM-LİSTELE] Tüm bölümler getiriliyor");

        try {
            List<Bolum> bolumler = bolumRepository.findAll();
            log.info("[BÖLÜM-LİSTELE] Toplam {} bölüm bulundu", bolumler.size());
            return bolumler;
        } catch (Exception e) {
            log.error("[BÖLÜM-LİSTELE][HATA] Bölümleri getirirken hata oluştu: ", e);
            throw e;
        }
    }

    public Bolum bolumKaydet(Bolum bolum) {
        log.info("[BÖLÜM-EKLE] Yeni bölüm kaydediliyor: {}", bolum.getBolumAdi());

        try {
            Bolum kaydedilenBolum = bolumRepository.save(bolum);
            log.info("[BÖLÜM-EKLE] Bölüm başarıyla kaydedildi - ID: {}, Ad: {}",
                    kaydedilenBolum.getId(), kaydedilenBolum.getBolumAdi());
            return kaydedilenBolum;
        } catch (Exception e) {
            log.error("[BÖLÜM-EKLE][HATA] Bölüm kaydedilirken hata oluştu: ", e);
            throw e;
        }
    }

    public Optional<Bolum> bolumGetirById(Long id) {
        log.debug("[BÖLÜM-GETİR] Bölüm aranıyor - ID: {}", id);

        try {
            Optional<Bolum> bolum = bolumRepository.findById(id);
            if (bolum.isPresent()) {
                log.debug("[BÖLÜM-GETİR] Bölüm bulundu - ID: {}, Ad: {}", id, bolum.get().getBolumAdi());
            } else {
                log.warn("[BÖLÜM-GETİR] Bölüm bulunamadı - ID: {}", id);
            }
            return bolum;
        } catch (Exception e) {
            log.error("[BÖLÜM-GETİR][HATA] Bölüm getirilirken hata oluştu - ID: {}, Hata: ", id, e);
            throw e;
        }
    }

    public Optional<Bolum> bolumGetirByAdi(String bolumAdi) {
        log.debug("[BÖLÜM-GETİR] Bölüm aranıyor - Ad: {}", bolumAdi);

        try {
            Optional<Bolum> bolum = bolumRepository.findByBolumAdi(bolumAdi);
            if (bolum.isPresent()) {
                log.debug("[BÖLÜM-GETİR] Bölüm bulundu - ID: {}, Ad: {}", bolum.get().getId(), bolumAdi);
            } else {
                log.warn("[BÖLÜM-GETİR] Bölüm bulunamadı - Ad: {}", bolumAdi);
            }
            return bolum;
        } catch (Exception e) {
            log.error("[BÖLÜM-GETİR][HATA] Bölüm getirilirken hata oluştu - Ad: {}, Hata: ", bolumAdi, e);
            throw e;
        }
    }

    public boolean bolumAdiVarMi(String bolumAdi) {
        log.debug("[BÖLÜM-EKLE] Bölüm adı kontrol ediliyor: {}", bolumAdi);

        try {
            boolean mevcutMu = bolumRepository.existsByBolumAdi(bolumAdi);
            log.debug("[BÖLÜM-EKLE] Bölüm adı kontrolü sonucu: {} - {}", bolumAdi, mevcutMu);
            return mevcutMu;
        } catch (Exception e) {
            log.error("[BÖLÜM-EKLE][HATA] Bölüm adı kontrolü sırasında hata oluştu - Ad: {}, Hata: ", bolumAdi, e);
            throw e;
        }
    }
}