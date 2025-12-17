package Alura.Hackaton.SentimentAPI.controller;

import Alura.Hackaton.SentimentAPI.dto.SentimentRequestDTO;
import Alura.Hackaton.SentimentAPI.dto.SentimentResponseDTO;
import Alura.Hackaton.SentimentAPI.service.SentimentService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/sentiment")
public class SentimentController {

    private SentimentService sentimentService;

    public SentimentController(SentimentService sentimentService) {
        this.sentimentService = sentimentService;
    }

    @PostMapping
    public ResponseEntity<SentimentResponseDTO> analyze(@Valid @RequestBody SentimentRequestDTO request) {
        return ResponseEntity.ok(sentimentService.analyze(request));
    }
}
