package Alura.Hackaton.SentimentAPI.dto;

public class SentimentResponseDTO {

    private String previsao;
    private double probabilidade;

    public String getPrevisao() {
        return previsao;
    }

    public void setPrevisao(String previsao) {
        this.previsao = previsao;
    }

    public double getProbabilidade() {
        return probabilidade;
    }

    public void setProbabilidade(double probabilidade) {
        this.probabilidade = probabilidade;
    }
}
