package br.com.sistema.dao;

import br.com.sistema.model.entities.Partida;
import java.util.List;

public interface IPartidaDAO {
    void salvarPartida(Partida p);
    void atualizarPartida(Partida p);
    void removerPartidaPorId(Long id);
    Partida encontrarPartidaPorId(Long id);
    List<Partida> listarPartidas();
}