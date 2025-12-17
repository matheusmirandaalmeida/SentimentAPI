package Alura.Hackaton.SentimentAPI.repository;

import Alura.Hackaton.SentimentAPI.entity.LogSentiment;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface LogSentimentRepository extends CrudRepository<LogSentiment, Long> {

    List<LogSentiment> findByPrevisao(String previsao);
}
