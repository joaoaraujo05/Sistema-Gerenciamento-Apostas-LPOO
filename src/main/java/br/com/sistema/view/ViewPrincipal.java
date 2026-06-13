package br.com.sistema.view;

import br.com.sistema.controller.ApostaController;
import br.com.sistema.controller.CampeonatoController;
import br.com.sistema.controller.GrupoController;
import br.com.sistema.controller.PartidaController;
import br.com.sistema.controller.TimeController;
import br.com.sistema.controller.UsuarioController;
import br.com.sistema.model.entities.Usuario;
import br.com.sistema.model.entities.UsuarioParticipante;

import javax.swing.*;
import java.awt.*;

public class ViewPrincipal extends JFrame {

    private Usuario usuarioLogado;

    public ViewPrincipal(Usuario usuarioLogado) {
        this.usuarioLogado = usuarioLogado;

        setTitle("[UNAERP] Sistema de Apostas");
        setSize(1024, 720);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panelTopo = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        panelTopo.add(new JLabel("Logado como: " + usuarioLogado.getNome()));
        JButton btnSair = new JButton("Voltar ao Login");
        btnSair.addActionListener(e -> voltarAoLogin());
        panelTopo.add(btnSair);

        JTabbedPane tabbedPane = new JTabbedPane();

        if (usuarioLogado.obterPerfil().equals("ADMINISTRADOR")) {
            montarTelasAdmin(tabbedPane);
        } else {
            montarTelasParticipante(tabbedPane);
        }

        add(panelTopo, BorderLayout.NORTH);
        add(tabbedPane, BorderLayout.CENTER);
    }

    private void voltarAoLogin() {
        LoginView login = new LoginView();
        login.setVisible(true);
        this.dispose();
    }

    private void montarTelasAdmin(JTabbedPane tabbedPane) {
        TimeController timeController = new TimeController();
        CampeonatoController campeonatoController = new CampeonatoController();
        UsuarioController usuarioController = new UsuarioController();
        GrupoController grupoController = new GrupoController();
        PartidaController partidaController = new PartidaController();

        TimeView timeView = new TimeView(timeController);
        CampeonatoView campeonatoView = new CampeonatoView(campeonatoController, timeController);
        ParticipanteView participanteView = new ParticipanteView(usuarioController);
        GrupoView grupoView = new GrupoView(grupoController);
        PartidaView partidaView = new PartidaView(partidaController, timeController, campeonatoController);

        tabbedPane.addTab("Times", timeView);
        tabbedPane.addTab("Campeonatos", campeonatoView);
        tabbedPane.addTab("Participantes", participanteView);
        tabbedPane.addTab("Grupos", grupoView);
        tabbedPane.addTab("Partidas", partidaView);
    }

    private void montarTelasParticipante(JTabbedPane tabbedPane) {
        UsuarioController usuarioController = new UsuarioController();
        GrupoController grupoController = new GrupoController();
        PartidaController partidaController = new PartidaController();
        ApostaController apostaController = new ApostaController();

        UsuarioParticipante participante = (UsuarioParticipante) usuarioLogado;

        GrupoParticipanteView grupoView = new GrupoParticipanteView(grupoController, usuarioController, participante);
        ApostaView apostaView = new ApostaView(apostaController, partidaController, participante);
        ClassificacaoView classificacaoView = new ClassificacaoView(grupoController, usuarioController, apostaController);

        tabbedPane.addTab("Grupos", grupoView);
        tabbedPane.addTab("Apostas", apostaView);
        tabbedPane.addTab("Classificação", classificacaoView);
    }
}