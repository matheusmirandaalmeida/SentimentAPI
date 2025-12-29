package Alura.Hackaton.SentimentAPI.service;

import Alura.Hackaton.SentimentAPI.dto.SentimentRequestDTO;
import Alura.Hackaton.SentimentAPI.dto.SentimentResponseDTO;

public interface SentimentService {
    SentimentResponseDTO analyze(SentimentRequestDTO request);
}

