package br.com.sistema.dao;

import br.com.sistema.model.entities.Usuario;
import br.com.sistema.model.entities.UsuarioParticipante;

import java.util.List;

public interface IUsuarioDAO {
    void salvarUsuario(Usuario u);
    void atualizarUsuario(Usuario u);
    void removerUsuarioPorId(Long id);
    Usuario encontrarUsuarioPorId(Long id);
    Usuario encontrarPorLogin(String login);
    List<UsuarioParticipante> listarParticipantes();
    List<UsuarioParticipante> listarParticipantesPorGrupo(Long idGrupo);
}