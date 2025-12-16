package Alura.Hackaton.SentimentAPI.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@Table(name = "log_sentiment")
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LogSentiment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 4000)
    private String texto;

    @Column(nullable = false, length = 30)
    private String previcao;

    // Probabilidade associada a previsao fica entre 0-1
    @Column(nullable = false)
    private Double probabilidade;

    @Column(nullable = false, updatable = false)
    private Instant criadoEm;

    @Column(length = 50)
    private String origem;

    public void prePersist() {
        if(criadoEm == null) {
            criadoEm = Instant.now();
        }
    }
}
