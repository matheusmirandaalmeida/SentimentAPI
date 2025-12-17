package Alura.Hackaton.SentimentAPI.service;

import Alura.Hackaton.SentimentAPI.dto.SentimentRequestDTO;
import Alura.Hackaton.SentimentAPI.dto.SentimentResponseDTO;
import org.springframework.transaction.annotation.Transactional;

public interface SentimentService {

    @Transactional
    SentimentResponseDTO analyze(SentimentRequestDTO request);

}
