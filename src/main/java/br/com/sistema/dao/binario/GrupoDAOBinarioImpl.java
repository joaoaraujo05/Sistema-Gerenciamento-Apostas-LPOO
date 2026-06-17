package br.com.sistema.dao.binario;

import br.com.sistema.dao.IGrupoDAO;
import br.com.sistema.model.entities.Grupo;
import br.com.sistema.model.exceptions.PersistenciaException;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class GrupoDAOBinarioImpl implements IGrupoDAO {

    private static final String FILE_PATH = "./dados/grupos.bin";

    private List<Grupo> grupos;

    public GrupoDAOBinarioImpl() {
        carregarArquivo();
    }

    @SuppressWarnings("unchecked")
    public void carregarArquivo() {
        File arquivo = new File(FILE_PATH);

        if (arquivo.exists()) {
            try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(arquivo))) {
                grupos = (List<Grupo>) ois.readObject();
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
                grupos = new ArrayList<>();
            }
        } else {
            grupos = new ArrayList<>();
        }
    }

    private void salvarArquivo() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(FILE_PATH))) {
            oos.writeObject(grupos);
        } catch (IOException e) {
            throw new PersistenciaException("Erro ao gravar no arquivo binario: " + e.getMessage());
        }
    }

    private Long gerarProximoId() {
        return grupos.stream()
                .mapToLong(g -> (g.getIdGrupo() != null) ? g.getIdGrupo() : 0)
                .max()
                .orElse(0L) + 1;
    }

    @Override
    public void salvarGrupo(Grupo g) {
        carregarArquivo();
        g.setIdGrupo(gerarProximoId());
        grupos.add(g);
        salvarArquivo();
    }

    @Override
    public void atualizarGrupo(Grupo g) {
        carregarArquivo();
        for (int i = 0; i < grupos.size(); i++) {
            if (grupos.get(i).getIdGrupo().equals(g.getIdGrupo())) {
                grupos.set(i, g);
                salvarArquivo();
                break;
            }
        }
    }

    @Override
    public void removerGrupoPorId(Long id) {
        carregarArquivo();
        grupos.removeIf(g -> g.getIdGrupo().equals(id));
        salvarArquivo();
    }

    @Override
    public Grupo encontrarGrupoPorId(Long id) {
        carregarArquivo();
        Grupo g = null;

        for (Grupo grupo : grupos) {
            if (grupo.getIdGrupo().equals(id)) {
                g = grupo;
                break;
            }
        }

        return g;
    }

    @Override
    public List<Grupo> listarGrupos() {
        carregarArquivo();
        return new ArrayList<>(grupos);
    }
}