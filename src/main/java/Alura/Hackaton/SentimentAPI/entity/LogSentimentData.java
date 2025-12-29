package Alura.Hackaton.SentimentAPI.entity;

public record LogSentimentData(
        String texto,
        String previsao,
        double probabilidade,
        String origem
) {
}
