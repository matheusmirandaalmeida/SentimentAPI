package Alura.Hackaton.SentimentAPI.repository;

import Alura.Hackaton.SentimentAPI.entity.Avaliacao;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AvaliacaoRepository extends JpaRepository<Avaliacao, String> {
    List<Avaliacao> findByEmpresaIgnoreCaseOrderByCreatedAtDesc(String empresa);
    List<Avaliacao> findAllByOrderByCreatedAtDesc();
    List<Avaliacao> findTop3ByOrderByIdDesc();
}
