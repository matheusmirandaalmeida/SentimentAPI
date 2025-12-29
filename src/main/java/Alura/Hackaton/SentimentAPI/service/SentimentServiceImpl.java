package Alura.Hackaton.SentimentAPI.service;

import Alura.Hackaton.SentimentAPI.client.SentimentClient;
import Alura.Hackaton.SentimentAPI.dto.SentimentRequestDTO;
import Alura.Hackaton.SentimentAPI.dto.SentimentResponseDTO;
import Alura.Hackaton.SentimentAPI.entity.LogSentiment;
import Alura.Hackaton.SentimentAPI.entity.LogSentimentData;
import Alura.Hackaton.SentimentAPI.repository.LogSentimentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class SentimentServiceImpl implements SentimentService {

    private final LogSentimentRepository logRepository;
    private final SentimentClient sentimentClient;

    @Override
    @Transactional
    public SentimentResponseDTO analyze(SentimentRequestDTO request) {

        // chama o DS
        var ds = sentimentClient.predict(request.text());

        // mapeia "Positive/Negative" -> "POSITIVO/NEGATIVO"
        String previsao = "NEGATIVO";
        if (ds != null && ds.getLabel() != null && ds.getLabel().equalsIgnoreCase("Positive")) {
            previsao = "POSITIVO";
        }

        // monta resposta no DTO
        SentimentResponseDTO response = new SentimentResponseDTO(
                previsao,
                ds != null && ds.getScore() != null ? ds.getScore() : 0.0
        );

        LogSentimentData data = new LogSentimentData(
                request.text(),
                response.previsao(),
                response.probabilidade(),
                "API"
        );

        LogSentiment log = new LogSentiment(data);

        logRepository.save(log);

        return response;
    }
}

