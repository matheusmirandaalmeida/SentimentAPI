package Alura.Hackaton.SentimentAPI.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class WebController {

    @GetMapping("/dashboard")
    public String dashboard() {
        return "redirect:/dashboard.html"; // ou retorna a página diretamente
    }

    // Ou melhor ainda, para todas as páginas:
    @GetMapping("/{page}")
    public String servePage(@PathVariable String page) {
        // Remove a extensão .html se presente
        if (page.endsWith(".html")) {
            page = page.substring(0, page.length() - 5);
        }
        return page + ".html";
    }
}