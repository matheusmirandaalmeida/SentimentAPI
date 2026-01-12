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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class SentimentServiceImpl implements SentimentService {

    private static final Logger log = LoggerFactory.getLogger(SentimentServiceImpl.class);

    private final LogSentimentRepository logRepository;
    private final SentimentClient sentimentClient;

    private static final int MAX_TEXT_LENGTH = 3000;

    @Override
    @Transactional
    public SentimentResponseDTO analyze(SentimentRequestDTO request) {
        // Validação
        if (request == null || request.text() == null || request.text().isBlank()) {
            throw new BusinessException("Texto não pode estar vazio");
        }

        if (request.text().length() > MAX_TEXT_LENGTH) {
            throw new BusinessException("Texto excede o limite máximo de 3000 caracteres");
        }

        log.info("Analisando sentimento para texto de {} caracteres", request.text().length());

        try {
            // Chamar serviço DS
            var dsResponse = sentimentClient.predict(request.text());

            if (dsResponse == null) {
                throw new ExternalServiceException("Resposta inválida do serviço de DataScience");
            }

            String previsao = dsResponse.getPrevisao();
            double probabilidade = dsResponse.getScore() != null ? dsResponse.getScore() : 0.0;

            log.info("Análise concluída: {} com {:.2f}% de confiança",
                    previsao, probabilidade * 100);

            SentimentResponseDTO response = new SentimentResponseDTO(
                    previsao,
                    probabilidade,
                    dsResponse.getLabelId(),
                    dsResponse.getTranslated()
            );

            // Log no banco
            LogSentimentData data = new LogSentimentData(
                    request.text(),
                    response.previsao(),
                    response.probabilidade(),
                    "API"
            );

            logRepository.save(new LogSentiment(data));

            return response;

        } catch (ExternalServiceException ex) {
            log.error("Erro no serviço externo de análise: {}", ex.getMessage());
            throw ex;
        } catch (Exception ex) {
            log.error("Erro inesperado durante análise de sentimento", ex);
            throw new BusinessException("Erro interno ao processar análise de sentimento");
        }
    }
}