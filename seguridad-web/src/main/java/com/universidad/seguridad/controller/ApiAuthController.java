package com.universidad.seguridad.controller;

import com.universidad.seguridad.security.JwtUtil;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class ApiAuthController {

    private final AuthenticationManager authManager;
    private final JwtUtil jwtUtil;

    public ApiAuthController(AuthenticationManager authManager, JwtUtil jwtUtil) {
        this.authManager = authManager;
        this.jwtUtil = jwtUtil;
    }

    public record LoginRequest(String email, String contrasenia) {}
    public record LoginResponse(String token, String email, String rol) {}

    @PostMapping("/login")
    public LoginResponse login(@RequestBody LoginRequest req) {
        Authentication auth = authManager.authenticate(
            new UsernamePasswordAuthenticationToken(req.email(), req.contrasenia())
        );
        String rol = auth.getAuthorities().iterator().next().getAuthority();
        String token = jwtUtil.generarToken(req.email(), rol);
        return new LoginResponse(token, req.email(), rol);
    }
}