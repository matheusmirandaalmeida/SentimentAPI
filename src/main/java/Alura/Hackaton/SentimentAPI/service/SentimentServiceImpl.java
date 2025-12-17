package Alura.Hackaton.SentimentAPI.service;

import Alura.Hackaton.SentimentAPI.client.SentimentClient;
import Alura.Hackaton.SentimentAPI.dto.SentimentRequestDTO;
import Alura.Hackaton.SentimentAPI.dto.SentimentResponseDTO;
import Alura.Hackaton.SentimentAPI.entity.LogSentiment;
import Alura.Hackaton.SentimentAPI.repository.LogSentimentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SentimentServiceImpl implements SentimentService {

    private final SentimentClient sentimentClient;
    private final LogSentimentRepository logRepository;

    public SentimentServiceImpl(SentimentClient sentimentClient, LogSentimentRepository logRepository) {
        this.sentimentClient = sentimentClient;
        this.logRepository = logRepository;
    }

    @Override
    @Transactional
    public SentimentResponseDTO analyze(SentimentRequestDTO request) {
        SentimentResponseDTO result = sentimentClient.predict(request.getText());

        if (result == null || result.getPrevisao() == null) {
            throw new RuntimeException("Resposta inválida do serviço de Data Science");
        }

        LogSentiment log = new LogSentiment();
        log.setTexto(request.getText());
        log.setPrevisao(result.getPrevisao());
        log.setProbabilidade(result.getProbabilidade());
        log.setOrigem("API"); // opcional

        logRepository.save(log);

        return result;
    }
}
