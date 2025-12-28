package Alura.Hackaton.SentimentAPI.dto;

import java.util.List;

public class SentimentDashboardResponse {

    private int positive;
    private int neutral;
    private int negative;
    private List<String> comments;

    public SentimentDashboardResponse() {
    }

    public SentimentDashboardResponse(int positive, int neutral, int negative, List<String> comments) {
        this.positive = positive;
        this.neutral = neutral;
        this.negative = negative;
        this.comments = comments;
    }

    public int getPositive() {
        return positive;
    }

    public void setPositive(int positive) {
        this.positive = positive;
    }

    public int getNeutral() {
        return neutral;
    }

    public void setNeutral(int neutral) {
        this.neutral = neutral;
    }

    public int getNegative() {
        return negative;
    }

    public void setNegative(int negative) {
        this.negative = negative;
    }

    public List<String> getComments() {
        return comments;
    }

    public void setComments(List<String> comments) {
        this.comments = comments;
    }
}
