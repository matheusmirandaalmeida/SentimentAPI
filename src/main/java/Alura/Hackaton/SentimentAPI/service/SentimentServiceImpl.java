package Alura.Hackaton.SentimentAPI.service;

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

    @Override
    @Transactional
    public SentimentResponseDTO analyze(SentimentRequestDTO request) {

        // 🔹 MOCK TEMPORÁRIO (SEM PYTHON)

        // Variaveis com valores temporarios do MOCK (mais prático para edição futura)
        //--------------------
        String previsao = "POSITIVO";
        double probabilidade = 0.94;
        //--------------------

        SentimentResponseDTO response = new SentimentResponseDTO(previsao, probabilidade);

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

