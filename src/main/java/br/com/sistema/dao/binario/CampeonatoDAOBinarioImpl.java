package br.com.sistema.dao.binario;

import br.com.sistema.dao.ICampeonatoDAO;
import br.com.sistema.model.entities.Campeonato;
import br.com.sistema.model.exceptions.PersistenciaException;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class CampeonatoDAOBinarioImpl implements ICampeonatoDAO {

    private static final String FILE_PATH = "./dados/campeonatos.bin";

    private List<Campeonato> campeonatos;

    public CampeonatoDAOBinarioImpl() {
        carregarArquivo();
    }

    @SuppressWarnings("unchecked")
    public void carregarArquivo() {
        File arquivo = new File(FILE_PATH);

        if (arquivo.exists()) {
            try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(arquivo))) {
                campeonatos = (List<Campeonato>) ois.readObject();
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
                campeonatos = new ArrayList<>();
            }
        } else {
            campeonatos = new ArrayList<>();
        }
    }

    private void salvarArquivo() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(FILE_PATH))) {
            oos.writeObject(campeonatos);
        } catch (IOException e) {
            throw new PersistenciaException("Erro ao gravar no arquivo binario: " + e.getMessage());
        }
    }

    private Long gerarProximoId() {
        return campeonatos.stream()
                .mapToLong(c -> (c.getIdCampeonato() != null) ? c.getIdCampeonato() : 0)
                .max()
                .orElse(0L) + 1;
    }

    @Override
    public void salvarCampeonato(Campeonato c) {
        c.setIdCampeonato(gerarProximoId());
        campeonatos.add(c);
        salvarArquivo();
    }

    @Override
    public void atualizarCampeonato(Campeonato c) {
        for (int i = 0; i < campeonatos.size(); i++) {
            if (campeonatos.get(i).getIdCampeonato().equals(c.getIdCampeonato())) {
                campeonatos.set(i, c);
                salvarArquivo();
                break;
            }
        }
    }

    @Override
    public void removerCampeonatoPorId(Long id) {
        campeonatos.removeIf(c -> c.getIdCampeonato().equals(id));
        salvarArquivo();
    }

    @Override
    public Campeonato encontrarCampeonatoPorId(Long id) {
        Campeonato c = null;

        for (Campeonato campeonato : campeonatos) {
            if (campeonato.getIdCampeonato().equals(id)) {
                c = campeonato;
                break;
            }
        }

        return c;
    }

    @Override
    public List<Campeonato> listarCampeonatos() {
        return new ArrayList<>(campeonatos);
    }
}