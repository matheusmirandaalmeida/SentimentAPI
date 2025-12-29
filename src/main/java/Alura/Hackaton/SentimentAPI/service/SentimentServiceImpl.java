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

    private final LogSentimentRepository logRepository;
    private final SentimentClient sentimentClient;

    public SentimentServiceImpl(LogSentimentRepository logRepository,
                                SentimentClient sentimentClient) {
        this.logRepository = logRepository;
        this.sentimentClient = sentimentClient;
    }

    @Override
    @Transactional
    public SentimentResponseDTO analyze(SentimentRequestDTO request) {

        // chama o DS
        var ds = sentimentClient.predict(request.getText());

        // monta resposta no DTO
        SentimentResponseDTO response = new SentimentResponseDTO();

        // mapeia "Positive/Negative" -> "POSITIVO/NEGATIVO"
        String previsao = "NEGATIVO";
        if (ds != null && ds.getLabel() != null && ds.getLabel().equalsIgnoreCase("Positive")) {
            previsao = "POSITIVO";
        }

        response.setPrevisao(previsao);
        response.setProbabilidade(ds != null && ds.getScore() != null ? ds.getScore() : 0.0);
        LogSentiment log = new LogSentiment();
        log.setTexto(request.getText());
        log.setPrevisao(response.getPrevisao());
        log.setProbabilidade(response.getProbabilidade());
        log.setOrigem("API");

        logRepository.save(log);

        return response;
    }
}
