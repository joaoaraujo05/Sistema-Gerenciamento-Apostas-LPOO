package br.com.sistema.model.entities;

import br.com.sistema.model.exceptions.ApostaInvalidaException;
import jakarta.persistence.*;

import java.io.Serializable;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Entity
@Table(name = "partidas")
public class Partida implements Serializable {

    private static final long serialVersionUID = 1L;

    private static final DateTimeFormatter FORMATO = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idPartida;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "campeonato_id")
    private Campeonato campeonato;

    @ManyToOne
    @JoinColumn(name = "mandante_id")
    private Time clubeMandante;

    @ManyToOne
    @JoinColumn(name = "visitante_id")
    private Time clubeVisitante;

    @Column(name = "dataHora", nullable = false)
    private LocalDateTime dataHora;

    @Column(name = "golsMandante")
    private int golsMandante;

    @Column(name = "golsVisitante")
    private int golsVisitante;

    @Column(name = "finalizada")
    private boolean finalizada;

    public Partida() {}

    public Partida(Long idPartida, Campeonato campeonato, Time clubeMandante, Time clubeVisitante, LocalDateTime dataHora) {
        if (campeonato == null) {
            throw new IllegalArgumentException("A partida precisa estar associada a um campeonato!");
        }

        if (clubeMandante == null || clubeVisitante == null) {
            throw new IllegalArgumentException("Os clubes da partida nao podem ser nulos!");
        }

        if (clubeMandante.equals(clubeVisitante)) {
            throw new IllegalArgumentException("Um clube nao pode jogar contra ele mesmo!");
        }

        if (dataHora == null) {
            throw new IllegalArgumentException("A data e o horario da partida sao obrigatorios!");
        }

        this.idPartida = idPartida;
        this.campeonato = campeonato;
        this.clubeMandante = clubeMandante;
        this.clubeVisitante = clubeVisitante;
        this.dataHora = dataHora;
        this.finalizada = false;
    }

    public boolean permiteAposta() {
        long minutosRestantes = Duration.between(LocalDateTime.now(), dataHora).toMinutes();
        return minutosRestantes >= 20;
    }

    public void validarPrazoParaAposta() {
        if (!permiteAposta()) {
            throw new ApostaInvalidaException("As apostas para essa partida ja foram encerradas!");
        }
    }

    public void registrarResultado(int golsMandante, int golsVisitante) {
        if (golsMandante < 0 || golsVisitante < 0) {
            throw new IllegalArgumentException("O numero de gols nao pode ser negativo!");
        }

        this.golsMandante = golsMandante;
        this.golsVisitante = golsVisitante;
        this.finalizada = true;
    }

    public String resultadoReal() {
        if (golsMandante > golsVisitante) {
            return "MANDANTE";
        } else if (golsMandante < golsVisitante) {
            return "VISITANTE";
        } else {
            return "EMPATE";
        }
    }

    public Long getIdPartida() {
        return idPartida;
    }

    public void setIdPartida(Long idPartida) {
        this.idPartida = idPartida;
    }

    public Campeonato getCampeonato() {
        return campeonato;
    }

    public void setCampeonato(Campeonato campeonato) {
        this.campeonato = campeonato;
    }

    public Time getClubeMandante() {
        return clubeMandante;
    }

    public void setClubeMandante(Time clubeMandante) {
        this.clubeMandante = clubeMandante;
    }

    public Time getClubeVisitante() {
        return clubeVisitante;
    }

    public void setClubeVisitante(Time clubeVisitante) {
        this.clubeVisitante = clubeVisitante;
    }

    public LocalDateTime getDataHora() {
        return dataHora;
    }

    public void setDataHora(LocalDateTime dataHora) {
        this.dataHora = dataHora;
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

    public boolean isFinalizada() {
        return finalizada;
    }

    public void setFinalizada(boolean finalizada) {
        this.finalizada = finalizada;
    }

    @Override
    public String toString() {
        String nomeMandante = (clubeMandante != null) ? clubeMandante.getNomeTime() : "?";
        String nomeVisitante = (clubeVisitante != null) ? clubeVisitante.getNomeTime() : "?";
        String nomeCampeonato = (campeonato != null) ? campeonato.getNomeCampeonato() : "Sem campeonato";
        String quando = (dataHora != null) ? dataHora.format(FORMATO) : "?";
        return nomeCampeonato + ": " + nomeMandante + " x " + nomeVisitante + " (" + quando + ")";
    }
}