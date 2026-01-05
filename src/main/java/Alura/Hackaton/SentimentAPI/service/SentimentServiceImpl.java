package Alura.Hackaton.SentimentAPI.service;

import Alura.Hackaton.SentimentAPI.client.SentimentClient;
import Alura.Hackaton.SentimentAPI.dto.SentimentRequestDTO;
import Alura.Hackaton.SentimentAPI.dto.SentimentResponseDTO;
import Alura.Hackaton.SentimentAPI.entity.LogSentiment;
import Alura.Hackaton.SentimentAPI.repository.LogSentimentRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.ResourceAccessException;

@Service
public class SentimentServiceImpl implements SentimentService {

    private static final Logger log = LoggerFactory.getLogger(SentimentServiceImpl.class);

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
<<<<<<< Updated upstream
        //Preciso alterar essa parte para receber os valores do dataSciance
        String text = request.getText() == null ? "" : request.getText().trim();
        if (text.isBlank()) {
            SentimentResponseDTO resp = new SentimentResponseDTO();
            resp.setPrevisao("PENDENTE");
            resp.setProbabilidade(0.0);
            return resp;
        }

        try {
            var ds = sentimentClient.predict(text);

            // "Positive/Negative" -> "POSITIVO/NEGATIVO"
            String previsao = "NEGATIVO";
            if (ds.getLabel() != null && ds.getLabel().equalsIgnoreCase("Positive")) {
                previsao = "POSITIVO";
            }
=======

        var ds = sentimentClient.predict(request.getText());

        String previsao;
        if (ds.getLabelId() != null) {
            previsao = (ds.getLabelId() == 1) ? "POSITIVO" : "NEGATIVO";
        } else {
            previsao = "Positive".equalsIgnoreCase(ds.getLabel()) ? "POSITIVO" : "NEGATIVO";
        }

        SentimentResponseDTO response = new SentimentResponseDTO();
        response.setPrevisao(previsao);
        response.setProbabilidade(ds.getScore() != null ? ds.getScore() : 0.0);

        LogSentiment log = new LogSentiment();
        log.setTexto(request.getText());
        log.setPrevisao(response.getPrevisao());
        log.setProbabilidade(response.getProbabilidade());
        log.setOrigem("API");
        logRepository.save(log);
>>>>>>> Stashed changes

            SentimentResponseDTO response = new SentimentResponseDTO();
            response.setPrevisao(previsao);
            response.setProbabilidade(ds.getScore() != null ? ds.getScore() : 0.0);

            LogSentiment logEntity = new LogSentiment();
            logEntity.setTexto(text);
            logEntity.setPrevisao(response.getPrevisao());
            logEntity.setProbabilidade(response.getProbabilidade());
            logEntity.setOrigem("API");
            logRepository.save(logEntity);

            return response;

        } catch (HttpStatusCodeException ex) {
            log.error("DS retornou {}: {}", ex.getStatusCode(), ex.getResponseBodyAsString());

            SentimentResponseDTO resp = new SentimentResponseDTO();
            resp.setPrevisao("PENDENTE");
            resp.setProbabilidade(0.0);
            return resp;

        } catch (ResourceAccessException ex) {
            log.error("DS indisponível: {}", ex.getMessage());

            SentimentResponseDTO resp = new SentimentResponseDTO();
            resp.setPrevisao("PENDENTE");
            resp.setProbabilidade(0.0);
            return resp;
        }
    }
}
