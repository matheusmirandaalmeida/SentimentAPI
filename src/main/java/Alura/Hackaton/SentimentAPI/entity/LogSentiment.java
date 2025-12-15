package Alura.Hackaton.SentimentAPI.entity;

import jakarta.persistence.*;
import lombok.*;

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


}
