package Alura.Hackaton.SentimentAPI.repository;

import Alura.Hackaton.SentimentAPI.entity.Avaliacao;
import Alura.Hackaton.SentimentAPI.enun.Sentimento;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface AvaliacaoRepository extends JpaRepository<Avaliacao, String> {
    List<Avaliacao> findByEmpresaIgnoreCaseOrderByCreatedAtDesc(String empresa);
    List<Avaliacao> findAllByOrderByCreatedAtDesc();
    List<Avaliacao> findTop3ByOrderByCreatedAtDesc();

    // consulta com paginação - funcao em desenvol
    @Query("SELECT a FROM Avaliacao a WHERE (:empresa IS NULL OR LOWER(a.empresa) LIKE LOWER(CONCAT('%', :empresa, '%'))) ORDER BY a.createdAt DESC")
    List<Avaliacao> searchByEmpresa(@Param("empresa") String empresa);

    // Para dashboard
    long countBySentimento(Sentimento sentimento);

    // Método para buscar textos por sentimento
    @Query("SELECT a.texto FROM Avaliacao a WHERE a.sentimento = :sentimento ORDER BY a.createdAt DESC")
    List<String> findTextosBySentimento(@Param("sentimento") Sentimento sentimento, Pageable pageable);
}