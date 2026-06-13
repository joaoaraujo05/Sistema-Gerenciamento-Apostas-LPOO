package br.com.sistema.view;

import br.com.sistema.controller.GrupoController;
import br.com.sistema.controller.UsuarioController;
import br.com.sistema.model.entities.Grupo;
import br.com.sistema.model.entities.UsuarioParticipante;
import br.com.sistema.model.exceptions.LimiteCadastroException;
import br.com.sistema.model.exceptions.PersistenciaException;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class GrupoParticipanteView extends JPanel {

    private GrupoController grupoController;
    private UsuarioController usuarioController;
    private UsuarioParticipante participanteLogado;

    private JLabel lblGrupoAtual;
    private JButton btnEntrar;
    private JButton btnAtualizar;

    private JTable tabela;
    private DefaultTableModel defaultTableModel;

    public GrupoParticipanteView(GrupoController grupoController, UsuarioController usuarioController, UsuarioParticipante participanteLogado) {
        this.grupoController = grupoController;
        this.usuarioController = usuarioController;
        this.participanteLogado = participanteLogado;
        setLayout(new BorderLayout());
        setSize(1024, 728);
        iniciarComponentes();
        carregarTabela();
        atualizarGrupoAtual();
    }

    public void iniciarComponentes() {
        JPanel panelMain = new JPanel(new BorderLayout(10, 10));
        panelMain.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel panelTopo = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panelTopo.setBorder(BorderFactory.createTitledBorder("Meu Grupo"));
        lblGrupoAtual = new JLabel("Meus grupos: (nenhum)");
        panelTopo.add(lblGrupoAtual);

        JPanel panelAcoes = new JPanel(new FlowLayout(FlowLayout.CENTER));
        btnEntrar = new JButton("Entrar no Grupo Selecionado");
        btnAtualizar = new JButton("Atualizar");
        panelAcoes.add(btnEntrar);
        panelAcoes.add(btnAtualizar);

        JPanel panelTabela = new JPanel(new BorderLayout());
        panelTabela.setBorder(BorderFactory.createTitledBorder("Grupos disponíveis"));

        defaultTableModel = new DefaultTableModel(new String[]{"ID", "Nome Grupo", "Participantes"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tabela = new JTable(defaultTableModel);
        JScrollPane scrollPane = new JScrollPane(tabela);
        panelTabela.add(scrollPane, BorderLayout.CENTER);

        JPanel panelSuperior = new JPanel(new BorderLayout());
        panelSuperior.add(panelTopo, BorderLayout.NORTH);
        panelSuperior.add(panelAcoes, BorderLayout.SOUTH);

        panelMain.add(panelSuperior, BorderLayout.NORTH);
        panelMain.add(panelTabela, BorderLayout.CENTER);

        add(panelMain, BorderLayout.CENTER);

        configurarAcoes();
    }

    public void configurarAcoes() {
        btnEntrar.addActionListener(e -> entrarNoGrupo());
        btnAtualizar.addActionListener(e -> {
            carregarTabela();
            atualizarGrupoAtual();
        });
    }

    private void carregarTabela() {
        defaultTableModel.setRowCount(0);
        List<Grupo> lista = grupoController.listarGrupos();
        for (Grupo g : lista) {
            int qtd = usuarioController.listarParticipantesPorGrupo(g.getIdGrupo()).size();
            defaultTableModel.addRow(new Object[]{g.getIdGrupo(), g.getNomeGrupo(), qtd + "/5"});
        }
    }

    private void atualizarGrupoAtual() {
        UsuarioParticipante atual = usuarioController.buscarParticipantePorId(participanteLogado.getId());
        if (atual != null) {
            participanteLogado = atual;
        }

        if (participanteLogado.getGrupos() == null || participanteLogado.getGrupos().isEmpty()) {
            lblGrupoAtual.setText("Meus grupos: (nenhum)");
        } else {
            StringBuilder sb = new StringBuilder();
            for (Grupo g : participanteLogado.getGrupos()) {
                if (sb.length() > 0) {
                    sb.append(", ");
                }
                sb.append(g.getNomeGrupo());
            }
            lblGrupoAtual.setText("Meus grupos: " + sb.toString());
        }
    }

    public void entrarNoGrupo() {
        int linha = tabela.getSelectedRow();
        if (linha == -1) {
            JOptionPane.showMessageDialog(this, "Selecione um grupo na tabela!", "Atenção", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Long idGrupo = (Long) defaultTableModel.getValueAt(linha, 0);

        try {
            Grupo grupo = grupoController.buscarGrupoPorId(idGrupo);
            usuarioController.vincularAoGrupo(participanteLogado.getId(), grupo);
            JOptionPane.showMessageDialog(this, "Você entrou no grupo " + grupo.getNomeGrupo() + "!");
            carregarTabela();
            atualizarGrupoAtual();
        } catch (LimiteCadastroException l) {
            JOptionPane.showMessageDialog(this, l.getMessage(), "Atenção", JOptionPane.WARNING_MESSAGE);
        } catch (IllegalArgumentException i) {
            JOptionPane.showMessageDialog(this, "Erro: " + i.getMessage());
        } catch (PersistenciaException p) {
            JOptionPane.showMessageDialog(this, "Erro de persistencia: " + p.getMessage());
        }
    }
}