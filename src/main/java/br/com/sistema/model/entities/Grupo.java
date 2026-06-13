package br.com.sistema.model.entities;

import br.com.sistema.model.exceptions.LimiteCadastroException;
import br.com.sistema.model.exceptions.PersistenciaException;
import jakarta.persistence.*;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "grupos")
public class Grupo implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idGrupo;

    @Column(name = "nomeGrupo", nullable = false)
    private String nomeGrupo;

    @ManyToMany(mappedBy = "grupos")
    private List<UsuarioParticipante> participantes = new ArrayList<>();

    public Grupo() {}

    public Grupo(Long idGrupo, String nomeGrupo) {
        this.idGrupo = idGrupo;
        this.nomeGrupo = nomeGrupo;
    }

    public void adicionarParticipante(UsuarioParticipante p) {
        if (p == null) {
            throw new IllegalArgumentException("O participante nao pode ser nulo!");
        }

        if (participantes.size() >= 5) {
            throw new LimiteCadastroException("Limite de 5 participantes por grupo atingido!");
        }

        if (participantes.contains(p)) {
            throw new PersistenciaException("O participante " + p.getNome() + " ja esta nesse grupo!");
        }

        participantes.add(p);
        p.getGrupos().add(this);
    }

    public void removerParticipante(UsuarioParticipante p) {
        if (p == null) {
            throw new IllegalArgumentException("O participante nao pode ser nulo!");
        }

        participantes.remove(p);
        p.getGrupos().remove(this);
    }

    public boolean grupoCompleto() {
        return participantes.size() == 5;
    }

    public Long getIdGrupo() {
        return idGrupo;
    }

    public void setIdGrupo(Long idGrupo) {
        this.idGrupo = idGrupo;
    }

    public String getNomeGrupo() {
        return nomeGrupo;
    }

    public void setNomeGrupo(String nomeGrupo) {
        this.nomeGrupo = nomeGrupo;
    }

    public List<UsuarioParticipante> getParticipantes() {
        return participantes;
    }

    public void setParticipantes(List<UsuarioParticipante> participantes) {
        this.participantes = participantes;
    }

    @Override
    public String toString() {
        return idGrupo + ": " + nomeGrupo;
    }
}