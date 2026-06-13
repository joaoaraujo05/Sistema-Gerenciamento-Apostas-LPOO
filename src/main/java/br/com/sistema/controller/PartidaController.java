package br.com.sistema.controller;

import br.com.sistema.dao.DAOFactory;
import br.com.sistema.dao.IApostaDAO;
import br.com.sistema.dao.IPartidaDAO;
import br.com.sistema.model.entities.Aposta;
import br.com.sistema.model.entities.Campeonato;
import br.com.sistema.model.entities.Partida;
import br.com.sistema.model.entities.Time;

import java.time.LocalDateTime;
import java.util.List;

public class PartidaController {
    private final IPartidaDAO partidaDAO;
    private final IApostaDAO apostaDAO;

    public PartidaController() {
        this.partidaDAO = DAOFactory.criarPartidaDAO();
        this.apostaDAO = DAOFactory.criarApostaDAO();
    }

    public void cadastrarPartida(Campeonato campeonato, Time mandante, Time visitante, LocalDateTime dataHora) {
        if (existePartidaIgual(mandante, visitante, dataHora, null)) {
            throw new IllegalArgumentException("Já existe uma partida cadastrada com esses times e horário!");
        }

        Partida p = new Partida(null, campeonato, mandante, visitante, dataHora);
        partidaDAO.salvarPartida(p);
    }

    public void registrarResultado(Long idPartida, int golsMandante, int golsVisitante) {
        if (idPartida == null) {
            throw new IllegalArgumentException("O Id da partida não pode ser nulo!");
        }

        Partida p = partidaDAO.encontrarPartidaPorId(idPartida);
        if (p == null) {
            throw new IllegalArgumentException("Partida não encontrada!");
        }

        p.registrarResultado(golsMandante, golsVisitante);
        partidaDAO.atualizarPartida(p);

        List<Aposta> apostas = apostaDAO.listarApostasPorPartida(idPartida);
        for (Aposta a : apostas) {
            a.setPartida(p);
            a.calcularPontos();
            apostaDAO.atualizarAposta(a);
        }
    }

    public Partida buscarPartidaPorId(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("O Id não pode ser nulo!");
        }
        return partidaDAO.encontrarPartidaPorId(id);
    }

    public void atualizarPartida(Partida p) {
        if (p == null) {
            throw new IllegalArgumentException("Partida não pode ser nula!");
        }

        if (existePartidaIgual(p.getClubeMandante(), p.getClubeVisitante(), p.getDataHora(), p.getIdPartida())) {
            throw new IllegalArgumentException("Já existe uma partida cadastrada com esses times e horário!");
        }

        partidaDAO.atualizarPartida(p);
    }

    private boolean existePartidaIgual(Time mandante, Time visitante, LocalDateTime dataHora, Long idIgnorar) {
        if (mandante == null || visitante == null || dataHora == null) {
            return false;
        }

        for (Partida p : partidaDAO.listarPartidas()) {
            boolean mesmoMandante = p.getClubeMandante() != null && p.getClubeMandante().getIdTime().equals(mandante.getIdTime());

            boolean mesmoVisitante = p.getClubeVisitante() != null && p.getClubeVisitante().getIdTime().equals(visitante.getIdTime());

            boolean mesmaData = p.getDataHora() != null && p.getDataHora().equals(dataHora);

            if (mesmoMandante && mesmoVisitante && mesmaData) {
                if (idIgnorar == null || !idIgnorar.equals(p.getIdPartida())) {
                    return true;
                }
            }
        }
        return false;
    }

    public void removerPartida(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("O Id não pode ser nulo!");
        }
        partidaDAO.removerPartidaPorId(id);
    }

    public List<Partida> listarPartidas() {
        return partidaDAO.listarPartidas();
    }
}