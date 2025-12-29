package Alura.Hackaton.SentimentAPI.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SentimentRequest {
    @NotBlank(message = "Campo 'text' é obrigatório.")
    @Size(min = 3, message = "Campo 'text' deve ter pelo menos 3 caracteres.")
    @Pattern(
            regexp = "^[A-Za-z0-9].*$",
            message = "Campo 'text' não pode começar com caracteres especiais."
    )

    private String text;
}