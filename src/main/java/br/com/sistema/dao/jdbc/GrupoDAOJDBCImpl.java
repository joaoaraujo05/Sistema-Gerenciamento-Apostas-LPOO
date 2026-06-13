package br.com.sistema.dao.jdbc;

import br.com.sistema.dao.IGrupoDAO;
import br.com.sistema.model.entities.Grupo;
import br.com.sistema.model.entities.UsuarioParticipante;
import br.com.sistema.model.exceptions.PersistenciaException;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class GrupoDAOJDBCImpl implements IGrupoDAO {

    @Override
    public void salvarGrupo(Grupo g) {
        String sql = "INSERT INTO grupos (nome) VALUES (?)";

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement st = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            st.setString(1, g.getNomeGrupo());
            int linhasAfetadas = st.executeUpdate();

            if (linhasAfetadas > 0) {
                try (ResultSet rs = st.getGeneratedKeys()) {
                    if (rs.next()) {
                        g.setIdGrupo(rs.getLong(1));
                    }
                }
            }
        } catch (SQLException e) {
            throw new PersistenciaException("Erro ao salvar o grupo: " + g.getNomeGrupo());
        }
    }

    @Override
    public void atualizarGrupo(Grupo g) {
        String sql = "UPDATE grupos SET nome = ? WHERE id = ?";

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement st = conn.prepareStatement(sql)) {

            st.setString(1, g.getNomeGrupo());
            st.setLong(2, g.getIdGrupo());
            st.executeUpdate();

        } catch (SQLException e) {
            throw new PersistenciaException("Erro ao atualizar o grupo: " + g.getNomeGrupo());
        }
    }

    @Override
    public void removerGrupoPorId(Long id) {
        String sql = "DELETE FROM grupos WHERE id = ?";

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement st = conn.prepareStatement(sql)) {

            st.setLong(1, id);
            st.executeUpdate();

        } catch (SQLException e) {
            throw new PersistenciaException("Erro ao remover o grupo com ID: " + id);
        }
    }

    @Override
    public Grupo encontrarGrupoPorId(Long id) {
        String sql = "SELECT * FROM grupos WHERE id = ?";

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement st = conn.prepareStatement(sql)) {

            st.setLong(1, id);

            try (ResultSet rs = st.executeQuery()) {
                if (rs.next()) {
                    Grupo g = new Grupo();
                    g.setIdGrupo(rs.getLong("id"));
                    g.setNomeGrupo(rs.getString("nome"));
                    g.getParticipantes().addAll(carregarParticipantesDoGrupo(conn, g.getIdGrupo()));
                    return g;
                } else {
                    return null;
                }
            }
        } catch (SQLException e) {
            throw new PersistenciaException("Erro ao buscar o grupo com ID: " + id);
        }
    }

    @Override
    public List<Grupo> listarGrupos() {
        String sql = "SELECT * FROM grupos ORDER BY id";
        List<Grupo> listaGrupos = new ArrayList<>();

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement st = conn.prepareStatement(sql)) {

            try (ResultSet rs = st.executeQuery()) {
                while (rs.next()) {
                    Grupo g = new Grupo();
                    g.setIdGrupo(rs.getLong("id"));
                    g.setNomeGrupo(rs.getString("nome"));
                    listaGrupos.add(g);
                }
            }

            for (Grupo g : listaGrupos) {
                g.getParticipantes().addAll(carregarParticipantesDoGrupo(conn, g.getIdGrupo()));
            }

            return listaGrupos;
        } catch (SQLException e) {
            throw new PersistenciaException("Erro ao listar todos os grupos");
        }
    }

    private List<UsuarioParticipante> carregarParticipantesDoGrupo(Connection conn, Long idGrupo) throws SQLException {
        String sql = "SELECT u.id, u.nome, u.login, u.senha, u.pontuacao_total FROM usuarios u " +
                "JOIN participante_grupo pg ON u.id = pg.participante_id " +
                "WHERE pg.grupo_id = ? AND u.perfil = 'PARTICIPANTE'";

        List<UsuarioParticipante> participantes = new ArrayList<>();

        try (PreparedStatement st = conn.prepareStatement(sql)) {
            st.setLong(1, idGrupo);
            try (ResultSet rs = st.executeQuery()) {
                while (rs.next()) {
                    UsuarioParticipante p = new UsuarioParticipante();
                    p.setId(rs.getLong("id"));
                    p.setNome(rs.getString("nome"));
                    p.setLogin(rs.getString("login"));
                    p.setSenha(rs.getString("senha"));
                    p.setPontuacaoTotal(rs.getInt("pontuacao_total"));
                    participantes.add(p);
                }
            }
        }

        return participantes;
    }
}