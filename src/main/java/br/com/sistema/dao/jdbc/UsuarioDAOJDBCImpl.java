package br.com.sistema.dao.jdbc;

import br.com.sistema.dao.IUsuarioDAO;
import br.com.sistema.model.entities.Grupo;
import br.com.sistema.model.entities.Usuario;
import br.com.sistema.model.entities.UsuarioAdministrador;
import br.com.sistema.model.entities.UsuarioParticipante;
import br.com.sistema.model.exceptions.PersistenciaException;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UsuarioDAOJDBCImpl implements IUsuarioDAO {

    @Override
    public void salvarUsuario(Usuario u) {
        String sql = "INSERT INTO usuarios (nome, login, senha, perfil, pontuacao_total) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement st = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            preencherStatement(st, u);
            int linhasAfetadas = st.executeUpdate();

            if (linhasAfetadas > 0) {
                try (ResultSet rs = st.getGeneratedKeys()) {
                    if (rs.next()) {
                        u.setId(rs.getLong(1));
                    }
                }
            }

            if (u instanceof UsuarioParticipante) {
                inserirGruposDoParticipante(conn, (UsuarioParticipante) u);
            }
        } catch (SQLException e) {
            throw new PersistenciaException("Erro ao salvar o usuario: " + e.getMessage());
        }
    }

    @Override
    public void atualizarUsuario(Usuario u) {
        String sql = "UPDATE usuarios SET nome = ?, login = ?, senha = ?, perfil = ?, pontuacao_total = ? WHERE id = ?";

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement st = conn.prepareStatement(sql)) {

            preencherStatement(st, u);
            st.setLong(6, u.getId());
            st.executeUpdate();

            if (u instanceof UsuarioParticipante) {
                removerGruposDoParticipante(conn, u.getId());
                inserirGruposDoParticipante(conn, (UsuarioParticipante) u);
            }
        } catch (SQLException e) {
            throw new PersistenciaException("Erro ao atualizar o usuario com ID: " + u.getId());
        }
    }

    @Override
    public void removerUsuarioPorId(Long id) {
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement st = conn.prepareStatement("DELETE FROM usuarios WHERE id = ?")) {

            removerGruposDoParticipante(conn, id);

            st.setLong(1, id);
            st.executeUpdate();

        } catch (SQLException e) {
            throw new PersistenciaException("Erro ao remover o usuario com ID: " + id);
        }
    }

    @Override
    public Usuario encontrarUsuarioPorId(Long id) {
        String sql = "SELECT * FROM usuarios WHERE id = ?";

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement st = conn.prepareStatement(sql)) {

            st.setLong(1, id);

            try (ResultSet rs = st.executeQuery()) {
                if (rs.next()) {
                    return montarUsuario(rs);
                } else {
                    return null;
                }
            }
        } catch (SQLException e) {
            throw new PersistenciaException("Erro ao buscar o usuario com ID: " + id);
        }
    }

    @Override
    public Usuario encontrarPorLogin(String login) {
        String sql = "SELECT * FROM usuarios WHERE login = ?";

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement st = conn.prepareStatement(sql)) {

            st.setString(1, login);

            try (ResultSet rs = st.executeQuery()) {
                if (rs.next()) {
                    return montarUsuario(rs);
                } else {
                    return null;
                }
            }
        } catch (SQLException e) {
            throw new PersistenciaException("Erro ao buscar usuario por login: " + e.getMessage());
        }
    }

    @Override
    public List<UsuarioParticipante> listarParticipantes() {
        String sql = "SELECT * FROM usuarios WHERE perfil = 'PARTICIPANTE' ORDER BY id";
        return buscarParticipantes(sql, null);
    }

    @Override
    public List<UsuarioParticipante> listarParticipantesPorGrupo(Long idGrupo) {
        String sql = "SELECT u.* FROM usuarios u " +
                "JOIN participante_grupo pg ON u.id = pg.participante_id " +
                "WHERE pg.grupo_id = ? AND u.perfil = 'PARTICIPANTE' ORDER BY u.id";
        return buscarParticipantes(sql, idGrupo);
    }

    private void preencherStatement(PreparedStatement st, Usuario u) throws SQLException {
        st.setString(1, u.getNome());
        st.setString(2, u.getLogin());
        st.setString(3, u.getSenha());
        st.setString(4, u.obterPerfil()); // discriminador "ADMINISTRADOR" ou "PARTICIPANTE"

        if (u instanceof UsuarioParticipante) {
            st.setInt(5, ((UsuarioParticipante) u).getPontuacaoTotal());
        } else {
            st.setInt(5, 0); // administrador nao tem pontuacao
        }
    }

    private Usuario montarUsuario(ResultSet rs) throws SQLException {
        String perfil = rs.getString("perfil");

        if ("ADMINISTRADOR".equals(perfil)) {
            UsuarioAdministrador a = new UsuarioAdministrador();
            a.setId(rs.getLong("id"));
            a.setNome(rs.getString("nome"));
            a.setLogin(rs.getString("login"));
            a.setSenha(rs.getString("senha"));
            return a;
        } else {
            UsuarioParticipante p = new UsuarioParticipante();
            p.setId(rs.getLong("id"));
            p.setNome(rs.getString("nome"));
            p.setLogin(rs.getString("login"));
            p.setSenha(rs.getString("senha"));
            p.setPontuacaoTotal(rs.getInt("pontuacao_total"));
            p.setGrupos(carregarGruposDoParticipante(p.getId()));
            return p;
        }
    }

    private List<UsuarioParticipante> buscarParticipantes(String sql, Long idGrupo) {
        List<UsuarioParticipante> lista = new ArrayList<>();

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement st = conn.prepareStatement(sql)) {

            if (idGrupo != null) {
                st.setLong(1, idGrupo);
            }

            try (ResultSet rs = st.executeQuery()) {
                while (rs.next()) {
                    lista.add((UsuarioParticipante) montarUsuario(rs));
                }
            }

            return lista;
        } catch (SQLException e) {
            throw new PersistenciaException("Erro ao listar participantes: " + e.getMessage());
        }
    }

    private void inserirGruposDoParticipante(Connection conn, UsuarioParticipante p) throws SQLException {
        String sql = "INSERT INTO participante_grupo (participante_id, grupo_id) VALUES (?, ?)";

        try (PreparedStatement st = conn.prepareStatement(sql)) {
            for (Grupo g : p.getGrupos()) {
                st.setLong(1, p.getId());
                st.setLong(2, g.getIdGrupo());
                st.executeUpdate();
            }
        }
    }

    private void removerGruposDoParticipante(Connection conn, Long idParticipante) throws SQLException {
        try (PreparedStatement st = conn.prepareStatement("DELETE FROM participante_grupo WHERE participante_id = ?")) {
            st.setLong(1, idParticipante);
            st.executeUpdate();
        }
    }

    private List<Grupo> carregarGruposDoParticipante(Long idParticipante) {
        String sql = "SELECT g.id, g.nome FROM grupos g " +
                "JOIN participante_grupo pg ON g.id = pg.grupo_id " +
                "WHERE pg.participante_id = ?";

        List<Grupo> grupos = new ArrayList<>();

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement st = conn.prepareStatement(sql)) {

            st.setLong(1, idParticipante);
            try (ResultSet rs = st.executeQuery()) {
                while (rs.next()) {
                    Grupo g = new Grupo();
                    g.setIdGrupo(rs.getLong("id"));
                    g.setNomeGrupo(rs.getString("nome"));
                    grupos.add(g);
                }
            }
        } catch (SQLException e) {
            throw new PersistenciaException("Erro ao carregar grupos do participante: " + e.getMessage());
        }

        return grupos;
    }
}