package Alura.Hackaton.SentimentAPI.test;

import Alura.Hackaton.SentimentAPI.dto.DsPredictResponseWrapper;

public class TestWrapper {
    public static void main(String[] args) {
        DsPredictResponseWrapper wrapper = new DsPredictResponseWrapper();
        wrapper.setLabel("Positive");
        wrapper.setScore(0.85);
        wrapper.setLabelId(1);
        wrapper.setTranslated("high salary");

        System.out.println("Previsao: " + wrapper.getPrevisao());
        System.out.println("Probabilidade: " + wrapper.getProbabilidade());

        // Teste com diferentes labels
        wrapper.setLabel("Negative");
        System.out.println("\nLabel Negative -> Previsao: " + wrapper.getPrevisao());

        wrapper.setLabel("Neutral");
        System.out.println("Label Neutral -> Previsao: " + wrapper.getPrevisao());

        wrapper.setLabel(null);
        System.out.println("Label null -> Previsao: " + wrapper.getPrevisao());
    }
}