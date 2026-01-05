package Alura.Hackaton.SentimentAPI.mapper;

import Alura.Hackaton.SentimentAPI.enun.Sentimento;
import Alura.Hackaton.SentimentAPI.client.SentimentClient;

public class SentimentoMapper {

    public static Sentimento fromDs(SentimentClient.DsPredictResponse ds) {
        if (ds == null) return Sentimento.PENDENTE;
        Integer id = ds.getLabel_id();
        if (id != null) {
            return (id == 1) ? Sentimento.POSITIVO : Sentimento.NEGATIVO;
        }

        // fallback: label string
        String label = ds.getLabel();
        if (label == null) return Sentimento.PENDENTE;

        return switch (label.trim().toLowerCase()) {
            case "positive" -> Sentimento.POSITIVO;
            case "negative" -> Sentimento.NEGATIVO;
            default -> Sentimento.NEUTRO;
        };
    }
}