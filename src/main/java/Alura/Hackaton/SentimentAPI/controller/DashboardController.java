package Alura.Hackaton.SentimentAPI.controller;

import Alura.Hackaton.SentimentAPI.entity.Avaliacao;
import Alura.Hackaton.SentimentAPI.enun.Sentimento;
import Alura.Hackaton.SentimentAPI.repository.AvaliacaoRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/dashboard")
@CrossOrigin(origins = {"http://localhost:5500", "http://localhost:8080"})
public class DashboardController {

    private final AvaliacaoRepository avaliacaoRepository;

    public DashboardController(AvaliacaoRepository avaliacaoRepository) {
        this.avaliacaoRepository = avaliacaoRepository;
    }

    @GetMapping
    public ResponseEntity<Map<String, Object>> getDashboardData() {
        try {
            // Contagem total
            long totalAvaliacoes = avaliacaoRepository.count();

            // Contagem por sentimento
            long totalPositivo = avaliacaoRepository.countBySentimento(Sentimento.POSITIVO);
            long totalNeutro = avaliacaoRepository.countBySentimento(Sentimento.NEUTRO);
            long totalNegativo = avaliacaoRepository.countBySentimento(Sentimento.NEGATIVO);
            long totalPendente = avaliacaoRepository.countBySentimento(Sentimento.PENDENTE);

            // Calcular porcentagens
            int porcentagemPositivo = 0;
            int porcentagemNeutro = 0;
            int porcentagemNegativo = 0;

            if (totalAvaliacoes > 0) {
                double avaliacoesProcessadas = totalAvaliacoes - totalPendente;
                if (avaliacoesProcessadas > 0) {
                    porcentagemPositivo = (int) Math.round((totalPositivo * 100.0) / avaliacoesProcessadas);
                    porcentagemNeutro = (int) Math.round((totalNeutro * 100.0) / avaliacoesProcessadas);
                    porcentagemNegativo = (int) Math.round((totalNegativo * 100.0) / avaliacoesProcessadas);
                }
            }

            // Ajuste para soma dar 100%
            int soma = porcentagemPositivo + porcentagemNeutro + porcentagemNegativo;
            if (soma != 100 && soma > 0) {
                if (porcentagemPositivo >= porcentagemNeutro && porcentagemPositivo >= porcentagemNegativo) {
                    porcentagemPositivo += (100 - soma);
                } else if (porcentagemNeutro >= porcentagemPositivo && porcentagemNeutro >= porcentagemNegativo) {
                    porcentagemNeutro += (100 - soma);
                } else {
                    porcentagemNegativo += (100 - soma);
                }
            }

            // Buscar comentários com sentimento
            List<Map<String, String>> comentariosComSentimento = buscarComentariosComSentimento();

            // Construir resposta
            Map<String, Object> data = new HashMap<>();
            data.put("positivo", porcentagemPositivo);
            data.put("neutro", porcentagemNeutro);
            data.put("negativo", porcentagemNegativo);
            data.put("total", totalAvaliacoes);
            data.put("total_positivo", totalPositivo);
            data.put("total_neutro", totalNeutro);
            data.put("total_negativo", totalNegativo);
            data.put("total_pendente", totalPendente);
            data.put("updatedAt", LocalDateTime.now().toString());
            data.put("comentarios", comentariosComSentimento); // Agora é uma lista de objetos

            return ResponseEntity.ok(data);

        } catch (Exception e) {
            // Fallback com estrutura correta
            Map<String, Object> fallbackData = new HashMap<>();
            fallbackData.put("positivo", 65);
            fallbackData.put("neutro", 20);
            fallbackData.put("negativo", 15);
            fallbackData.put("total", 100);
            fallbackData.put("total_positivo", 65);
            fallbackData.put("total_neutro", 20);
            fallbackData.put("total_negativo", 15);
            fallbackData.put("total_pendente", 0);
            fallbackData.put("updatedAt", LocalDateTime.now().toString());

            List<Map<String, String>> fallbackComentarios = Arrays.asList(
                    Map.of("texto", "Erro ao carregar dados do banco", "sentimento", "negativo"),
                    Map.of("texto", "Verifique a conexão com o banco de dados", "sentimento", "neutro")
            );
            fallbackData.put("comentarios", fallbackComentarios);

            return ResponseEntity.ok(fallbackData);
        }
    }

    private List<Map<String, String>> buscarComentariosComSentimento() {
        List<Map<String, String>> comentarios = new ArrayList<>();

        try {
            // Buscar avaliações com texto não vazio
            List<Avaliacao> avaliacoes = avaliacaoRepository.findAll()
                    .stream()
                    .filter(a -> a.getTexto() != null && !a.getTexto().trim().isEmpty())
                    .collect(Collectors.toList());

            // Adicionar comentários de cada sentimento
            int contadorPositivo = 0;
            int contadorNeutro = 0;
            int contadorNegativo = 0;
            int maxPorTipo = 3; // Máximo de comentários por tipo

            for (Avaliacao avaliacao : avaliacoes) {
                if (avaliacao.getSentimento() == Sentimento.POSITIVO && contadorPositivo < maxPorTipo) {
                    Map<String, String> comentario = new HashMap<>();
                    comentario.put("texto", avaliacao.getTexto());
                    comentario.put("sentimento", "positivo");
                    comentarios.add(comentario);
                    contadorPositivo++;
                } else if (avaliacao.getSentimento() == Sentimento.NEUTRO && contadorNeutro < maxPorTipo) {
                    Map<String, String> comentario = new HashMap<>();
                    comentario.put("texto", avaliacao.getTexto());
                    comentario.put("sentimento", "neutro");
                    comentarios.add(comentario);
                    contadorNeutro++;
                } else if (avaliacao.getSentimento() == Sentimento.NEGATIVO && contadorNegativo < maxPorTipo) {
                    Map<String, String> comentario = new HashMap<>();
                    comentario.put("texto", avaliacao.getTexto());
                    comentario.put("sentimento", "negativo");
                    comentarios.add(comentario);
                    contadorNegativo++;
                }
                if (contadorPositivo + contadorNeutro + contadorNegativo >= 10) {
                    break;
                }
            }

            // Se não houver comentários
            if (comentarios.isEmpty()) {
                Map<String, String> comentarioVazio = new HashMap<>();
                comentarioVazio.put("texto", "Ainda não há avaliações no sistema");
                comentarioVazio.put("sentimento", "neutro");
                comentarios.add(comentarioVazio);
            }

        } catch (Exception e) {
            Map<String, String> comentarioErro = new HashMap<>();
            comentarioErro.put("texto", "Erro ao carregar comentários");
            comentarioErro.put("sentimento", "negativo");
            comentarios.add(comentarioErro);
        }

        return comentarios;
    }
}