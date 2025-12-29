package Alura.Hackaton.SentimentAPI.entity;

import Alura.Hackaton.SentimentAPI.dto.SentimentRequestDTO;
import Alura.Hackaton.SentimentAPI.dto.SentimentResponseDTO;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
public class LogSentiment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 2000)
    private String texto;

    private String previsao;
    private double probabilidade;
    private String origem;

    public LogSentiment(LogSentimentData data) {
        this.texto = data.texto();
        this.previsao = data.previsao();
        this.probabilidade = data.probabilidade();
        this.origem = data.origem();
    }
}
