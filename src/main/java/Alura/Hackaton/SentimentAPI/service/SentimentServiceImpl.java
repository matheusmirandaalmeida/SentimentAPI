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

import java.util.Locale;

@RequiredArgsConstructor
@Service
public class SentimentServiceImpl implements SentimentService {

    private final LogSentimentRepository logRepository;
    private final SentimentClient sentimentClient;

    private static final int MAX_TEXT_LENGTH = 3000;

    @Override
    @Transactional
    public SentimentResponseDTO analyze(SentimentRequestDTO request) {

        if (request == null || request.text() == null || request.text().isBlank()) {
            throw new BusinessException("Texto não pode estar vazio");
        }

        if (request.text().length() > MAX_TEXT_LENGTH) {
            throw new BusinessException("Texto excede o limite máximo de 3000 caracteres");
        }

        // chama o DS
        var ds = sentimentClient.predict(request.text());

        if (ds == null || ds.getLabel() == null) {
            throw new ExternalServiceException("Resposta inválida do serviço de DataScience");
        }

        String labelNorm = ds.getLabel().trim().toUpperCase(Locale.ROOT);

        // Mapeia 3 classes (com fallback seguro)
        String previsao = switch (labelNorm) {
            case "POSITIVE", "POSITIVO" -> "POSITIVO";
            case "NEGATIVE", "NEGATIVO" -> "NEGATIVO";
            case "NEUTRAL", "NEUTRO"   -> "NEUTRO";
            default -> "NEUTRO"; // fallback pra nunca voltar PENDENTE por erro de label
        };

        double score = (ds.getScore() != null && !ds.getScore().isNaN())
                ? ds.getScore()
                : 0.0;

        // monta resposta no DTO
        SentimentResponseDTO response = new SentimentResponseDTO(previsao, score);

        LogSentimentData data = new LogSentimentData(
                request.text(),
                response.previsao(),
                response.probabilidade(),
                "API"
        );

        logRepository.save(new LogSentiment(data));

        return response;
    }
}
