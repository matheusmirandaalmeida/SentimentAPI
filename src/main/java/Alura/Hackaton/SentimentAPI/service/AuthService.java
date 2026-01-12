package Alura.Hackaton.SentimentAPI.service;

import Alura.Hackaton.SentimentAPI.dto.*;
import Alura.Hackaton.SentimentAPI.entity.Usuario;
import Alura.Hackaton.SentimentAPI.exception.BusinessException;
import Alura.Hackaton.SentimentAPI.repository.UsuarioRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private static final Logger log = LoggerFactory.getLogger(AuthService.class);

    private final UsuarioRepository repository;
    private final PasswordEncoder encoder;
    private final DataExportService dataExportService;

    public AuthService(UsuarioRepository repository, PasswordEncoder encoder,
                       DataExportService dataExportService) {
        this.repository = repository;
        this.encoder = encoder;
        this.dataExportService = dataExportService;
    }

    public void register(RegisterRequest req) {
        log.info("Tentativa de registro para email: {}", req.getEmail());

        if (repository.existsByEmail(req.getEmail())) {
            throw new BusinessException("Email já cadastrado");
        }

        // Usuários registrados normalmente são USER, não ADMIN
        Usuario usuario = new Usuario(
                req.getEmail(),
                encoder.encode(req.getSenha()),
                "USER"
        );
        repository.save(usuario);

        // Exporta dados atualizados para JSON
        dataExportService.exportarDadosParaJson();

        log.info("Usuário registrado com sucesso: {}", req.getEmail());
    }

    public AuthResponse login(LoginRequest req) {
        log.info("Tentativa de login para email: {}", req.getEmail());

        Usuario usuario = repository.findByEmail(req.getEmail())
                .orElseThrow(() -> new BusinessException("Email ou senha inválidos"));

        if (!encoder.matches(req.getSenha(), usuario.getSenha())) {
            log.warn("Tentativa de login com senha incorreta para: {}", req.getEmail());
            throw new BusinessException("Email ou senha inválidos");
        }

        // Gera token com role
        String token = "fake-jwt-token-" + usuario.getId() + "-" +
                usuario.getRole() + "-" + System.currentTimeMillis();

        log.info("Login bem-sucedido para: {} (role: {})", req.getEmail(), usuario.getRole());

        return new AuthResponse(token, usuario.getRole());
    }
}