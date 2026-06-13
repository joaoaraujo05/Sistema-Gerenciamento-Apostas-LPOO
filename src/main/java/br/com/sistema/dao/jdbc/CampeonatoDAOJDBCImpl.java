package br.com.sistema.dao.jdbc;

import br.com.sistema.dao.ICampeonatoDAO;
import br.com.sistema.model.entities.Campeonato;
import br.com.sistema.model.entities.Time;
import br.com.sistema.model.exceptions.PersistenciaException;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CampeonatoDAOJDBCImpl implements ICampeonatoDAO {

    @Override
    public void salvarCampeonato(Campeonato c) {
        String sql = "INSERT INTO campeonatos (nome) VALUES (?)";

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement st = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            st.setString(1, c.getNomeCampeonato());
            int linhasAfetadas = st.executeUpdate();

            if (linhasAfetadas > 0) {
                try (ResultSet rs = st.getGeneratedKeys()) {
                    if (rs.next()) {
                        c.setIdCampeonato(rs.getLong(1));
                    }
                }
            }

            inserirTimesDoCampeonato(conn, c);

        } catch (SQLException e) {
            throw new PersistenciaException("Erro ao salvar o campeonato: " + e.getMessage());
        }
    }

    @Override
    public void atualizarCampeonato(Campeonato c) {
        String sql = "UPDATE campeonatos SET nome = ? WHERE id = ?";

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement st = conn.prepareStatement(sql)) {

            st.setString(1, c.getNomeCampeonato());
            st.setLong(2, c.getIdCampeonato());
            st.executeUpdate();

            removerVinculos(conn, c.getIdCampeonato());
            inserirTimesDoCampeonato(conn, c);

        } catch (SQLException e) {
            throw new PersistenciaException("Erro ao atualizar o campeonato com ID: " + c.getIdCampeonato());
        }
    }

    @Override
    public void removerCampeonatoPorId(Long id) {
        String sql = "DELETE FROM campeonatos WHERE id = ?";

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement st = conn.prepareStatement(sql)) {

            removerVinculos(conn, id);

            st.setLong(1, id);
            st.executeUpdate();

        } catch (SQLException e) {
            throw new PersistenciaException("Erro ao remover o campeonato com ID: " + id);
        }
    }

    @Override
    public Campeonato encontrarCampeonatoPorId(Long id) {
        String sql = "SELECT * FROM campeonatos WHERE id = ?";

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement st = conn.prepareStatement(sql)) {

            st.setLong(1, id);

            try (ResultSet rs = st.executeQuery()) {
                if (rs.next()) {
                    Campeonato c = new Campeonato();
                    c.setIdCampeonato(rs.getLong("id"));
                    c.setNomeCampeonato(rs.getString("nome"));
                    c.getTimes().addAll(carregarTimesDoCampeonato(conn, c.getIdCampeonato()));
                    return c;
                } else {
                    return null;
                }
            }
        } catch (SQLException e) {
            throw new PersistenciaException("Erro ao buscar o campeonato com ID: " + id);
        }
    }

    @Override
    public List<Campeonato> listarCampeonatos() {
        String sql = "SELECT * FROM campeonatos ORDER BY id";
        List<Campeonato> lista = new ArrayList<>();

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement st = conn.prepareStatement(sql)) {

            try (ResultSet rs = st.executeQuery()) {
                while (rs.next()) {
                    Campeonato c = new Campeonato();
                    c.setIdCampeonato(rs.getLong("id"));
                    c.setNomeCampeonato(rs.getString("nome"));
                    lista.add(c);
                }
            }

            for (Campeonato c : lista) {
                c.getTimes().addAll(carregarTimesDoCampeonato(conn, c.getIdCampeonato()));
            }

            return lista;
        } catch (SQLException e) {
            throw new PersistenciaException("Erro ao listar todos os campeonatos");
        }
    }

    private void inserirTimesDoCampeonato(Connection conn, Campeonato c) throws SQLException {
        String sql = "INSERT INTO campeonato_time (campeonato_id, time_id) VALUES (?, ?)";

        try (PreparedStatement st = conn.prepareStatement(sql)) {
            for (Time t : c.getTimes()) {
                st.setLong(1, c.getIdCampeonato());
                st.setLong(2, t.getIdTime());
                st.executeUpdate();
            }
        }
    }

    private void removerVinculos(Connection conn, Long idCampeonato) throws SQLException {
        String sql = "DELETE FROM campeonato_time WHERE campeonato_id = ?";

        try (PreparedStatement st = conn.prepareStatement(sql)) {
            st.setLong(1, idCampeonato);
            st.executeUpdate();
        }
    }

    private List<Time> carregarTimesDoCampeonato(Connection conn, Long idCampeonato) throws SQLException {
        String sql = "SELECT t.id, t.nome FROM times t " +
                "JOIN campeonato_time ct ON t.id = ct.time_id " +
                "WHERE ct.campeonato_id = ?";

        List<Time> times = new ArrayList<>();

        try (PreparedStatement st = conn.prepareStatement(sql)) {
            st.setLong(1, idCampeonato);
            try (ResultSet rs = st.executeQuery()) {
                while (rs.next()) {
                    Time t = new Time();
                    t.setIdTime(rs.getLong("id"));
                    t.setNomeTime(rs.getString("nome"));
                    times.add(t);
                }
            }
        }

        return times;
    }
}