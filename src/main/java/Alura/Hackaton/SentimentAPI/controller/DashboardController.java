package Alura.Hackaton.SentimentAPI.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {

    @GetMapping
    public ResponseEntity<Map<String, Object>> getDashboardData() {
        Map<String, Object> data = Map.of(
                "positivo", 65,
                "neutro", 20,
                "negativo", 15,
                "total", 100,
                "updatedAt", LocalDateTime.now().toString(),
                "comentarios", new String[]{
                        "Excelente serviço, recomendo!",
                        "Poderia ser melhor",
                        "Muito satisfeito",
                        "Não gostei do atendimento",
                        "Produto de boa qualidade"
                }
        );
        return ResponseEntity.ok(data);
    }
}
