package com.example.backend.service;

import com.example.backend.entity.Isci;
import com.example.backend.repository.IsciRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Slf4j
@Service
public class IsciService {

    private final IsciRepository isciRepository;

    @Autowired
    public IsciService(IsciRepository isciRepository) {
        this.isciRepository = isciRepository;
    }

    public Isci kaydetIsci(Isci isci) {
        log.info("[İŞÇİ-EKLE] Yeni işçi kayıt edilecek: {} {}", isci.getIsim(), isci.getSoyisim());

        try {
            Isci kaydedilenIsci = isciRepository.save(isci);
            log.info("[İŞÇİ-EKLE] İşçi başarıyla kaydedildi - ID: {}, TC: {}", kaydedilenIsci.getId(), kaydedilenIsci.getTcKimlikNo());
            return kaydedilenIsci;
        } catch (Exception e) {
            log.error("[İŞÇİ-EKLE][HATA] İşçi kayıt sırasında hata: ", e);
            throw e;
        }
    }

    public List<Isci> tumIscileriGetir() {
        log.debug("[İŞÇİ-LİSTELE] Tüm işçiler listeleniyor");
        List<Isci> isciler = isciRepository.findAll();
        log.info("[İŞÇİ-LİSTELE] Toplam {} işçi bulundu", isciler.size());
        return isciler;
    }

    public void silIsciById(Long id) {
        log.info("[İŞÇİ-SİL] İşçi silinecek - ID: {}", id);

        if (!isciRepository.existsById(id)) {
            log.warn("[İŞÇİ-SİL] Silinmek istenen işçi bulunamadı - ID: {}", id);
            throw new EntityNotFoundException("ID: " + id + " olan işçi bulunamadı");
        }

        isciRepository.deleteById(id);
        log.info("[İŞÇİ-SİL] İşçi başarıyla silindi - ID: {}", id);
    }

    public boolean tcKimlikNoVarMi(String tcKimlikNo) {
        log.debug("[İŞÇİ-EKLE] TC kimlik no kontrolü yapılıyor: {}", tcKimlikNo);
        boolean exists = isciRepository.existsByTcKimlikNo(tcKimlikNo);
        log.debug("[İŞÇİ-EKLE] TC kimlik no kontrolü sonucu: {}", exists);
        return exists;
    }

    public Isci findById(Long id) {
        log.debug("[İŞÇİ-GETİR] İşçi aranıyor - ID: {}", id);
        return isciRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("[İŞÇİ-GETİR] İşçi bulunamadı - ID: {}", id);
                    return new EntityNotFoundException("ID: " + id + " olan işçi bulunamadı");
                });
    }

    public Isci guncelle(Long id, Isci guncelIsci) {
        log.info("[İŞÇİ-GÜNCELLE] İşçi güncelleniyor - ID: {}", id);

        Isci mevcutIsci = isciRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("[İŞÇİ-GÜNCELLE] Güncellenecek işçi bulunamadı - ID: {}", id);
                    return new EntityNotFoundException("ID: " + id + " olan işçi bulunamadı");
                });

        log.debug("[İŞÇİ-GÜNCELLE] Eski değerler - İsim: {}, Soyisim: {}, Bölüm: {}",
                mevcutIsci.getIsim(), mevcutIsci.getSoyisim(),
                mevcutIsci.getBolum() != null ? mevcutIsci.getBolum().getBolumAdi() : "null");

        mevcutIsci.setIsim(guncelIsci.getIsim());
        mevcutIsci.setSoyisim(guncelIsci.getSoyisim());
        mevcutIsci.setBolum(guncelIsci.getBolum());
        mevcutIsci.setDogumTarihi(guncelIsci.getDogumTarihi());

        log.debug("[İŞÇİ-GÜNCELLE] Yeni değerler - İsim: {}, Soyisim: {}, Bölüm: {}",
                mevcutIsci.getIsim(), mevcutIsci.getSoyisim(),
                mevcutIsci.getBolum() != null ? mevcutIsci.getBolum().getBolumAdi() : "null");

        Isci guncellenenIsci = isciRepository.save(mevcutIsci);
        log.info("[İŞÇİ-GÜNCELLE] İşçi başarıyla güncellendi - ID: {}", id);

        return guncellenenIsci;
    }
}