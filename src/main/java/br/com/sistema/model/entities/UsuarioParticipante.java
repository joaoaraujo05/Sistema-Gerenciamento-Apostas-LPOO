package br.com.sistema.model.entities;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@DiscriminatorValue("PARTICIPANTE")
public class UsuarioParticipante extends Usuario{

    @Column(name = "pontuacao_total")
    private int pontuacaoTotal;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "participante_grupo",
            joinColumns = @JoinColumn(name = "participante_id"),
            inverseJoinColumns = @JoinColumn(name = "grupo_id")
    )
    private List<Grupo> grupos = new ArrayList<>();

    @OneToMany(mappedBy = "participante", cascade = CascadeType.ALL)
    List<Aposta> apostas = new ArrayList<>();

    public UsuarioParticipante(){}

    public UsuarioParticipante(int pontuacaoTotal, List<Grupo> grupos, List<Aposta> apostas) {
        this.pontuacaoTotal = pontuacaoTotal;
        this.grupos = grupos;
        this.apostas = apostas;
    }

    public UsuarioParticipante(Long id, String nome, String login, String senha, int pontuacaoTotal, List<Grupo> grupos, List<Aposta> apostas) {
        super(id, nome, login, senha);
        this.pontuacaoTotal = pontuacaoTotal;
        this.grupos = grupos;
        this.apostas = apostas;
    }

    @Override
    public boolean autenticar(String login, String senha) {
        if (this.getLogin().equals(login) && this.getSenha().equals(senha)) return true;
        return false;
    }

    @Override
    public String obterPerfil() {
        return "PARTICIPANTE";
    }

    public int calcularPontos() {
        pontuacaoTotal = 0;
        for (Aposta a : apostas) {
            pontuacaoTotal += a.getPontos();
        }
        return pontuacaoTotal;
    }

    public void adicionarAposta(Aposta a) {
        if (a == null) {
            throw new IllegalArgumentException("A aposta nao pode ser nula!");
        }
        apostas.add(a);
        a.setParticipante(this);
    }

    public boolean participaDoGrupo(Long idGrupo) {
        for (Grupo g : grupos) {
            if (g.getIdGrupo() != null && g.getIdGrupo().equals(idGrupo)) {
                return true;
            }
        }
        return false;
    }

    public int getPontuacaoTotal() {
        return pontuacaoTotal;
    }

    public void setPontuacaoTotal(int pontuacaoTotal) {
        this.pontuacaoTotal = pontuacaoTotal;
    }

    public List<Grupo> getGrupos() {
        return grupos;
    }

    public void setGrupos(List<Grupo> grupos) {
        this.grupos = grupos;
    }

    public List<Aposta> getApostas() {
        return apostas;
    }

    public void setApostas(List<Aposta> apostas) {
        this.apostas = apostas;
    }

    @Override
    public String toString() {
        return getId() + ": " + getNome();
    }
}