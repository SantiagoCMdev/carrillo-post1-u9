package com.universidad.seguridad.controller;

import com.universidad.seguridad.model.Tarea;
import com.universidad.seguridad.model.Usuario;
import com.universidad.seguridad.service.TareaService;
import com.universidad.seguridad.service.UsuarioService;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
public class AuthController {

    private final UsuarioService service;
    private final TareaService tareaService;

    public AuthController(UsuarioService service, TareaService tareaService) {
        this.service = service;
        this.tareaService = tareaService;
    }

    @GetMapping("/")
    public String inicio() {
        return "redirect:/login";
    }

    @GetMapping("/login")
    public String mostrarLogin() {
        return "auth/login";
    }

    @GetMapping("/registro")
    public String mostrarRegistro(Model model) {
        model.addAttribute("usuario", new Usuario());
        return "auth/registro";
    }

    @PostMapping("/registro")
    public String registrar(@Valid @ModelAttribute Usuario usuario,
                            BindingResult result) {
        if (result.hasErrors()) return "auth/registro";
        try {
            service.registrar(usuario);
            return "redirect:/login?registrado";
        } catch (RuntimeException e) {
            result.rejectValue("email", "error.email", e.getMessage());
            return "auth/registro";
        }
    }

    @GetMapping("/dashboard")
    public String dashboard(@RequestParam(required = false) String buscar,
                            Model model, Authentication auth) {
        Usuario u = service.buscarPorEmail(auth.getName());
        // Pasamos el nombre del usuario para probar XSS tal como indica el Paso 8
        model.addAttribute("usuario", u.getNombre());
        model.addAttribute("email", u.getEmail());
        model.addAttribute("roles", auth.getAuthorities());

        List<Tarea> tareas;
        if (buscar != null && !buscar.isBlank()) {
            tareas = tareaService.buscar(buscar);
            model.addAttribute("terminoBusqueda", buscar);
        } else {
            tareas = tareaService.listarDe(u.getId());
        }
        model.addAttribute("tareas", tareas);
        return "dashboard";
    }

    @PostMapping("/tareas/nueva")
    public String crearTarea(@RequestParam String titulo,
                             @RequestParam(required = false) String descripcion,
                             Authentication auth) {
        Usuario u = service.buscarPorEmail(auth.getName());
        Tarea t = new Tarea();
        t.setTitulo(titulo);
        t.setDescripcion(descripcion);
        t.setPropietario(u);
        tareaService.guardar(t);
        return "redirect:/dashboard";
    }

    @GetMapping("/admin")
    public String adminPanel(Model model) {
        model.addAttribute("usuarios", service.listarTodos());
        return "admin/panel";
    }
}