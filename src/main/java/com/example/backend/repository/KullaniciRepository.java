package com.example.backend.repository;
import com.example.backend.entity.Kullanici;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface KullaniciRepository extends JpaRepository<Kullanici, Long> {
    Optional<Kullanici> findByEmail(String email);
    boolean existsByEmail(String email);
    Optional<Kullanici> findBySifreSifirlamaAnahtari(String anahtar);
}