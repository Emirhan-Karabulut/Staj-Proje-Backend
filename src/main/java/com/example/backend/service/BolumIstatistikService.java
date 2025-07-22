package com.example.backend.service;

import com.example.backend.entity.Bolum;
import com.example.backend.repository.BolumRepository;
import com.example.backend.repository.IsciRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class BolumIstatistikService {

    private final IsciRepository isciRepository;
    private final BolumRepository bolumRepository;

    @Autowired
    public BolumIstatistikService(IsciRepository isciRepository, BolumRepository bolumRepository) {
        this.isciRepository = isciRepository;
        this.bolumRepository = bolumRepository;
    }

    public Map<String, Long> getBolumIscileriSayisi() {
        log.info("[İSTATİSTİK-LİSTELE] Bölüm işçi sayıları hesaplanıyor");

        Map<String, Long> bolumSayilari = new HashMap<>();
        List<Bolum> bolumler = bolumRepository.findAll();

        for (Bolum bolum : bolumler) {
            Long count = isciRepository.countByBolumId(bolum.getId());
            log.debug("[İSTATİSTİK-LİSTELE] Bölüm: {}, İşçi Sayısı: {}", bolum.getBolumAdi(), count);
            bolumSayilari.put(bolum.getBolumAdi(), count);
        }

        log.info("[İSTATİSTİK-LİSTELE] Bölüm işçi sayıları başarıyla hesaplandı");
        return bolumSayilari;
    }
}