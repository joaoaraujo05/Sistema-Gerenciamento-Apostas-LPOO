package br.com.sistema.model.entities;

import br.com.sistema.model.exceptions.LimiteCadastroException;
import br.com.sistema.model.exceptions.PersistenciaException;
import jakarta.persistence.*;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "campeonatos")
public class Campeonato implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idCampeonato;

    @Column(name = "nomeCampeonato")
    private String nomeCampeonato;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable (
            name = "campeonato_time",
            joinColumns = @JoinColumn(name = "campeonato_id"),
            inverseJoinColumns = @JoinColumn(name = "time_id")
    )
    private List<Time> times = new ArrayList<>();

    public Campeonato() {}

    public Campeonato(Long idCampeonato, String nomeCampeonato) {
        this.idCampeonato = idCampeonato;
        this.nomeCampeonato = nomeCampeonato;
    }

    public Long getIdCampeonato() {
        return idCampeonato;
    }

    public void setIdCampeonato(Long idCampeonato) {
        this.idCampeonato = idCampeonato;
    }

    public String getNomeCampeonato() {
        return nomeCampeonato;
    }

    public void setNomeCampeonato(String nomeCampeonato) {
        this.nomeCampeonato = nomeCampeonato;
    }

    public void adicionarTime(Time t) {
        if (t == null) {
            throw new IllegalArgumentException("O time nao pode ser nulo!");
        }

        if (times.size() > 8) {
            throw new LimiteCadastroException("Limite de times excedido!");
        }

        if (times.contains(t)) {
            throw new PersistenciaException("O time " + t.getNomeTime() + " ja está nesse campeonato!");
        }

        times.add(t);
    }

    public void removerTime(Time t) {
        if (t == null) {
            throw new IllegalArgumentException("O time nao pode ser nulo!");
        }

        times.remove(t);
    }

    public boolean timeCompleto() {
        return times.size() == 8;
    }

    public List<Time> getTimes() {
        return times;
    }

    @Override
    public String toString() {
        return nomeCampeonato;
    }
}