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
        log.info("Tentativa de registro: email={}, tipo={}", req.getEmail(), req.getTipoUsuario());

        // Validação de email - se ja existe
        if (repository.existsByEmail(req.getEmail())) {
            throw new BusinessException("Email já cadastrado");
        }

        // Validação para empresa - se ja existe
        if ("EMPRESA".equals(req.getTipoUsuario()) && req.getCnpj() != null) {
            if (repository.existsByCnpj(req.getCnpj())) {
                throw new BusinessException("CNPJ já cadastrado");
            }
        }

        // Validacao de campos para empresa
        if ("EMPRESA".equals(req.getTipoUsuario())) {
            if (req.getNomeEmpresa() == null || req.getNomeEmpresa().isBlank()) {
                throw new BusinessException("Nome da empresa é obrigatório");
            }
            if (req.getCnpj() == null || req.getCnpj().isBlank()) {
                throw new BusinessException("CNPJ é obrigatório para empresas");
            }
        }

        // Determinar role baseado no tipo de usuario
        String role = "EMPRESA".equals(req.getTipoUsuario()) ? "EMPRESA" : "USER";

        // Criar usuario
        Usuario usuario = new Usuario(
                req.getEmail(),
                encoder.encode(req.getSenha()),
                role,
                req.getTipoUsuario(),
                req.getNomeEmpresa(),
                req.getCnpj()
        );

        repository.save(usuario);

        // Exporta dados atualizados
        dataExportService.exportarDadosParaJson();

        log.info("Usuário registrado com sucesso: {} (tipo: {}, role: {})",
                req.getEmail(), req.getTipoUsuario(), role);
    }

    public AuthResponse login(LoginRequest req) {
        log.info("Tentativa de login: {}", req.getEmail());

        Usuario usuario = repository.findByEmail(req.getEmail())
                .orElseThrow(() -> new BusinessException("Email ou senha inválidos"));

        if (!encoder.matches(req.getSenha(), usuario.getSenha())) {
            log.warn("Tentativa de login com senha incorreta: {}", req.getEmail());
            throw new BusinessException("Email ou senha inválidos");
        }

        // Gera token com role e tipo de usuario
        String token = "fake-jwt-token-" + usuario.getId() + "-" +
                usuario.getRole() + "-" + usuario.getTipoUsuario() + "-" +
                System.currentTimeMillis();

        log.info("Login bem-sucedido: {} (role: {}, tipo: {})",
                req.getEmail(), usuario.getRole(), usuario.getTipoUsuario());

        return new AuthResponse(token, usuario.getRole(), usuario.getTipoUsuario(),
                usuario.getNomeEmpresa());
    }
}