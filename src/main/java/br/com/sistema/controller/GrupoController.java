package br.com.sistema.controller;

import br.com.sistema.dao.DAOFactory;
import br.com.sistema.dao.IGrupoDAO;
import br.com.sistema.model.entities.Grupo;
import br.com.sistema.model.entities.UsuarioParticipante;
import br.com.sistema.model.exceptions.LimiteCadastroException;

import java.util.List;

public class GrupoController {
    private final IGrupoDAO grupoDAO;

    public GrupoController() {
        this.grupoDAO = DAOFactory.criarGrupoDAO();
    }

    public void cadastrarGrupo(String nome) {
        if (nome == null || nome.trim().isEmpty()) {
            throw new IllegalArgumentException("O nome do grupo não pode ser vazio!");
        }

        if (existeGrupoComNome(nome, null)) {
            throw new IllegalArgumentException("Já existe um grupo com esse nome!");
        }

        if (grupoDAO.listarGrupos().size() >= 5) {
            throw new LimiteCadastroException("Limite de 5 grupos no sistema atingido!");
        }

        Grupo g = new Grupo();
        g.setNomeGrupo(nome.trim());
        grupoDAO.salvarGrupo(g);
    }

    public void adicionarParticipante(Long idGrupo, UsuarioParticipante participante) {
        if (idGrupo == null) {
            throw new IllegalArgumentException("O Id do grupo não pode ser nulo!");
        }

        Grupo g = grupoDAO.encontrarGrupoPorId(idGrupo);
        if (g == null) {
            throw new IllegalArgumentException("Grupo não encontrado!");
        }

        g.adicionarParticipante(participante);
        grupoDAO.atualizarGrupo(g);
    }

    public Grupo buscarGrupoPorId(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("O Id não pode ser nulo!");
        }
        return grupoDAO.encontrarGrupoPorId(id);
    }

    public void atualizarGrupo(Grupo g) {
        if (g == null) {
            throw new IllegalArgumentException("Grupo não pode ser nulo!");
        }

        if (existeGrupoComNome(g.getNomeGrupo(), g.getIdGrupo())) {
            throw new IllegalArgumentException("Já existe um grupo com esse nome!");
        }

        grupoDAO.atualizarGrupo(g);
    }

    private boolean existeGrupoComNome(String nome, Long idIgnorar) {
        if (nome == null) {
            return false;
        }
        for (Grupo g : grupoDAO.listarGrupos()) {
            if (g.getNomeGrupo() != null && g.getNomeGrupo().trim().equalsIgnoreCase(nome.trim())) {
                if (idIgnorar == null || !idIgnorar.equals(g.getIdGrupo())) {
                    return true;
                }
            }
        }
        return false;
    }

    public void removerGrupo(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("O Id não pode ser nulo!");
        }
        grupoDAO.removerGrupoPorId(id);
    }

    public List<Grupo> listarGrupos() {
        return grupoDAO.listarGrupos();
    }
}