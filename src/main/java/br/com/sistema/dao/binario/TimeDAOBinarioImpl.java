package br.com.sistema.dao.binario;

import br.com.sistema.dao.ITimeDAO;
import br.com.sistema.model.entities.Time;
import br.com.sistema.model.exceptions.PersistenciaException;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class TimeDAOBinarioImpl implements ITimeDAO {

    private static final String FILE_PATH = "./dados/times.bin";

    private List<Time> times;

    public TimeDAOBinarioImpl() {
        carregarArquivo();
    }

    @SuppressWarnings("unchecked")
    public void carregarArquivo() {
        File arquivo = new File(FILE_PATH);

        if (arquivo.exists()) {
            try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(arquivo))) {
                times = (List<Time>) ois.readObject();
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
                times = new ArrayList<>();
            }
        } else {
            times = new ArrayList<>();
        }
    }

    private void salvarArquivo() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(FILE_PATH))){
            oos.writeObject(times);
        } catch (IOException e) {
            throw new PersistenciaException("Erro ao gravar no arquivo bianrio: " + e.getMessage());
        }
    }

    private Long gerarProximoId() {
        return times.stream()
                .mapToLong(t -> (t.getIdTime() != null) ? t.getIdTime() : 0)
                .max()
                .orElse(0L) + 1;
    }

    @Override
    public void salvarTime(Time t) {
        t.setIdTime(gerarProximoId());
        times.add(t);
        salvarArquivo();
    }

    @Override
    public void atualizarTime(Time t) {
        for (int i=0; i < times.size(); i++) {
            if (times.get(i).getIdTime().equals(t.getIdTime())) {
                times.set(i,t);
                salvarArquivo();
                break;
            }
        }
    }

    @Override
    public void removerTimePorId(Long id) {
        times.removeIf(p-> p.getIdTime().equals(id));
        salvarArquivo();
    }

    @Override
    public Time encontrarTimePorId(Long id) {
        Time t = null;

        for (Time time : times) {
            if (time.getIdTime().equals(id)) {
                t = time;
                break;
            }
        }

        return t;
    }

    @Override
    public List<Time> listarTimes() {
        return new ArrayList<>(times);
    }
}
