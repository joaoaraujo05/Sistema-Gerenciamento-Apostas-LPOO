package br.com.sistema.model.entities;

import jakarta.persistence.*;

import java.io.Serializable;

@Entity
@Table(name = "times")
public class Time implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idTime;

    @Column(name = "nomeTime", nullable = false)
    private String nomeTime;

    public Time() {}

    public Time(Long idTime, String nomeTime) {
        this.idTime = idTime;
        this.nomeTime = nomeTime;
    }

    public Long getIdTime() {
        return idTime;
    }

    public void setIdTime(Long idTime) {
        this.idTime = idTime;
    }

    public String getNomeTime() {
        return nomeTime;
    }

    public void setNomeTime(String nomeTime) {
        this.nomeTime = nomeTime;
    }

    @Override
    public String toString() {
        return idTime + ": " + nomeTime;
    }
}
