package Alura.Hackaton.SentimentAPI.dto;

import java.util.List;

public record SentimentDashboardResponse(
        int positive,
        int neutral,
        int negative,
        List<String> comments) {
}
