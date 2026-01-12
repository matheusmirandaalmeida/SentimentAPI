package Alura.Hackaton.SentimentAPI.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class WebController {

    @GetMapping("/")
    public String landing() {
        return "html/landing.html";
    }

    @GetMapping("/landing")
    public String landingPage() {
        return "redirect:/";
    }

    @GetMapping("/dashboard")
    public String dashboard() {
        return "html/dashboard.html";
    }

    @GetMapping("/avaliacoes")
    public String avaliacoes() {
        return "html/avaliacoes.html";
    }

    @GetMapping("/login")
    public String login() {
        return "html/login.html";
    }

    @GetMapping("/cadastro")
    public String cadastro() {
        return "html/cadastro.html";
    }

    @GetMapping("/comentarios")
    public String comentarios() {
        return "html/comentarios.html";
    }
}