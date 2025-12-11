package Alura.Hackaton.SentimentAPI.controller;

import Alura.Hackaton.SentimentAPI.dto.SentimentRequest;
import Alura.Hackaton.SentimentAPI.dto.SentimentResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/sentiment")
public class SentimentController {

    // Endpoint principal: previsão de sentimento
    @PostMapping
    public ResponseEntity<SentimentResponse> analyze(@Valid @RequestBody SentimentRequest request) {
        String text = request.getText();

        // Simulação de resposta do modelo
        SentimentResponse response = new SentimentResponse("Positivo", 0.9);

        // Retornar 201 Created
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // Endpoint opcional: estatísticas (retorna 200 OK)
    @PostMapping("/stats")
    public ResponseEntity<String> stats() {
        // Simulação de estatísticas
        String stats = "{ \"positivos\": 60, \"negativos\": 30, \"neutros\": 10 }";
        return ResponseEntity.status(HttpStatus.OK).body(stats);
    }
}