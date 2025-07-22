package com.example.backend.controller;

import com.example.backend.entity.Isci;
import com.example.backend.service.IsciService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/isciler")
public class IsciController {

    private final IsciService isciService;

    @Autowired
    public IsciController(IsciService isciService) {
        this.isciService = isciService;
    }

    @PostMapping("/ekle")
    public ResponseEntity<?> ekleIsci(@Valid @RequestBody Isci isci) {
        log.info("[İŞÇİ-EKLE] Yeni işçi ekleme isteği alındı: {} {}", isci.getIsim(), isci.getSoyisim());

        try {
            if (isciService.tcKimlikNoVarMi(isci.getTcKimlikNo())) {
                log.warn("[İŞÇİ-EKLE] TC kimlik numarası zaten kayıtlı: {}", isci.getTcKimlikNo());
                return ResponseEntity.badRequest().body("Bu TC kimlik numarası zaten kayıtlı");
            }

            Isci kaydedilenIsci = isciService.kaydetIsci(isci);
            log.info("[İŞÇİ-EKLE] İşçi başarıyla kaydedildi - ID: {}, TC: {}",
                    kaydedilenIsci.getId(), kaydedilenIsci.getTcKimlikNo());

            return ResponseEntity.ok(kaydedilenIsci);

        } catch (Exception e) {
            log.error("[İŞÇİ-EKLE][HATA] İşçi eklerken hata oluştu: ", e);
            throw e;
        }
    }

    @GetMapping("/hepsi")
    public List<Isci> listeleTumIsciler() {
        log.info("[İŞÇİ-LİSTELE] Tüm işçiler listeleme isteği alındı");

        try {
            List<Isci> isciler = isciService.tumIscileriGetir();
            log.info("[İŞÇİ-LİSTELE] Toplam {} işçi listelendi", isciler.size());
            return isciler;
        } catch (Exception e) {
            log.error("[İŞÇİ-LİSTELE][HATA] İşçileri listelerken hata oluştu: ", e);
            throw e;
        }
    }

    @DeleteMapping("/sil/{id}")
    public void silIsci(@PathVariable Long id) {
        log.info("[İŞÇİ-SİL] İşçi silme isteği alındı - ID: {}", id);

        try {
            isciService.silIsciById(id);
            log.info("[İŞÇİ-SİL] İşçi başarıyla silindi - ID: {}", id);
        } catch (Exception e) {
            log.error("[İŞÇİ-SİL][HATA] İşçi silinirken hata oluştu - ID: {}, Hata: ", id, e);
            throw e;
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Isci> guncelle(@PathVariable Long id, @Valid @RequestBody Isci isci) {
        log.info("[İŞÇİ-GÜNCELLE] İşçi güncelleme isteği alındı - ID: {}", id);

        try {
            Isci guncellenenIsci = isciService.guncelle(id, isci);
            log.info("[İŞÇİ-GÜNCELLE] İşçi başarıyla güncellendi - ID: {}, Yeni ad: {} {}",
                    id, guncellenenIsci.getIsim(), guncellenenIsci.getSoyisim());

            return ResponseEntity.ok(guncellenenIsci);

        } catch (Exception e) {
            log.error("[İŞÇİ-GÜNCELLE][HATA] İşçi güncellenirken hata oluştu - ID: {}, Hata: ", id, e);
            throw e;
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Isci> getById(@PathVariable Long id) {
        log.debug("[İŞÇİ-GETİR] İşçi getirme isteği alındı - ID: {}", id);

        try {
            Isci isci = isciService.findById(id);
            log.debug("[İŞÇİ-GETİR] İşçi bulundu - ID: {}, Ad: {} {}",
                    id, isci.getIsim(), isci.getSoyisim());

            return ResponseEntity.ok(isci);

        } catch (Exception e) {
            log.error("[İŞÇİ-GETİR][HATA] İşçi getirilirken hata oluştu - ID: {}, Hata: ", id, e);
            throw e;
        }
    }
}