package Alura.Hackaton.SentimentAPI.service;

import Alura.Hackaton.SentimentAPI.dto.SentimentRequestDTO;
import Alura.Hackaton.SentimentAPI.enun.Sentimento;
import Alura.Hackaton.SentimentAPI.dto.AvaliacaoCreateRequest;
import Alura.Hackaton.SentimentAPI.dto.AvaliacaoResponse;
import Alura.Hackaton.SentimentAPI.entity.Avaliacao;
import Alura.Hackaton.SentimentAPI.repository.AvaliacaoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AvaliacaoService {

    private final AvaliacaoRepository repo;
    private final SentimentService sentimentService;

    public AvaliacaoResponse criar(AvaliacaoCreateRequest req) {

        SentimentRequestDTO sreq = new SentimentRequestDTO(req.texto());

        var sresp = sentimentService.analyze(sreq);

        Sentimento sentimento = Sentimento.PENDENTE;
        if ("POSITIVO".equalsIgnoreCase(sresp.previsao())) {
            sentimento = Sentimento.POSITIVO;
        } else if ("NEGATIVO".equalsIgnoreCase(sresp.previsao())) {
            sentimento = Sentimento.NEGATIVO;
        }

        Avaliacao a = Avaliacao.builder()
                .empresa(req.empresa())
                .vinculo(req.vinculo())
                .situacao(req.situacao())
                .cargo(req.cargo())
                .titulo(req.titulo())
                .texto(req.texto())
                .sentimento(sentimento)
                .createdAt(Instant.now())
                .build();

        Avaliacao salvo = repo.save(a);
        return toResponse(salvo); // 👈 agora existe
    }

    public List<AvaliacaoResponse> listar(String empresa) {
        var lista = (empresa != null && !empresa.isBlank())
                ? repo.findByEmpresaIgnoreCaseOrderByCreatedAtDesc(empresa)
                : repo.findAllByOrderByCreatedAtDesc();

        return lista.stream().map(this::toResponse).toList();
    }

    public AvaliacaoResponse buscarPorId(String id) {
        Avaliacao a = repo.findById(id)
                .orElseThrow(() -> new RuntimeException("Avaliação não encontrada"));
        return toResponse(a);
    }

    private AvaliacaoResponse toResponse(Avaliacao a) {
        return new AvaliacaoResponse(
                a.getId(),
                a.getEmpresa(),
                a.getVinculo(),
                a.getSituacao(),
                a.getCargo(),
                a.getTitulo(),
                a.getTexto(),
                a.getSentimento(),
                a.getCreatedAt()
        );
    }
}