package Alura.Hackaton.SentimentAPI.controller;

import Alura.Hackaton.SentimentAPI.entity.Avaliacao;
import Alura.Hackaton.SentimentAPI.entity.Usuario;
import Alura.Hackaton.SentimentAPI.enun.Sentimento;
import Alura.Hackaton.SentimentAPI.repository.AvaliacaoRepository;
import Alura.Hackaton.SentimentAPI.repository.UsuarioRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/dashboard/empresa")
@CrossOrigin(origins = {"http://localhost:5500", "http://localhost:8080"})
public class EmpresaDashboardController {

    private final AvaliacaoRepository avaliacaoRepository;
    private final UsuarioRepository usuarioRepository;

    public EmpresaDashboardController(AvaliacaoRepository avaliacaoRepository,
                                      UsuarioRepository usuarioRepository) {
        this.avaliacaoRepository = avaliacaoRepository;
        this.usuarioRepository = usuarioRepository;
    }

    @GetMapping("/minha-empresa")
    public ResponseEntity<Map<String, Object>> getDashboardMinhaEmpresa(
            @RequestHeader(value = "Authorization", required = false) String token) {

        try {
            // Extrair email do token (implementação simplificada)
            String userEmail = extractEmailFromToken(token);

            if (userEmail == null) {
                return ResponseEntity.status(401).body(Map.of(
                        "erro", "Token de autenticação inválido ou ausente"
                ));
            }

            // Buscar usuário
            Usuario usuario = usuarioRepository.findByEmail(userEmail)
                    .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

            // Verificar se é empresa
            if (!"EMPRESA".equals(usuario.getTipoUsuario())) {
                return ResponseEntity.status(403).body(Map.of(
                        "erro", "Acesso negado. Apenas empresas podem acessar este dashboard"
                ));
            }

            // Usar nome da empresa do usuário para filtrar
            String nomeEmpresa = usuario.getNomeEmpresa();

            if (nomeEmpresa == null || nomeEmpresa.isBlank()) {
                return ResponseEntity.status(400).body(Map.of(
                        "erro", "Nome da empresa não cadastrado"
                ));
            }

            // Buscar avaliações da empresa
            List<Avaliacao> avaliacoes = avaliacaoRepository.searchByEmpresa(nomeEmpresa);

            // Contagem total
            long totalAvaliacoes = avaliacoes.size();

            // Contagem por sentimento
            long totalPositivo = avaliacoes.stream()
                    .filter(a -> a.getSentimento() == Sentimento.POSITIVO)
                    .count();
            long totalNeutro = avaliacoes.stream()
                    .filter(a -> a.getSentimento() == Sentimento.NEUTRO)
                    .count();
            long totalNegativo = avaliacoes.stream()
                    .filter(a -> a.getSentimento() == Sentimento.NEGATIVO)
                    .count();
            long totalPendente = avaliacoes.stream()
                    .filter(a -> a.getSentimento() == Sentimento.PENDENTE)
                    .count();

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
            List<Map<String, String>> comentariosComSentimento = buscarComentariosComSentimento(avaliacoes);

            // Construir resposta
            Map<String, Object> response = new HashMap<>();
            response.put("empresa", nomeEmpresa);
            response.put("cnpj", usuario.getCnpj());
            response.put("email", usuario.getEmail());
            response.put("positivo", porcentagemPositivo);
            response.put("neutro", porcentagemNeutro);
            response.put("negativo", porcentagemNegativo);
            response.put("total", totalAvaliacoes);
            response.put("total_positivo", totalPositivo);
            response.put("total_neutro", totalNeutro);
            response.put("total_negativo", totalNegativo);
            response.put("total_pendente", totalPendente);
            response.put("updatedAt", LocalDateTime.now().toString());
            response.put("comentarios", comentariosComSentimento);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of(
                    "erro", "Erro interno ao processar requisição: " + e.getMessage()
            ));
        }
    }

    private String extractEmailFromToken(String token) {
        if (token == null || !token.startsWith("fake-jwt-token-")) {
            return null;
        }

        try {
            String[] parts = token.split("-");
            if (parts.length >= 4) {
                String userId = parts[3];

                // Buscar usuário pelo ID para obter email
                Optional<Usuario> usuario = usuarioRepository.findById(Long.parseLong(userId));
                return usuario.map(Usuario::getEmail).orElse(null);
            }
        } catch (Exception e) {
            // Em caso de erro, retornar null
        }

        return null;
    }

    private List<Map<String, String>> buscarComentariosComSentimento(List<Avaliacao> avaliacoes) {
        // Mesma implementação do DashboardController
        List<Map<String, String>> comentarios = new ArrayList<>();

        try {
            List<Avaliacao> avaliacoesComTexto = avaliacoes.stream()
                    .filter(a -> a.getTexto() != null && !a.getTexto().trim().isEmpty())
                    .collect(Collectors.toList());

            int contadorPositivo = 0;
            int contadorNeutro = 0;
            int contadorNegativo = 0;
            int maxPorTipo = 3;

            for (Avaliacao avaliacao : avaliacoesComTexto) {
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

            if (comentarios.isEmpty()) {
                Map<String, String> comentarioVazio = new HashMap<>();
                comentarioVazio.put("texto", "Ainda não há avaliações para esta empresa");
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