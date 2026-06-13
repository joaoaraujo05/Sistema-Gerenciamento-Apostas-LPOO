package br.com.sistema.model.entities;

import br.com.sistema.model.exceptions.ApostaInvalidaException;
import jakarta.persistence.*;

import java.io.Serializable;

@Entity
@Table(name = "apostas")
public class Aposta implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idAposta;

    @ManyToOne
    @JoinColumn(name = "participante_id")
    private UsuarioParticipante participante;

    @ManyToOne
    @JoinColumn(name = "partida_id")
    private Partida partida;

    @Column(name = "resultadoEsperado")
    private String resultadoEsperado;

    @Column(name = "golsMandante")
    private int golsMandante;

    @Column(name = "golsVisitante")
    private int golsVisitante;

    @Column(name = "pontos")
    private int pontos;

    public Aposta() {}

    public Aposta(UsuarioParticipante participante, Partida partida, String resultadoEsperado, int golsMandante, int golsVisitante) {
        if (participante == null) {
            throw new IllegalArgumentException("A aposta precisa de um participante!");
        }

        if (partida == null) {
            throw new IllegalArgumentException("A aposta precisa de uma partida!");
        }

        this.participante = participante;
        this.partida = partida;
        this.resultadoEsperado = resultadoEsperado;
        this.golsMandante = golsMandante;
        this.golsVisitante = golsVisitante;
        this.pontos = 0;

        validarResultadoEPlacar();
    }

    public void validarResultadoEPlacar() {
        if (golsMandante < 0 || golsVisitante < 0) {
            throw new ApostaInvalidaException("O placar previsto nao pode ter gols negativos!");
        }

        if (resultadoEsperado == null) {
            throw new ApostaInvalidaException("Voce precisa informar o resultado esperado!");
        }

        if (!resultadoEsperado.equals("MANDANTE")
                && !resultadoEsperado.equals("VISITANTE")
                && !resultadoEsperado.equals("EMPATE")) {
            throw new ApostaInvalidaException("Resultado esperado invalido!");
        }

        String resultadoDoPlacar = resultadoDoPlacarPrevisto();

        if (!resultadoEsperado.equals(resultadoDoPlacar)) {
            throw new ApostaInvalidaException("Aposta incoerente: o resultado escolhido (" + resultadoEsperado
                    + ") nao bate com o placar previsto (" + golsMandante + " x " + golsVisitante + ")!");
        }
    }

    private String resultadoDoPlacarPrevisto() {
        if (golsMandante > golsVisitante) {
            return "MANDANTE";
        } else if (golsMandante < golsVisitante) {
            return "VISITANTE";
        } else {
            return "EMPATE";
        }
    }

    public int calcularPontos() {
        if (partida == null || !partida.isFinalizada()) {
            this.pontos = 0;
            return pontos;
        }

        boolean acertouResultado = resultadoEsperado.equals(partida.resultadoReal());
        boolean acertouPlacar = (golsMandante == partida.getGolsMandante())
                && (golsVisitante == partida.getGolsVisitante());

        if (acertouResultado && acertouPlacar) {
            this.pontos = 10;
        } else if (acertouResultado) {
            this.pontos = 5;
        } else {
            this.pontos = 0;
        }

        return pontos;
    }

    public Long getIdAposta() {
        return idAposta;
    }

    public void setIdAposta(Long idAposta) {
        this.idAposta = idAposta;
    }

    public UsuarioParticipante getParticipante() {
        return participante;
    }

    public void setParticipante(UsuarioParticipante participante) {
        this.participante = participante;
    }

    public Partida getPartida() {
        return partida;
    }

    public void setPartida(Partida partida) {
        this.partida = partida;
    }

    public String getResultadoEsperado() {
        return resultadoEsperado;
    }

    public void setResultadoEsperado(String resultadoEsperado) {
        this.resultadoEsperado = resultadoEsperado;
    }

    public int getGolsMandante() {
        return golsMandante;
    }

    public void setGolsMandante(int golsMandante) {
        this.golsMandante = golsMandante;
    }

    public int getGolsVisitante() {
        return golsVisitante;
    }

    public void setGolsVisitante(int golsVisitante) {
        this.golsVisitante = golsVisitante;
    }

    public int getPontos() {
        return pontos;
    }

    public void setPontos(int pontos) {
        this.pontos = pontos;
    }

    @Override
    public String toString() {
        return idAposta + ": " + resultadoEsperado + " (" + golsMandante + " x " + golsVisitante + ") - " + pontos + " pts";
    }
}