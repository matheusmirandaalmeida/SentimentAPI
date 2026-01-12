package Alura.Hackaton.SentimentAPI.controller;

import Alura.Hackaton.SentimentAPI.dto.*;
import Alura.Hackaton.SentimentAPI.service.SentimentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/sentiment")
public class SentimentController {
    private final SentimentService sentimentService;

    @PostMapping
    public ResponseEntity<SentimentResponseDTO> analyze(
            @Valid @RequestBody SentimentRequestDTO request) {

        return ResponseEntity.ok(sentimentService.analyze(request));
    }
}

