package br.com.sistema.view;

import br.com.sistema.controller.ApostaController;
import br.com.sistema.controller.GrupoController;
import br.com.sistema.controller.UsuarioController;
import br.com.sistema.model.entities.Grupo;
import br.com.sistema.model.entities.UsuarioParticipante;
import br.com.sistema.model.exceptions.PersistenciaException;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class ClassificacaoView extends JPanel {

    private GrupoController grupoController;
    private UsuarioController usuarioController;
    private ApostaController apostaController;

    private JComboBox<Grupo> comboGrupos;
    private JButton btnVerClassificacao;
    private JButton btnAtualizar;

    private JTable tabela;
    private DefaultTableModel defaultTableModel;

    public ClassificacaoView(GrupoController grupoController, UsuarioController usuarioController, ApostaController apostaController) {
        this.grupoController = grupoController;
        this.usuarioController = usuarioController;
        this.apostaController = apostaController;
        setLayout(new BorderLayout());
        setSize(1024, 728);
        iniciarComponentes();
        carregarGrupos();
    }

    public void iniciarComponentes() {
        JPanel panelMain = new JPanel(new BorderLayout(10, 10));
        panelMain.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel panelTopo = new JPanel(new FlowLayout(FlowLayout.CENTER));
        panelTopo.setBorder(BorderFactory.createTitledBorder("Classificação do Grupo"));
        comboGrupos = new JComboBox<>();
        btnVerClassificacao = new JButton("Ver Classificação");
        btnAtualizar = new JButton("Atualizar Grupos");
        panelTopo.add(new JLabel("Grupo:"));
        panelTopo.add(comboGrupos);
        panelTopo.add(btnVerClassificacao);
        panelTopo.add(btnAtualizar);

        JPanel panelTabela = new JPanel(new BorderLayout());
        panelTabela.setBorder(BorderFactory.createTitledBorder("Pontuação"));

        defaultTableModel = new DefaultTableModel(new String[]{"Posição", "Participante", "Pontos"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tabela = new JTable(defaultTableModel);
        JScrollPane scrollPane = new JScrollPane(tabela);
        panelTabela.add(scrollPane, BorderLayout.CENTER);

        panelMain.add(panelTopo, BorderLayout.NORTH);
        panelMain.add(panelTabela, BorderLayout.CENTER);

        add(panelMain, BorderLayout.CENTER);

        configurarAcoes();
    }

    public void configurarAcoes() {
        btnVerClassificacao.addActionListener(e -> carregarClassificacao());
        btnAtualizar.addActionListener(e -> carregarGrupos());
    }

    private void carregarGrupos() {
        comboGrupos.removeAllItems();
        for (Grupo g : grupoController.listarGrupos()) {
            comboGrupos.addItem(g);
        }
    }

    private void carregarClassificacao() {
        Grupo grupo = (Grupo) comboGrupos.getSelectedItem();
        if (grupo == null) {
            JOptionPane.showMessageDialog(this, "Selecione um grupo!", "Atenção", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            List<UsuarioParticipante> participantes = usuarioController.listarParticipantesPorGrupo(grupo.getIdGrupo());

            List<UsuarioParticipante> ordenados = new ArrayList<>(participantes);
            List<Integer> pontosLista = new ArrayList<>();
            for (UsuarioParticipante p : ordenados) {
                pontosLista.add(apostaController.pontuacaoDoParticipante(p.getId()));
            }

            for (int i = 0; i < ordenados.size() - 1; i++) {
                for (int j = 0; j < ordenados.size() - 1 - i; j++) {
                    if (pontosLista.get(j) < pontosLista.get(j + 1)) {
                        Integer pTemp = pontosLista.get(j);
                        pontosLista.set(j, pontosLista.get(j + 1));
                        pontosLista.set(j + 1, pTemp);

                        UsuarioParticipante uTemp = ordenados.get(j);
                        ordenados.set(j, ordenados.get(j + 1));
                        ordenados.set(j + 1, uTemp);
                    }
                }
            }

            defaultTableModel.setRowCount(0);
            for (int i = 0; i < ordenados.size(); i++) {
                defaultTableModel.addRow(new Object[]{
                        (i + 1) + "º",
                        ordenados.get(i).getNome(),
                        pontosLista.get(i)
                });
            }

            if (ordenados.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Esse grupo ainda não tem participantes.");
            }
        } catch (PersistenciaException p) {
            JOptionPane.showMessageDialog(this, "Erro de persistencia: " + p.getMessage());
        }
    }
}