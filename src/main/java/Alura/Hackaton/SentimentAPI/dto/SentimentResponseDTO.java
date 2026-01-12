package Alura.Hackaton.SentimentAPI.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record SentimentResponseDTO(
        String previsao,
        double probabilidade,

        @JsonProperty("label_id")
        Integer labelId,      // 1=POSITIVE, 0=NEGATIVE, 2=NEUTRAL

        String translated
){

    public SentimentResponseDTO(String previsao, double probabilidade) {
        this(previsao, probabilidade, null, null);
    }
}