package br.com.sistema.dao.jdbc;

import br.com.sistema.dao.IApostaDAO;
import br.com.sistema.model.entities.Aposta;
import br.com.sistema.model.entities.Partida;
import br.com.sistema.model.entities.UsuarioParticipante;
import br.com.sistema.model.exceptions.PersistenciaException;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ApostaDAOJDBCImpl implements IApostaDAO {

    private final PartidaDAOJDBCImpl partidaDAO = new PartidaDAOJDBCImpl();
    private final UsuarioDAOJDBCImpl usuarioDAO = new UsuarioDAOJDBCImpl();

    @Override
    public void salvarAposta(Aposta a) {
        String sql = "INSERT INTO apostas (participante_id, partida_id, resultado, gols_mandante, gols_visitante, pontos) VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement st = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            st.setLong(1, a.getParticipante().getId());
            st.setLong(2, a.getPartida().getIdPartida());
            st.setString(3, a.getResultadoEsperado());
            st.setInt(4, a.getGolsMandante());
            st.setInt(5, a.getGolsVisitante());
            st.setInt(6, a.getPontos());

            int linhasAfetadas = st.executeUpdate();

            if (linhasAfetadas > 0) {
                try (ResultSet rs = st.getGeneratedKeys()) {
                    if (rs.next()) {
                        a.setIdAposta(rs.getLong(1));
                    }
                }
            }
        } catch (SQLException e) {
            throw new PersistenciaException("Erro ao salvar a aposta: " + e.getMessage());
        }
    }

    @Override
    public void atualizarAposta(Aposta a) {
        String sql = "UPDATE apostas SET participante_id = ?, partida_id = ?, resultado = ?, gols_mandante = ?, gols_visitante = ?, pontos = ? WHERE id = ?";

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement st = conn.prepareStatement(sql)) {

            st.setLong(1, a.getParticipante().getId());
            st.setLong(2, a.getPartida().getIdPartida());
            st.setString(3, a.getResultadoEsperado());
            st.setInt(4, a.getGolsMandante());
            st.setInt(5, a.getGolsVisitante());
            st.setInt(6, a.getPontos());
            st.setLong(7, a.getIdAposta());
            st.executeUpdate();

        } catch (SQLException e) {
            throw new PersistenciaException("Erro ao atualizar a aposta com ID: " + a.getIdAposta());
        }
    }

    @Override
    public void removerApostaPorId(Long id) {
        String sql = "DELETE FROM apostas WHERE id = ?";

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement st = conn.prepareStatement(sql)) {

            st.setLong(1, id);
            st.executeUpdate();

        } catch (SQLException e) {
            throw new PersistenciaException("Erro ao remover a aposta com ID: " + id);
        }
    }

    @Override
    public Aposta encontrarApostaPorId(Long id) {
        String sql = "SELECT * FROM apostas WHERE id = ?";

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement st = conn.prepareStatement(sql)) {

            st.setLong(1, id);

            try (ResultSet rs = st.executeQuery()) {
                if (rs.next()) {
                    return montarAposta(rs);
                } else {
                    return null;
                }
            }
        } catch (SQLException e) {
            throw new PersistenciaException("Erro ao buscar a aposta com ID: " + id);
        }
    }

    @Override
    public List<Aposta> listarApostas() {
        String sql = "SELECT * FROM apostas ORDER BY id";

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement st = conn.prepareStatement(sql)) {

            List<Aposta> listaApostas = new ArrayList<>();

            try (ResultSet rs = st.executeQuery()) {
                while (rs.next()) {
                    listaApostas.add(montarAposta(rs));
                }
                return listaApostas;
            }
        } catch (SQLException e) {
            throw new PersistenciaException("Erro ao listar todas as apostas");
        }
    }

    @Override
    public List<Aposta> listarApostasPorPartida(Long idPartida) {
        String sql = "SELECT * FROM apostas WHERE partida_id = ? ORDER BY id";

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement st = conn.prepareStatement(sql)) {

            st.setLong(1, idPartida);

            List<Aposta> listaApostas = new ArrayList<>();

            try (ResultSet rs = st.executeQuery()) {
                while (rs.next()) {
                    listaApostas.add(montarAposta(rs));
                }
                return listaApostas;
            }
        } catch (SQLException e) {
            throw new PersistenciaException("Erro ao listar apostas da partida: " + idPartida);
        }
    }

    private Aposta montarAposta(ResultSet rs) throws SQLException {
        Partida partida = partidaDAO.encontrarPartidaPorId(rs.getLong("partida_id"));
        UsuarioParticipante participante = (UsuarioParticipante) usuarioDAO.encontrarUsuarioPorId(rs.getLong("participante_id"));

        Aposta a = new Aposta();
        a.setIdAposta(rs.getLong("id"));
        a.setParticipante(participante);
        a.setPartida(partida);
        a.setResultadoEsperado(rs.getString("resultado"));
        a.setGolsMandante(rs.getInt("gols_mandante"));
        a.setGolsVisitante(rs.getInt("gols_visitante"));
        a.setPontos(rs.getInt("pontos"));
        return a;
    }
}