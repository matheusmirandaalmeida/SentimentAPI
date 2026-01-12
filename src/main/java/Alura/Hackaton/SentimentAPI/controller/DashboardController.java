// EM DashboardController.java
package Alura.Hackaton.SentimentAPI.controller;

import Alura.Hackaton.SentimentAPI.enun.Sentimento;
import Alura.Hackaton.SentimentAPI.repository.AvaliacaoRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.*;

@RestController
@RequestMapping("/api/dashboard")
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

            // Aqui é para garantir que a soma sempre dará100%
            int soma = porcentagemPositivo + porcentagemNeutro + porcentagemNegativo;
            if (soma != 100 && soma > 0) {
                // Ajustar o maior valor para que some 100
                if (porcentagemPositivo >= porcentagemNeutro && porcentagemPositivo >= porcentagemNegativo) {
                    porcentagemPositivo += (100 - soma);
                } else if (porcentagemNeutro >= porcentagemPositivo && porcentagemNeutro >= porcentagemNegativo) {
                    porcentagemNeutro += (100 - soma);
                } else {
                    porcentagemNegativo += (100 - soma);
                }
            }

            // Buscar comentários recentes
            List<String> comentariosRecentes = buscarComentariosRecentes();

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
            data.put("comentarios", comentariosRecentes);

            return ResponseEntity.ok(data);

        } catch (Exception e) {
            // Fallback para dados mockados em caso de erro
            Map<String, Object> fallbackData = Map.of(
                    "positivo", 65,
                    "neutro", 20,
                    "negativo", 15,
                    "total", 100,
                    "total_positivo", 65,
                    "total_neutro", 20,
                    "total_negativo", 15,
                    "total_pendente", 0,
                    "updatedAt", LocalDateTime.now().toString(),
                    "comentarios", new String[]{
                            "Erro ao carregar dados do banco",
                            "Verifique a conexão com o banco de dados"
                    }
            );
            return ResponseEntity.ok(fallbackData);
        }
    }

    private List<String> buscarComentariosRecentes() {
        try {
            // Buscar últimos 5 comentários de cada tipo
            List<String> comentarios = new ArrayList<>();

            // Comentários positivos
            avaliacaoRepository.findTextosBySentimento(
                    Sentimento.POSITIVO,
                    org.springframework.data.domain.PageRequest.of(0, 2)
            ).forEach(comentarios::add);

            // Comentários neutros
            avaliacaoRepository.findTextosBySentimento(
                    Sentimento.NEUTRO,
                    org.springframework.data.domain.PageRequest.of(0, 2)
            ).forEach(comentarios::add);

            // Comentários negativos
            avaliacaoRepository.findTextosBySentimento(
                    Sentimento.NEGATIVO,
                    org.springframework.data.domain.PageRequest.of(0, 1)
            ).forEach(comentarios::add);

            // Se não houver comentários, retornar mensagem padrão
            if (comentarios.isEmpty()) {
                return Arrays.asList(
                        "Ainda não há avaliações no sistema",
                        "Seja o primeiro a compartilhar sua experiência!"
                );
            }

            return comentarios;

        } catch (Exception e) {
            return Arrays.asList(
                    "Erro ao carregar comentários",
                    "Tente novamente mais tarde"
            );
        }
    }
}