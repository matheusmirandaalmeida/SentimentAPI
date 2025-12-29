package Alura.Hackaton.SentimentAPI.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SentimentResponse {
    private String previsao;
    private double probabilidade;
}