package com.example.backend.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
public class CsrfController {

    @GetMapping("/api/csrf")
    public void getCsrfToken() {
        log.debug("[GÜVENLİK-CSRF] CSRF token isteği alındı, sadece cookie set edilecek. Veri Dönmez!");
        // Sadece CSRF cookie set edilir, veri dönmez.
    }
}