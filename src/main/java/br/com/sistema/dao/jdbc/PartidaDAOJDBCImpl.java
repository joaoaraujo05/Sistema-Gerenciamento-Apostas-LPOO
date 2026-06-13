package br.com.sistema.dao.jdbc;

import br.com.sistema.dao.IPartidaDAO;
import br.com.sistema.model.entities.Partida;
import br.com.sistema.model.entities.Time;
import br.com.sistema.model.exceptions.PersistenciaException;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PartidaDAOJDBCImpl implements IPartidaDAO {

    private final TimeDAOJDBCImpl timeDAO = new TimeDAOJDBCImpl();
    private final CampeonatoDAOJDBCImpl campeonatoDAO = new CampeonatoDAOJDBCImpl();

    @Override
    public void salvarPartida(Partida p) {
        String sql = "INSERT INTO partidas (campeonato_id, mandante_id, visitante_id, data_hora, gols_mandante, gols_visitante, finalizada) VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement st = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            st.setLong(1, p.getCampeonato().getIdCampeonato());
            st.setLong(2, p.getClubeMandante().getIdTime());
            st.setLong(3, p.getClubeVisitante().getIdTime());
            st.setTimestamp(4, Timestamp.valueOf(p.getDataHora()));
            st.setInt(5, p.getGolsMandante());
            st.setInt(6, p.getGolsVisitante());
            st.setBoolean(7, p.isFinalizada());

            int linhasAfetadas = st.executeUpdate();

            if (linhasAfetadas > 0) {
                try (ResultSet rs = st.getGeneratedKeys()) {
                    if (rs.next()) {
                        p.setIdPartida(rs.getLong(1));
                    }
                }
            }
        } catch (SQLException e) {
            throw new PersistenciaException("Erro ao salvar a partida: " + e.getMessage());
        }
    }

    @Override
    public void atualizarPartida(Partida p) {
        String sql = "UPDATE partidas SET campeonato_id = ?, mandante_id = ?, visitante_id = ?, data_hora = ?, gols_mandante = ?, gols_visitante = ?, finalizada = ? WHERE id = ?";

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement st = conn.prepareStatement(sql)) {

            st.setLong(1, p.getCampeonato().getIdCampeonato());
            st.setLong(2, p.getClubeMandante().getIdTime());
            st.setLong(3, p.getClubeVisitante().getIdTime());
            st.setTimestamp(4, Timestamp.valueOf(p.getDataHora()));
            st.setInt(5, p.getGolsMandante());
            st.setInt(6, p.getGolsVisitante());
            st.setBoolean(7, p.isFinalizada());
            st.setLong(8, p.getIdPartida());
            st.executeUpdate();

        } catch (SQLException e) {
            throw new PersistenciaException("Erro ao atualizar a partida com ID: " + p.getIdPartida());
        }
    }

    @Override
    public void removerPartidaPorId(Long id) {
        String sql = "DELETE FROM partidas WHERE id = ?";

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement st = conn.prepareStatement(sql)) {

            st.setLong(1, id);
            st.executeUpdate();

        } catch (SQLException e) {
            throw new PersistenciaException("Erro ao remover a partida com ID: " + id);
        }
    }

    @Override
    public Partida encontrarPartidaPorId(Long id) {
        String sql = "SELECT * FROM partidas WHERE id = ?";

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement st = conn.prepareStatement(sql)) {

            st.setLong(1, id);

            try (ResultSet rs = st.executeQuery()) {
                if (rs.next()) {
                    return montarPartida(rs);
                } else {
                    return null;
                }
            }
        } catch (SQLException e) {
            throw new PersistenciaException("Erro ao buscar a partida com ID: " + id);
        }
    }

    @Override
    public List<Partida> listarPartidas() {
        String sql = "SELECT * FROM partidas ORDER BY id";

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement st = conn.prepareStatement(sql)) {

            List<Partida> listaPartidas = new ArrayList<>();

            try (ResultSet rs = st.executeQuery()) {
                while (rs.next()) {
                    listaPartidas.add(montarPartida(rs));
                }
                return listaPartidas;
            }
        } catch (SQLException e) {
            throw new PersistenciaException("Erro ao listar todas as partidas");
        }
    }

    private Partida montarPartida(ResultSet rs) throws SQLException {
        Time mandante = timeDAO.encontrarTimePorId(rs.getLong("mandante_id"));
        Time visitante = timeDAO.encontrarTimePorId(rs.getLong("visitante_id"));

        Partida p = new Partida();
        p.setIdPartida(rs.getLong("id"));

        long idCampeonato = rs.getLong("campeonato_id");
        if (!rs.wasNull()) {
            p.setCampeonato(campeonatoDAO.encontrarCampeonatoPorId(idCampeonato));
        }

        p.setClubeMandante(mandante);
        p.setClubeVisitante(visitante);
        p.setDataHora(rs.getTimestamp("data_hora").toLocalDateTime());
        p.setGolsMandante(rs.getInt("gols_mandante"));
        p.setGolsVisitante(rs.getInt("gols_visitante"));
        p.setFinalizada(rs.getBoolean("finalizada"));
        return p;
    }
}