package br.com.sistema.dao.binario;

import br.com.sistema.dao.IPartidaDAO;
import br.com.sistema.model.entities.Partida;
import br.com.sistema.model.exceptions.PersistenciaException;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class PartidaDAOBinarioImpl implements IPartidaDAO {

    private static final String FILE_PATH = "./dados/partidas.bin";

    private List<Partida> partidas;

    public PartidaDAOBinarioImpl() {
        carregarArquivo();
    }

    @SuppressWarnings("unchecked")
    public void carregarArquivo() {
        File arquivo = new File(FILE_PATH);

        if (arquivo.exists()) {
            try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(arquivo))) {
                partidas = (List<Partida>) ois.readObject();
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
                partidas = new ArrayList<>();
            }
        } else {
            partidas = new ArrayList<>();
        }
    }

    private void salvarArquivo() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(FILE_PATH))) {
            oos.writeObject(partidas);
        } catch (IOException e) {
            throw new PersistenciaException("Erro ao gravar no arquivo binario: " + e.getMessage());
        }
    }

    private Long gerarProximoId() {
        return partidas.stream()
                .mapToLong(p -> (p.getIdPartida() != null) ? p.getIdPartida() : 0)
                .max()
                .orElse(0L) + 1;
    }

    @Override
    public void salvarPartida(Partida p) {
        carregarArquivo();
        p.setIdPartida(gerarProximoId());
        partidas.add(p);
        salvarArquivo();
    }

    @Override
    public void atualizarPartida(Partida p) {
        carregarArquivo();
        for (int i = 0; i < partidas.size(); i++) {
            if (partidas.get(i).getIdPartida().equals(p.getIdPartida())) {
                partidas.set(i, p);
                salvarArquivo();
                break;
            }
        }
    }

    @Override
    public void removerPartidaPorId(Long id) {
        carregarArquivo();
        partidas.removeIf(p -> p.getIdPartida().equals(id));
        salvarArquivo();
    }

    @Override
    public Partida encontrarPartidaPorId(Long id) {
        carregarArquivo();
        Partida p = null;

        for (Partida partida : partidas) {
            if (partida.getIdPartida().equals(id)) {
                p = partida;
                break;
            }
        }

        return p;
    }

    @Override
    public List<Partida> listarPartidas() {
        carregarArquivo();
        return new ArrayList<>(partidas);
    }
}