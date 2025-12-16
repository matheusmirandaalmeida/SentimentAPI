package Alura.Hackaton.SentimentAPI.dto;

import java.time.Instant;

public class ErrorResponseDTO {
    private String mensagem;
    private String detalhe;
    private Instant timestamp;

    public ErrorResponseDTO() {}

    public ErrorResponseDTO(String mensagem, String detalhe, Instant timestamp) {
        this.mensagem = mensagem;
        this.detalhe = detalhe;
        this.timestamp = timestamp;
    }

    public String getMensagem() { return mensagem; }
    public void setMensagem(String mensagem) { this.mensagem = mensagem; }

    public String getDetalhe() { return detalhe; }
    public void setDetalhe(String detalhe) { this.detalhe = detalhe; }

    public Instant getTimestamp() { return timestamp; }
    public void setTimestamp(Instant timestamp) { this.timestamp = timestamp; }
}
