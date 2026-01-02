package Alura.Hackaton.SentimentAPI.dto;

import org.springframework.validation.FieldError;

public record ValidationErrorDTO(
        String campo,
        String mensagem
) {
    public ValidationErrorDTO(FieldError error) {
        this(error.getField(), error.getDefaultMessage());
    }
}
