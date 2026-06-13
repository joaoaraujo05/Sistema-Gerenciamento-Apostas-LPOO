package br.com.sistema.controller;

import br.com.sistema.dao.DAOFactory;
import br.com.sistema.dao.IUsuarioDAO;
import br.com.sistema.model.entities.Grupo;
import br.com.sistema.model.entities.Usuario;
import br.com.sistema.model.entities.UsuarioAdministrador;
import br.com.sistema.model.entities.UsuarioParticipante;
import br.com.sistema.model.exceptions.LimiteCadastroException;

import java.util.List;

public class UsuarioController {
    private final IUsuarioDAO usuarioDAO;

    public UsuarioController() {
        this.usuarioDAO = DAOFactory.criarUsuarioDAO();
    }

    public Usuario autenticar(String login, String senha) {
        if (login == null || login.trim().isEmpty()) {
            throw new IllegalArgumentException("Informe o login!");
        }

        Usuario u = usuarioDAO.encontrarPorLogin(login);
        if (u != null && u.autenticar(login, senha)) {
            return u;
        }
        return null;
    }

    public void garantirAdminPadrao() {
        if (usuarioDAO.encontrarPorLogin("admin") == null) {
            UsuarioAdministrador admin = new UsuarioAdministrador();
            admin.setNome("Administrador");
            admin.setLogin("admin");
            admin.setSenha("admin123");
            usuarioDAO.salvarUsuario(admin);
        }
    }

    public void cadastrarAdministrador(String nome, String login, String senha) {
        if (nome == null || nome.trim().isEmpty()) {
            throw new IllegalArgumentException("O nome do administrador não pode ser vazio!");
        }

        if (login == null || login.trim().isEmpty()) {
            throw new IllegalArgumentException("O login não pode ser vazio!");
        }

        UsuarioAdministrador admin = new UsuarioAdministrador();
        admin.setNome(nome);
        admin.setLogin(login);
        admin.setSenha(senha);
        usuarioDAO.salvarUsuario(admin);
    }

    public void cadastrarParticipante(String nome, String login, String senha) {
        if (nome == null || nome.trim().isEmpty()) {
            throw new IllegalArgumentException("O nome do participante não pode ser vazio!");
        }

        if (login == null || login.trim().isEmpty()) {
            throw new IllegalArgumentException("O login não pode ser vazio!");
        }

        if (usuarioDAO.encontrarPorLogin(login) != null) {
            throw new IllegalArgumentException("Já existe um usuário com esse login!");
        }

        UsuarioParticipante p = new UsuarioParticipante();
        p.setNome(nome);
        p.setLogin(login);
        p.setSenha(senha);
        usuarioDAO.salvarUsuario(p);
    }

    public void vincularAoGrupo(Long idParticipante, Grupo grupo) {
        if (idParticipante == null) {
            throw new IllegalArgumentException("O Id do participante não pode ser nulo!");
        }

        if (grupo == null) {
            throw new IllegalArgumentException("O grupo não pode ser nulo!");
        }

        Usuario u = usuarioDAO.encontrarUsuarioPorId(idParticipante);
        if (!(u instanceof UsuarioParticipante)) {
            throw new IllegalArgumentException("Usuario invalido ou não é um participante!");
        }

        UsuarioParticipante p = (UsuarioParticipante) u;

        if (p.participaDoGrupo(grupo.getIdGrupo())) {
            throw new LimiteCadastroException("Você já participa desse grupo!");
        }

        if (usuarioDAO.listarParticipantesPorGrupo(grupo.getIdGrupo()).size() >= 5) {
            throw new LimiteCadastroException("Limite de 5 participantes por grupo atingido!");
        }

        p.getGrupos().add(grupo);
        usuarioDAO.atualizarUsuario(p);
    }

    public void atualizarParticipante(Long id, String nome, String login, String senha) {
        if (id == null) {
            throw new IllegalArgumentException("O Id não pode ser nulo!");
        }
        if (nome == null || nome.trim().isEmpty()) {
            throw new IllegalArgumentException("O nome do participante não pode ser vazio!");
        }
        if (login == null || login.trim().isEmpty()) {
            throw new IllegalArgumentException("O login não pode ser vazio!");
        }

        Usuario u = usuarioDAO.encontrarUsuarioPorId(id);
        if (!(u instanceof UsuarioParticipante)) {
            throw new IllegalArgumentException("Participante não encontrado!");
        }
        UsuarioParticipante p = (UsuarioParticipante) u;

        Usuario outro = usuarioDAO.encontrarPorLogin(login);
        if (outro != null && !outro.getId().equals(id)) {
            throw new IllegalArgumentException("Já existe outro usuário com esse login!");
        }

        p.setNome(nome);
        p.setLogin(login);
        if (senha != null && !senha.isEmpty()) {
            p.setSenha(senha);
        }
        usuarioDAO.atualizarUsuario(p);
    }

    public UsuarioParticipante buscarParticipantePorId(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("O Id não pode ser nulo!");
        }

        Usuario u = usuarioDAO.encontrarUsuarioPorId(id);
        if (u instanceof UsuarioParticipante) {
            return (UsuarioParticipante) u;
        }
        return null;
    }

    public void removerParticipante(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("O Id não pode ser nulo!");
        }
        usuarioDAO.removerUsuarioPorId(id);
    }

    public List<UsuarioParticipante> listarParticipantes() {
        return usuarioDAO.listarParticipantes();
    }

    public List<UsuarioParticipante> listarParticipantesPorGrupo(Long idGrupo) {
        if (idGrupo == null) {
            throw new IllegalArgumentException("O Id do grupo não pode ser nulo!");
        }
        return usuarioDAO.listarParticipantesPorGrupo(idGrupo);
    }
}