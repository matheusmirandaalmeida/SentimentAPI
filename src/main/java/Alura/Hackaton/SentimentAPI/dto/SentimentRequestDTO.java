package Alura.Hackaton.SentimentAPI.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record SentimentRequestDTO(
        @NotBlank(message = "Campo 'text' é obrigatório e não deve estar vazio.")
        @Size(min = 3, message = "Campo 'text' deve ter pelo menos 3 caracteres.")
        @Pattern(
                regexp = "^[A-Za-z0-9].*$",
                message = "Campo 'text' não pode começar com caracteres especiais."
        )
        String text){

}
