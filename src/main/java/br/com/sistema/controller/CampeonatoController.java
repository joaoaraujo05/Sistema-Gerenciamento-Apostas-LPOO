package br.com.sistema.controller;

import br.com.sistema.dao.DAOFactory;
import br.com.sistema.dao.ICampeonatoDAO;
import br.com.sistema.dao.ITimeDAO;
import br.com.sistema.model.entities.Campeonato;
import br.com.sistema.model.entities.Time;
import br.com.sistema.model.exceptions.LimiteCadastroException;

import java.util.List;

public class CampeonatoController {
    private final ICampeonatoDAO campeonatoDAO;
    private final ITimeDAO timeDAO;

    public CampeonatoController() {
        this.campeonatoDAO = DAOFactory.criarCampeonatoDAO();
        this.timeDAO = DAOFactory.criarTimeDAO();
    }

    public void cadastrarCampeonato(String nome, List<Time> times) {
        if (nome == null || nome.trim().isEmpty()) {
            throw new IllegalArgumentException("O nome do campeonato não pode ser vazio!");
        }

        if (existeCampeonatoComNome(nome, null)) {
            throw new IllegalArgumentException("Já existe um campeonato com esse nome!");
        }

        if (times == null || times.isEmpty()) {
            throw new LimiteCadastroException("Selecione ao menos um time");
        }

        Campeonato c = new Campeonato();
        for (Time t : times) {
            c.setNomeCampeonato(nome.trim());
            c.adicionarTime(t);
        }
        campeonatoDAO.salvarCampeonato(c);
    }

    public List<Campeonato> listaTodos() {
        return campeonatoDAO.listarCampeonatos();
    }

    public Campeonato buscarCampeonatoPorId(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("O Id não pode ser nulo!");
        }
        return campeonatoDAO.encontrarCampeonatoPorId(id);
    }

    public void atualizarCampeonato(Campeonato c) {
        if (c == null) {
            throw new IllegalArgumentException("Campeonato não pode ser nulo!");
        }

        if (existeCampeonatoComNome(c.getNomeCampeonato(), c.getIdCampeonato())) {
            throw new IllegalArgumentException("Já existe um campeonato com esse nome!");
        }

        if (c.getTimes() == null || c.getTimes().size() > 8 || c.getTimes().isEmpty()) {
            throw new LimiteCadastroException("selecione ao menos um time");
        }

        campeonatoDAO.atualizarCampeonato(c);
    }

    private boolean existeCampeonatoComNome(String nome, Long idIgnorar) {
        if (nome == null) {
            return false;
        }
        for (Campeonato c : campeonatoDAO.listarCampeonatos()) {
            if (c.getNomeCampeonato() != null && c.getNomeCampeonato().trim().equalsIgnoreCase(nome.trim())) {
                if (idIgnorar == null || !idIgnorar.equals(c.getIdCampeonato())) {
                    return true;
                }
            }
        }
        return false;
    }

    public void removerCampeonato(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("O Id não pode ser nulo!");
        }

        campeonatoDAO.removerCampeonatoPorId(id);
    }



}