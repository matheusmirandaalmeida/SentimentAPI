package Alura.Hackaton.SentimentAPI.repository;

import Alura.Hackaton.SentimentAPI.entity.LogSentiment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LogSentimentRepository
        extends JpaRepository<LogSentiment, Long> {
}
