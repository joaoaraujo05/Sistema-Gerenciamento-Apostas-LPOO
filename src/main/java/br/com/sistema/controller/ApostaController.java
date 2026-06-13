package br.com.sistema.controller;

import br.com.sistema.dao.DAOFactory;
import br.com.sistema.dao.IApostaDAO;
import br.com.sistema.dao.IPartidaDAO;
import br.com.sistema.model.entities.Aposta;
import br.com.sistema.model.entities.Partida;
import br.com.sistema.model.entities.UsuarioParticipante;
import br.com.sistema.model.exceptions.ApostaInvalidaException;

import java.util.ArrayList;
import java.util.List;

public class ApostaController {
    private final IApostaDAO apostaDAO;
    private final IPartidaDAO partidaDAO;

    public ApostaController() {
        this.apostaDAO = DAOFactory.criarApostaDAO();
        this.partidaDAO = DAOFactory.criarPartidaDAO();
    }

    public void registrarAposta(UsuarioParticipante participante, Long idPartida, String resultadoEsperado, int golsMandante, int golsVisitante) {

        if (participante == null) {
            throw new IllegalArgumentException("É necessário informar o participante!");
        }

        if (idPartida == null) {
            throw new IllegalArgumentException("É necessário informar a partida!");
        }

        Partida p = partidaDAO.encontrarPartidaPorId(idPartida);
        if (p == null) {
            throw new IllegalArgumentException("Partida não encontrada!");
        }

        p.validarPrazoParaAposta();

        Aposta a = new Aposta(participante, p, resultadoEsperado, golsMandante, golsVisitante);

        apostaDAO.salvarAposta(a);
    }

    public void editarAposta(Long idAposta, String resultadoEsperado, int golsMandante, int golsVisitante) {
        if (idAposta == null) {
            throw new IllegalArgumentException("É necessário informar a aposta!");
        }

        Aposta a = apostaDAO.encontrarApostaPorId(idAposta);
        if (a == null || a.getPartida() == null) {
            throw new IllegalArgumentException("Aposta não encontrada!");
        }

        Partida p = partidaDAO.encontrarPartidaPorId(a.getPartida().getIdPartida());
        if (p == null) {
            throw new IllegalArgumentException("Partida da aposta não encontrada!");
        }

        if (p.isFinalizada()) {
            throw new ApostaInvalidaException("A partida já foi finalizada; não é possível alterar a aposta.");
        }

        p.validarPrazoParaAposta();

        a.setResultadoEsperado(resultadoEsperado);
        a.setGolsMandante(golsMandante);
        a.setGolsVisitante(golsVisitante);

        a.validarResultadoEPlacar();

        apostaDAO.atualizarAposta(a);
    }

    public Aposta buscarApostaPorId(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("O Id não pode ser nulo!");
        }
        return apostaDAO.encontrarApostaPorId(id);
    }

    public void removerAposta(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("O Id não pode ser nulo!");
        }
        apostaDAO.removerApostaPorId(id);
    }

    public List<Aposta> listarApostas() {
        return apostaDAO.listarApostas();
    }

    public List<Aposta> listarApostasPorParticipante(Long idParticipante) {
        if (idParticipante == null) {
            throw new IllegalArgumentException("O Id do participante não pode ser nulo!");
        }

        List<Aposta> resultado = new ArrayList<>();
        for (Aposta a : apostaDAO.listarApostas()) {
            if (a.getParticipante() != null
                    && idParticipante.equals(a.getParticipante().getId())) {
                resultado.add(a);
            }
        }
        return resultado;
    }

    public List<Aposta> listarApostasPorPartida(Long idPartida) {
        if (idPartida == null) {
            throw new IllegalArgumentException("O Id da partida não pode ser nulo!");
        }
        return apostaDAO.listarApostasPorPartida(idPartida);
    }

    public int pontuacaoDoParticipante(Long idParticipante) {
        if (idParticipante == null) {
            throw new IllegalArgumentException("O Id do participante não pode ser nulo!");
        }

        int total = 0;
        for (Aposta a : apostaDAO.listarApostas()) {
            if (a.getParticipante() != null && idParticipante.equals(a.getParticipante().getId())) {

                total += a.calcularPontos();
            }
        }
        return total;
    }
}