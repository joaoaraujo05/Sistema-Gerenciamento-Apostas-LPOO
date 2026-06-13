package br.com.sistema.dao.jdbc;

import br.com.sistema.dao.ITimeDAO;
import br.com.sistema.model.entities.Time;
import br.com.sistema.model.exceptions.PersistenciaException;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TimeDAOJDBCImpl implements ITimeDAO {

    @Override
    public void salvarTime(Time t) {
        String sql = "INSERT INTO times (nome) VALUES (?)";

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement st = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)){

            st.setString(1,t.getNomeTime());
            int linhasAfetadas = st.executeUpdate();

            if (linhasAfetadas > 0) {
                try (ResultSet rs = st.getGeneratedKeys()) {
                    if (rs.next()) {
                        Long id = rs.getLong(1);
                        t.setIdTime(id);
                    }
                }
            }
        } catch (SQLException e) {
            throw new PersistenciaException("Erro ao salvar o time: " + t.getNomeTime());
        }
    }

    @Override
    public void atualizarTime(Time t) {
        String sql = "UPDATE times SET nome = ? WHERE id = ?";

        try (Connection conn = ConnectionFactory.getConnection();
            PreparedStatement st = conn.prepareStatement(sql)) {

            st.setString(1,t.getNomeTime());
            st.setLong(2,t.getIdTime());
            st.executeUpdate();

        } catch (SQLException e) {
            throw new PersistenciaException("Erro ao atualizar o time: " + t.getNomeTime());
        }
    }

    @Override
    public void removerTimePorId(Long id) {
        String sql = "DELETE FROM times WHERE id = ?";

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement st = conn.prepareStatement(sql)) {

            st.setLong(1,id);
            st.executeUpdate();

        } catch (SQLException e) {
            throw new PersistenciaException("Erro ao remover o time com ID: " + id);
        }
    }

    @Override
    public Time encontrarTimePorId(Long id) {
        String sql = "SELECT * FROM times WHERE id = ?";

        try (Connection conn = ConnectionFactory.getConnection();
        PreparedStatement st = conn.prepareStatement(sql)) {

            st.setLong(1,id);

            try (ResultSet rs = st.executeQuery()) {
                if (rs.next()) {
                    Time t = new Time();
                    t.setIdTime(rs.getLong("id"));
                    t.setNomeTime(rs.getString("nome"));
                    return t;
                } else {
                    return null;
                }
            }
        } catch (SQLException e) {
            throw new PersistenciaException("Erro ao buscar o time com ID: " + id);
        }

    }

    @Override
    public List<Time> listarTimes() {
        String sql = "SELECT * FROM times ORDER BY id";

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement st = conn.prepareStatement(sql)) {

            List<Time> listaTimes = new ArrayList<>();

            try (ResultSet rs = st.executeQuery()) {
                while (rs.next()) {
                    Time t = new Time();
                    t.setIdTime(rs.getLong("id"));
                    t.setNomeTime(rs.getString("nome"));
                    listaTimes.add(t);
                }
                return listaTimes;
            }
        } catch (SQLException e) {
            throw new PersistenciaException("Erro ao lista todos os times");
        }
    }
}
