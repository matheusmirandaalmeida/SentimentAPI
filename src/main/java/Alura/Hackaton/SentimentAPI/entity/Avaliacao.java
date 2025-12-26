package Alura.Hackaton.SentimentAPI.entity;

import Alura.Hackaton.SentimentAPI.Sentimento;
import jakarta.persistence.*;
import lombok.*;
import java.time.Instant;

@Entity
@Table(name = "avaliacoes")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class Avaliacao {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(nullable = false)
    private String empresa;

    @Column(nullable = false)
    private String vinculo;

    @Column(nullable = false)
    private String situacao;

    private String cargo;

    @Column(nullable = false)
    private String titulo;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String texto;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Sentimento sentimento;

    @Column(nullable = false)
    private Instant createdAt;
}
