package Alura.Hackaton.SentimentAPI.service;

import Alura.Hackaton.SentimentAPI.entity.Avaliacao;
import Alura.Hackaton.SentimentAPI.entity.Usuario;
import Alura.Hackaton.SentimentAPI.repository.AvaliacaoRepository;
import Alura.Hackaton.SentimentAPI.repository.UsuarioRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


//Essa classe é para exportar dados para o Json, irei mudar o formato do H2 para gerar um Json, assim,
// o tornando um banco que irá permancer com os dados salvos em json.
@Service
public class DataExportService {

    private static final Logger log = LoggerFactory.getLogger(DataExportService.class);

    private final UsuarioRepository usuarioRepository;
    private AvaliacaoRepository avaliacaoRepository;
    private final ObjectMapper objectMapper;

    public DataExportService(UsuarioRepository usuarioRepository,
                             AvaliacaoRepository avaliacaoRepository,
                             ObjectMapper objectMapper) {
        this.usuarioRepository = usuarioRepository;
        this.avaliacaoRepository = avaliacaoRepository;
        this.objectMapper = objectMapper;
        this.objectMapper.registerModule(new JavaTimeModule());
        this.objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
        this.objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }

    @EventListener(ApplicationReadyEvent.class)
    public void init() {
        //aqui é para criar um adm padrão
        criarAdminPadrao();

        exportarDadosParaJson();
    }

    private void criarAdminPadrao() {
        if(!usuarioRepository.existsByEmail("admin@Sentiment.com")) {
            // aqui criamos um passworEnconder bean
            org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder encoder =
                    new org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder();

            Usuario admin = new Usuario(
                    "admin@Sentiment.com",
                    encoder.encode("admin123"),
                    "ADMIN",
                    "INDIVIDUAL",
                    null,
                    null
            );
            usuarioRepository.save(admin);
            log.info("Criando administracao com sucesso");
        }
    }

    public void exportarDadosParaJson() {
        try {
            Path dataDir = Paths.get("data");
            if (!Files.exists(dataDir)) {
                Files.createDirectories(dataDir);
            }

            //aqui exportamos os usuaarios
            List<Usuario> usuarios = usuarioRepository.findAll();
            File usuariosFile = new File("data/usuarios.json");
            objectMapper.writeValue(usuariosFile, usuarios);

            //exportar aas avaliações
            List<Avaliacao> avaliacoes = avaliacaoRepository.findAll();
            File avaliacoesFile = new File("data/avaliacoes.json");
            objectMapper.writeValue(avaliacoesFile, avaliacoes);

            log.info("Export dados com sucesso");
        } catch (Exception e) {
            throw new RuntimeException("Erro ao exportar dados para json",e);
        }
    }

    public Map<String, Object> importarDadosParaJson() throws IOException {
        Map<String, Object> map = new HashMap<>();

        File usuariosFile = new File("data/usuarios.json");
        if (usuariosFile.exists()) {
            List<Usuario> usuarios = objectMapper.readValue(
                    usuariosFile,
                    objectMapper.getTypeFactory().constructCollectionType(List.class, Usuario.class)
            );
            map.put("usuarios", usuarios);
        }

        File avaliacoesFile = new File("data/avaliacoes.json");
        if (avaliacoesFile.exists()) {
            List<Avaliacao> avaliacaos = objectMapper.readValue(
                    avaliacoesFile,
                    objectMapper.getTypeFactory().constructCollectionType(List.class, Avaliacao.class)
            );
            map.put("avaliacoes", avaliacaos);
        }
        return map;
    }
}
