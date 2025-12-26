package Alura.Hackaton.SentimentAPI.dto;

import Alura.Hackaton.SentimentAPI.Sentimento;

import java.time.Instant;

public record AvaliacaoResponse(
        String id,
        String empresa,
        String vinculo,
        String situacao,
        String cargo,
        String titulo,
        String texto,
        Sentimento sentimento,
        Instant createdAt
) {
}
