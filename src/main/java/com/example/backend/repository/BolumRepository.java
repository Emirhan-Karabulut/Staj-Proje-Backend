package com.example.backend.repository;

import com.example.backend.entity.Bolum;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BolumRepository extends JpaRepository<Bolum, Long> {
    Optional<Bolum> findByBolumAdi(String bolumAdi);
    boolean existsByBolumAdi(String bolumAdi);
}