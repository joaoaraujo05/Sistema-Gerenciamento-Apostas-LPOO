package br.com.sistema.dao;

import br.com.sistema.model.entities.Time;

import java.util.List;

public interface ITimeDAO {
    void salvarTime(Time t);
    void atualizarTime(Time t);
    void removerTimePorId(Long id);
    Time encontrarTimePorId(Long id);
    List<Time> listarTimes();
}
