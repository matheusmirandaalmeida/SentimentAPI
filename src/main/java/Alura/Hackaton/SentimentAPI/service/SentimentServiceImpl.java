package Alura.Hackaton.SentimentAPI.service;

import Alura.Hackaton.SentimentAPI.dto.SentimentRequestDTO;
import Alura.Hackaton.SentimentAPI.dto.SentimentResponseDTO;
import Alura.Hackaton.SentimentAPI.entity.LogSentiment;
import Alura.Hackaton.SentimentAPI.repository.LogSentimentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SentimentServiceImpl implements SentimentService {

    private final LogSentimentRepository logRepository;

    public SentimentServiceImpl(LogSentimentRepository logRepository) {
        this.logRepository = logRepository;
    }

    @Override
    @Transactional
    public SentimentResponseDTO analyze(SentimentRequestDTO request) {

        // 🔹 MOCK TEMPORÁRIO (SEM PYTHON)
        SentimentResponseDTO response = new SentimentResponseDTO();
        response.setPrevisao("POSITIVO");
        response.setProbabilidade(0.94);

        LogSentiment log = new LogSentiment();
        log.setTexto(request.getText());
        log.setPrevisao(response.getPrevisao());
        log.setProbabilidade(response.getProbabilidade());
        log.setOrigem("API");

        logRepository.save(log);

        return response;
    }
}

