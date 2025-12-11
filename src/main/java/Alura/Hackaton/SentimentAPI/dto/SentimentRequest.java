package Alura.Hackaton.SentimentAPI.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class SentimentRequest {

    @NotBlank(message = "Campo 'text' é obrigatório.")
    @Size(min = 3, message = "Campo 'text' deve ter pelo menos 3 caracteres.")
    @Pattern(
            regexp = "^[A-Za-z0-9].*$",
            message = "Campo 'text' não pode começar com caracteres especiais."
    )
    private String text;

    public SentimentRequest() {}

    public SentimentRequest(String text) {
        this.text = text;
    }

    public String getText() { return text; }

    public void setText(String text) { this.text = text; }
}