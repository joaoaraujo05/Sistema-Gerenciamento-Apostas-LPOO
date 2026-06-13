package br.com.sistema.dao;

import br.com.sistema.model.entities.Campeonato;

import java.util.List;

public interface ICampeonatoDAO {
    void salvarCampeonato(Campeonato c);
    void atualizarCampeonato(Campeonato c);
    void removerCampeonatoPorId(Long id);
    Campeonato encontrarCampeonatoPorId(Long id);
    List<Campeonato> listarCampeonatos();
}
