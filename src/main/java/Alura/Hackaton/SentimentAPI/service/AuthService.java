package Alura.Hackaton.SentimentAPI.service;

import Alura.Hackaton.SentimentAPI.dto.*;
import Alura.Hackaton.SentimentAPI.entity.Usuario;
import Alura.Hackaton.SentimentAPI.repository.UsuarioRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final UsuarioRepository repository;
    private final PasswordEncoder encoder;

    public AuthService(UsuarioRepository repository, PasswordEncoder encoder) {
        this.repository = repository;
        this.encoder = encoder;
    }

    public void register(RegisterRequest req) {
        if (repository.existsByEmail(req.getEmail())) {
            throw new IllegalArgumentException("Email já cadastrado");
        }
        Usuario usuario = new Usuario(req.getEmail(), encoder.encode(req.getSenha()));
        repository.save(usuario);
    }

    public AuthResponse login(LoginRequest req) {
        Usuario usuario = repository.findByEmail(req.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("Email ou senha inválidos"));

        if (!encoder.matches(req.getSenha(), usuario.getSenha())) {
            throw new IllegalArgumentException("Email ou senha inválidos");
        }

        // por enquanto: retorna token fake (só pra fechar a resposta)
        // depois a gente troca por JWT real
        return new AuthResponse("OK");
    }
}