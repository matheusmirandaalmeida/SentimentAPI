package Alura.Hackaton.SentimentAPI.dto;

import jakarta.validation.constraints.NotBlank;

public record AvaliacaoCreateRequest(
        @NotBlank String empresa,
        @NotBlank String vinculo,
        @NotBlank String situacao,
        String cargo,
        @NotBlank String titulo,
        @NotBlank String texto
) {
}
