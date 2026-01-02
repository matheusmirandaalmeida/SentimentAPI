package Alura.Hackaton.SentimentAPI.service;

import Alura.Hackaton.SentimentAPI.client.SentimentClient;
import Alura.Hackaton.SentimentAPI.dto.SentimentRequestDTO;
import Alura.Hackaton.SentimentAPI.dto.SentimentResponseDTO;
import Alura.Hackaton.SentimentAPI.entity.LogSentiment;
import Alura.Hackaton.SentimentAPI.entity.LogSentimentData;
import Alura.Hackaton.SentimentAPI.exception.BusinessException;
import Alura.Hackaton.SentimentAPI.exception.ExternalServiceException;
import Alura.Hackaton.SentimentAPI.repository.LogSentimentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class SentimentServiceImpl implements SentimentService {

    private final LogSentimentRepository logRepository;
    private final SentimentClient sentimentClient;

    private static final int MAX_TEXT_LENGTH = 3000;

    @Override
    @Transactional
    public SentimentResponseDTO analyze(SentimentRequestDTO request) {

        // Define limite de tamanho para o texto para evitar gargalos do DataScience
        if (request.text().length() > MAX_TEXT_LENGTH) {
            throw new BusinessException(
                    "Texto excede o limite máximo de 3000 caracteres"
            );
        }

        // chama o DS
        var ds = sentimentClient.predict(request.text());

        if (ds == null || ds.getLabel() == null) {
            throw new ExternalServiceException("Resposta inválida do serviço de DataScience");
        }

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

