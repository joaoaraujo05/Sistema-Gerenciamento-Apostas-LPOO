package br.com.sistema.dao.binario;

import br.com.sistema.dao.IUsuarioDAO;
import br.com.sistema.model.entities.Grupo;
import br.com.sistema.model.entities.Usuario;
import br.com.sistema.model.entities.UsuarioParticipante;
import br.com.sistema.model.exceptions.PersistenciaException;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class UsuarioDAOBinarioImpl implements IUsuarioDAO {

    private static final String FILE_PATH = "./dados/usuarios.bin";

    private List<Usuario> usuarios;

    public UsuarioDAOBinarioImpl() {
        carregarArquivo();
    }

    @SuppressWarnings("unchecked")
    public void carregarArquivo() {
        File arquivo = new File(FILE_PATH);

        if (arquivo.exists()) {
            try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(arquivo))) {
                usuarios = (List<Usuario>) ois.readObject();
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
                usuarios = new ArrayList<>();
            }
        } else {
            usuarios = new ArrayList<>();
        }
    }

    private void salvarArquivo() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(FILE_PATH))) {
            oos.writeObject(usuarios);
        } catch (IOException e) {
            throw new PersistenciaException("Erro ao gravar no arquivo binario: " + e.getMessage());
        }
    }

    private Long gerarProximoId() {
        return usuarios.stream()
                .mapToLong(u -> (u.getId() != null) ? u.getId() : 0)
                .max()
                .orElse(0L) + 1;
    }

    @Override
    public void salvarUsuario(Usuario u) {
        carregarArquivo();
        u.setId(gerarProximoId());
        usuarios.add(u);
        salvarArquivo();
    }

    @Override
    public void atualizarUsuario(Usuario u) {
        carregarArquivo();
        for (int i = 0; i < usuarios.size(); i++) {
            if (usuarios.get(i).getId().equals(u.getId())) {
                usuarios.set(i, u);
                salvarArquivo();
                break;
            }
        }
    }

    @Override
    public void removerUsuarioPorId(Long id) {
        carregarArquivo();
        usuarios.removeIf(u -> u.getId().equals(id));
        salvarArquivo();
    }

    @Override
    public Usuario encontrarUsuarioPorId(Long id) {
        carregarArquivo();
        Usuario u = null;

        for (Usuario usuario : usuarios) {
            if (usuario.getId().equals(id)) {
                u = usuario;
                break;
            }
        }

        return u;
    }

    @Override
    public Usuario encontrarPorLogin(String login) {
        carregarArquivo();
        Usuario u = null;

        for (Usuario usuario : usuarios) {
            if (usuario.getLogin() != null && usuario.getLogin().equals(login)) {
                u = usuario;
                break;
            }
        }

        return u;
    }

    @Override
    public List<UsuarioParticipante> listarParticipantes() {
        carregarArquivo();
        List<UsuarioParticipante> participantes = new ArrayList<>();

        for (Usuario u : usuarios) {
            if (u instanceof UsuarioParticipante) {
                participantes.add((UsuarioParticipante) u);
            }
        }

        return participantes;
    }

    @Override
    public List<UsuarioParticipante> listarParticipantesPorGrupo(Long idGrupo) {
        carregarArquivo();
        List<UsuarioParticipante> resultado = new ArrayList<>();

        for (Usuario u : usuarios) {
            if (u instanceof UsuarioParticipante) {
                UsuarioParticipante p = (UsuarioParticipante) u;
                for (Grupo g : p.getGrupos()) {
                    if (g.getIdGrupo() != null && g.getIdGrupo().equals(idGrupo)) {
                        resultado.add(p);
                        break;
                    }
                }
            }
        }

        return resultado;
    }
}