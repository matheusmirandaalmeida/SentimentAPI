package Alura.Hackaton.SentimentAPI.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {

    @GetMapping("/test")
    public String test() {
        return "API está funcionando!";
    }

    @GetMapping("/test/files")
    public String testFiles() {
        return "Arquivos estáticos devem estar funcionando";
    }
}