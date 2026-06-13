package br.com.sistema.dao;

import br.com.sistema.model.entities.Aposta;
import java.util.List;

public interface IApostaDAO {
    void salvarAposta(Aposta a);
    void atualizarAposta(Aposta a);
    void removerApostaPorId(Long id);
    Aposta encontrarApostaPorId(Long id);
    List<Aposta> listarApostas();
    List<Aposta> listarApostasPorPartida(Long idPartida);
}