package com.example.backend.repository;

import com.example.backend.entity.Isci;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IsciRepository extends JpaRepository<Isci, Long> {
    boolean existsByTcKimlikNo(String tcKimlikNo);
    Long countByBolumId(Long bolumId);
}