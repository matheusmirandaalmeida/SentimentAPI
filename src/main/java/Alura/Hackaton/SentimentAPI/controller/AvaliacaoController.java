package Alura.Hackaton.SentimentAPI.controller;

import Alura.Hackaton.SentimentAPI.dto.AvaliacaoCreateRequest;
import Alura.Hackaton.SentimentAPI.dto.AvaliacaoDTO;
import Alura.Hackaton.SentimentAPI.dto.AvaliacaoResponse;
import Alura.Hackaton.SentimentAPI.service.AvaliacaoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/avaliacoes")
@RequiredArgsConstructor
@CrossOrigin(origins = "*") // depois você restringe pro seu front
public class AvaliacaoController {

    private final AvaliacaoService service;

    @PostMapping
    public AvaliacaoResponse criar(@RequestBody @Valid AvaliacaoCreateRequest req) {
        return service.criar(req);
    }

    @GetMapping
    public List<AvaliacaoResponse> listar(
            @RequestParam(required = false) String empresa,
            @RequestParam(defaultValue = "0") int limit
    ) {
        return service.listar(empresa, limit);
    }

    @GetMapping("/{id}")
    public AvaliacaoResponse buscar(@PathVariable String id) {
        return service.buscarPorId(id);
    }
}