package br.com.sistema.controller;

import br.com.sistema.dao.DAOFactory;
import br.com.sistema.dao.ITimeDAO;
import br.com.sistema.model.entities.Time;

import java.util.List;

public class TimeController {
    private final ITimeDAO timeDAO;

    public TimeController() {
        this.timeDAO = DAOFactory.criarTimeDAO();
    }

    public void cadastrarTime(String nome) {
        if (nome == null || nome.trim().isEmpty()) {
            throw new IllegalArgumentException("O nome não pode vazio!");
        }

        if (existeTimeComNome(nome, null)) {
            throw new IllegalArgumentException("Já existe um time com esse nome!");
        }

        Time t = new Time();
        t.setNomeTime(nome.trim());
        timeDAO.salvarTime(t);
    }

    public void atualizarTime(Time t) {
        if (t == null) {
            throw new IllegalArgumentException("Time não pode ser nulo!");
        }

        if (existeTimeComNome(t.getNomeTime(), t.getIdTime())) {
            throw new IllegalArgumentException("Já existe um time com esse nome!");
        }

        timeDAO.atualizarTime(t);
    }

    private boolean existeTimeComNome(String nome, Long idIgnorar) {
        if (nome == null) {
            return false;
        }
        for (Time t : timeDAO.listarTimes()) {
            if (t.getNomeTime() != null && t.getNomeTime().trim().equalsIgnoreCase(nome.trim())) {
                if (idIgnorar == null || !idIgnorar.equals(t.getIdTime())) {
                    return true;
                }
            }
        }
        return false;
    }

    public Time buscarTimePorId(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("O Id não pode ser nulo!");
        }
        return timeDAO.encontrarTimePorId(id);
    }

    public void removerTime(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("O Id não pode ser nulo!");
        }

        timeDAO.removerTimePorId(id);
    }

    public List<Time> listarTimes() {
        return timeDAO.listarTimes();
    }
}