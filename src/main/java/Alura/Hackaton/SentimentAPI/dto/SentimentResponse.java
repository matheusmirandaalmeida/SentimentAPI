package Alura.Hackaton.SentimentAPI.dto;

public class SentimentResponse {

    private String previsao;
    private double probabilidade;

    public SentimentResponse() {}

    public SentimentResponse(String previsao, double probabilidade) {
        this.previsao = previsao;
        this.probabilidade = probabilidade;
    }

    public String getPrevisao() {
        return previsao;
    }
    public double getProbabilidade() {
        return probabilidade;
    }
    public void setPrevisao(String previsao) {
        this.previsao = previsao;
    }
    public void setProbabilidade(double probabilidade) {
        this.probabilidade = probabilidade;
    }
}