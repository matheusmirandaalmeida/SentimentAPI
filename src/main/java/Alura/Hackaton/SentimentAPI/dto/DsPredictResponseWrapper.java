package Alura.Hackaton.SentimentAPI.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class DsPredictResponseWrapper {

    @JsonProperty("label")
    private String label;

    @JsonProperty("score")
    private Double score;

    @JsonProperty("label_id")
    private Integer labelId;

    @JsonProperty("translated")
    private String translated;

    // Construtor padrão
    public DsPredictResponseWrapper() {}

    // Getters e Setters
    public String getLabel() { return label; }
    public void setLabel(String label) { this.label = label; }

    public Double getScore() { return score; }
    public void setScore(Double score) { this.score = score; }

    public Integer getLabelId() { return labelId; }
    public void setLabelId(Integer labelId) { this.labelId = labelId; }

    public String getTranslated() { return translated; }
    public void setTranslated(String translated) { this.translated = translated; }

    // Método auxiliar para mapear label
    public String getPrevisao() {
        if (label == null) return "NEUTRO";
        return switch (label.toUpperCase()) {
            case "POSITIVE" -> "POSITIVO";
            case "NEGATIVE" -> "NEGATIVO";
            case "NEUTRAL" -> "NEUTRO";
            default -> "NEUTRO";
        };
    }

    // Método auxiliar para obter probabilidade
    public Double getProbabilidade() {
        return score != null ? score : 0.0;
    }
}