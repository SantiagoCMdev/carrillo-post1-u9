package com.universidad.seguridad;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootTest
class GenerarHash {
    @Autowired
    PasswordEncoder encoder;

    @Test
    void generarHashAdmin() {
        System.out.println("=== HASH BCRYPT PARA ADMIN123 ===");
        System.out.println(encoder.encode("admin123"));
        System.out.println("=================================");
    }
}