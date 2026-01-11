package Alura.Hackaton.SentimentAPI.service;

import Alura.Hackaton.SentimentAPI.dto.SentimentRequestDTO;
import Alura.Hackaton.SentimentAPI.enun.Sentimento;
import Alura.Hackaton.SentimentAPI.dto.AvaliacaoCreateRequest;
import Alura.Hackaton.SentimentAPI.dto.AvaliacaoResponse;
import Alura.Hackaton.SentimentAPI.entity.Avaliacao;
import Alura.Hackaton.SentimentAPI.exception.ResourceNotFoundException;
import Alura.Hackaton.SentimentAPI.repository.AvaliacaoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AvaliacaoService {

    private final AvaliacaoRepository repo;
    private final SentimentService sentimentService;

    @Transactional
    public AvaliacaoResponse criar(AvaliacaoCreateRequest req) {
        //Analisa o sentimento do texto
        SentimentRequestDTO sreq = new SentimentRequestDTO(req.texto());
        var sresp = sentimentService.analyze(sreq);

        //Mapeia a resposta para o enum
        Sentimento sentimento = mapStringToSentimento(sresp.previsao());

        //Cria a entidade
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
        return toResponse(salvo);
    }

    public List<AvaliacaoResponse> listar(String empresa, int limit) {
        List<Avaliacao> lista;

        if (empresa != null && !empresa.isBlank()) {
            //Filtra por empresa
            lista = repo.findByEmpresaIgnoreCaseOrderByCreatedAtDesc(empresa);
        } else {
            //ordenadas por data
            lista = repo.findAllByOrderByCreatedAtDesc();
        }

        // Aplica limite
        if (limit > 0) {
            //Aqui o limite é 3 - config no repository
            if (empresa == null || empresa.isBlank()) {
                lista = repo.findTop3ByOrderByCreatedAtDesc();
            } else {
                lista = lista.stream().limit(limit).toList();
            }
        }

        return lista.stream()
                .map(this::toResponse)
                .toList();
    }

    public AvaliacaoResponse buscarPorId(String id) {
        Avaliacao a = repo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Avaliação com ID " + id + " não encontrada"));
        return toResponse(a);
    }

    private Sentimento mapStringToSentimento(String previsao) {
        if (previsao == null) return Sentimento.PENDENTE;

        return switch (previsao.toUpperCase()) {
            case "POSITIVO" -> Sentimento.POSITIVO;
            case "NEGATIVO" -> Sentimento.NEGATIVO;
            case "NEUTRO" -> Sentimento.NEUTRO;
            default -> Sentimento.PENDENTE;
        };
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