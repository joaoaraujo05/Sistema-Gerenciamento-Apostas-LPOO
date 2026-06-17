package br.com.sistema.dao.binario;

import br.com.sistema.dao.IApostaDAO;
import br.com.sistema.model.entities.Aposta;
import br.com.sistema.model.exceptions.PersistenciaException;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class ApostaDAOBinarioImpl implements IApostaDAO {

    private static final String FILE_PATH = "./dados/apostas.bin";

    private List<Aposta> apostas;

    public ApostaDAOBinarioImpl() {
        carregarArquivo();
    }

    @SuppressWarnings("unchecked")
    public void carregarArquivo() {
        File arquivo = new File(FILE_PATH);

        if (arquivo.exists()) {
            try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(arquivo))) {
                apostas = (List<Aposta>) ois.readObject();
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
                apostas = new ArrayList<>();
            }
        } else {
            apostas = new ArrayList<>();
        }
    }

    private void salvarArquivo() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(FILE_PATH))) {
            oos.writeObject(apostas);
        } catch (IOException e) {
            throw new PersistenciaException("Erro ao gravar no arquivo binario: " + e.getMessage());
        }
    }

    private Long gerarProximoId() {
        return apostas.stream()
                .mapToLong(a -> (a.getIdAposta() != null) ? a.getIdAposta() : 0)
                .max()
                .orElse(0L) + 1;
    }

    @Override
    public void salvarAposta(Aposta a) {
        carregarArquivo();
        a.setIdAposta(gerarProximoId());
        apostas.add(a);
        salvarArquivo();
    }

    @Override
    public void atualizarAposta(Aposta a) {
        carregarArquivo();
        for (int i = 0; i < apostas.size(); i++) {
            if (apostas.get(i).getIdAposta().equals(a.getIdAposta())) {
                apostas.set(i, a);
                salvarArquivo();
                break;
            }
        }
    }

    @Override
    public void removerApostaPorId(Long id) {
        carregarArquivo();
        apostas.removeIf(a -> a.getIdAposta().equals(id));
        salvarArquivo();
    }

    @Override
    public Aposta encontrarApostaPorId(Long id) {
        carregarArquivo();
        Aposta a = null;

        for (Aposta aposta : apostas) {
            if (aposta.getIdAposta().equals(id)) {
                a = aposta;
                break;
            }
        }

        return a;
    }

    @Override
    public List<Aposta> listarApostas() {
        carregarArquivo();
        return new ArrayList<>(apostas);
    }

    @Override
    public List<Aposta> listarApostasPorPartida(Long idPartida) {
        carregarArquivo();
        List<Aposta> resultado = new ArrayList<>();

        for (Aposta a : apostas) {
            if (a.getPartida() != null && a.getPartida().getIdPartida().equals(idPartida)) {
                resultado.add(a);
            }
        }

        return resultado;
    }
}