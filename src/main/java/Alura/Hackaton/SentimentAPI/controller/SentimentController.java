package Alura.Hackaton.SentimentAPI.controller;

import Alura.Hackaton.SentimentAPI.dto.*;
import Alura.Hackaton.SentimentAPI.service.SentimentService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/v1/sentiment")
public class SentimentController {

    private final SentimentService sentimentService;

    public SentimentController(SentimentService sentimentService) {
        this.sentimentService = sentimentService;
    }

    @PostMapping
    public ResponseEntity<SentimentResponseDTO> analyze(
            @Valid @RequestBody SentimentRequestDTO request) {

        return ResponseEntity.ok(sentimentService.analyze(request));
    }

    @GetMapping("/dashboard")
    public SentimentDashboardResponse getDashboard() {
        return new SentimentDashboardResponse (
                65,
                25,
                10,
                List.of(
                        "Atendimento excelente",
                        "Entrega rápida",
                        "Produto muito bom"
                )
        );
    }
}

