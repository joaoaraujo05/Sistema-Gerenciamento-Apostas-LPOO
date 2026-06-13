package br.com.sistema.view;

import br.com.sistema.controller.CampeonatoController;
import br.com.sistema.controller.TimeController;
import br.com.sistema.model.entities.Campeonato;
import br.com.sistema.model.entities.Time;
import br.com.sistema.model.exceptions.LimiteCadastroException;
import br.com.sistema.model.exceptions.PersistenciaException;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

public class CampeonatoView extends JPanel {

    private CampeonatoController campeonatoController;
    private TimeController timeController;

    private JTextField txtNomeCamp;
    private JList<Time> listTimes;
    private DefaultListModel<Time> listModel;

    private JButton btnNovo;
    private JButton btnSalvar;
    private JButton btnEditar;
    private JButton btnExcluir;
    private JButton btnAtualizar;

    private JTable tabela;
    private DefaultTableModel defaultTableModel;

    private Long idCampeonato = null;

    public CampeonatoView(CampeonatoController campeonatoController, TimeController timeController) {
        this.campeonatoController = campeonatoController;
        this.timeController = timeController;
        setLayout(new BorderLayout());
        setSize(1024, 728);
        iniciarComponentes();
        carregarTimes();
        carregarTabela();
    }

    public void iniciarComponentes() {
        JPanel panelMain = new JPanel(new BorderLayout(10, 10));
        panelMain.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel formulario = new JPanel(new GridLayout(1, 2, 5, 5));
        formulario.setBorder(BorderFactory.createTitledBorder("Dados"));

        formulario.add(new JLabel("Nome do campeonato:"));
        txtNomeCamp = new JTextField();
        formulario.add(txtNomeCamp);

        JPanel panelTimes = new JPanel(new BorderLayout());
        panelTimes.setBorder(BorderFactory.createTitledBorder("Selecione os clubes (máximo 8) - segure CTRL para marcar vários"));
        listModel = new DefaultListModel<>();
        listTimes = new JList<>(listModel);
        listTimes.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        listTimes.setVisibleRowCount(5);
        panelTimes.add(new JScrollPane(listTimes), BorderLayout.CENTER);

        JPanel panelAcoes = new JPanel(new FlowLayout(FlowLayout.CENTER));
        btnNovo = new JButton("Novo");
        btnSalvar = new JButton("Salvar");
        btnEditar = new JButton("Editar");
        btnExcluir = new JButton("Excluir");
        btnAtualizar = new JButton("Atualizar Times");
        panelAcoes.add(btnNovo);
        panelAcoes.add(btnSalvar);
        panelAcoes.add(btnEditar);
        panelAcoes.add(btnExcluir);
        panelAcoes.add(btnAtualizar);

        JPanel panelTabela = new JPanel(new BorderLayout());
        panelTabela.setBorder(BorderFactory.createTitledBorder("Consulta (clique para editar)"));

        defaultTableModel = new DefaultTableModel(new String[]{"ID", "Nome Campeonato"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tabela = new JTable(defaultTableModel);
        JScrollPane scrollPane = new JScrollPane(tabela);
        panelTabela.add(scrollPane, BorderLayout.CENTER);

        JPanel panelSuperior = new JPanel(new BorderLayout());
        panelSuperior.add(formulario, BorderLayout.NORTH);
        panelSuperior.add(panelTimes, BorderLayout.CENTER);
        panelSuperior.add(panelAcoes, BorderLayout.SOUTH);

        panelMain.add(panelSuperior, BorderLayout.NORTH);
        panelMain.add(panelTabela, BorderLayout.CENTER);

        add(panelMain, BorderLayout.CENTER);

        configurarAcoes();
    }

    public void configurarAcoes() {
        btnNovo.addActionListener(e -> limparCampos());
        btnSalvar.addActionListener(e -> salvarCampeonato());
        btnEditar.addActionListener(e -> editarCampeonato());
        btnExcluir.addActionListener(e -> excluirCampeonato());
        btnAtualizar.addActionListener(e -> carregarTimes());

        tabela.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (tabela.getSelectedRow() != -1) {
                    preencherFormularioComLinhaSelecionada();
                }
            }
        });
    }

    private void carregarTimes() {
        listModel.clear();
        List<Time> times = timeController.listarTimes();
        for (Time t : times) {
            listModel.addElement(t);
        }
    }

    private void carregarTabela() {
        defaultTableModel.setRowCount(0);
        List<Campeonato> lista = campeonatoController.listaTodos();
        for (Campeonato c : lista) {
            defaultTableModel.addRow(new Object[]{c.getIdCampeonato(), c.getNomeCampeonato()});
        }
    }

    private void preencherFormularioComLinhaSelecionada() {
        int row = tabela.getSelectedRow();
        if (row == -1) {
            return;
        }

        idCampeonato = (Long) tabela.getValueAt(row, 0);
        txtNomeCamp.setText(tabela.getValueAt(row, 1).toString());

        // marca na lista os times que ja fazem parte do campeonato
        Campeonato c = campeonatoController.buscarCampeonatoPorId(idCampeonato);
        List<Integer> indices = new ArrayList<>();
        if (c != null) {
            for (int i = 0; i < listModel.size(); i++) {
                Time t = listModel.get(i);
                for (Time ct : c.getTimes()) {
                    if (ct.getIdTime().equals(t.getIdTime())) {
                        indices.add(i);
                        break;
                    }
                }
            }
        }

        int[] selecao = new int[indices.size()];
        for (int i = 0; i < indices.size(); i++) {
            selecao[i] = indices.get(i);
        }
        listTimes.setSelectedIndices(selecao);
    }

    public void limparCampos() {
        txtNomeCamp.setText("");
        listTimes.clearSelection();
        tabela.clearSelection();
        idCampeonato = null;
        txtNomeCamp.requestFocus();
    }

    public void salvarCampeonato() {
        String nome = txtNomeCamp.getText().trim();
        List<Time> selecionados = listTimes.getSelectedValuesList();

        if (selecionados.size() > 8) {
            JOptionPane.showMessageDialog(this, "Selecione no máximo 8 times!", "Atenção", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            if (idCampeonato == null) {
                campeonatoController.cadastrarCampeonato(nome, selecionados);
                JOptionPane.showMessageDialog(this, "Campeonato cadastrado!");
            } else {
                Campeonato c = campeonatoController.buscarCampeonatoPorId(idCampeonato);
                if (c != null) {
                    c.setNomeCampeonato(nome);
                    c.getTimes().clear();
                    c.getTimes().addAll(selecionados);
                    campeonatoController.atualizarCampeonato(c);
                    JOptionPane.showMessageDialog(this, "Campeonato atualizado!");
                }
            }
            limparCampos();
            carregarTabela();
        } catch (IllegalArgumentException i) {
            JOptionPane.showMessageDialog(this, "Erro de argumentos passados: " + i.getMessage());
        } catch (LimiteCadastroException l) {
            JOptionPane.showMessageDialog(this, l.getMessage(), "Atenção", JOptionPane.WARNING_MESSAGE);
        } catch (PersistenciaException p) {
            JOptionPane.showMessageDialog(this, "Erro de persistencia: " + p.getMessage());
        }
    }

    public void editarCampeonato() {
        if (tabela.getSelectedRow() == -1) {
            JOptionPane.showMessageDialog(this, "Escolha um campeonato para editar", "Atenção", JOptionPane.WARNING_MESSAGE);
            return;
        }
        preencherFormularioComLinhaSelecionada();
    }

    public void excluirCampeonato() {
        int linha = tabela.getSelectedRow();

        if (linha == -1) {
            JOptionPane.showMessageDialog(this, "Escolha um campeonato para ser excluido", "Atenção", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Long id = (Long) defaultTableModel.getValueAt(linha, 0);

        int resposta = JOptionPane.showConfirmDialog(
                this,
                "Deseja realmente excluir o campeonato?",
                "SIM",
                JOptionPane.YES_NO_OPTION
        );

        if (resposta == JOptionPane.YES_OPTION) {
            try {
                campeonatoController.removerCampeonato(id);
                JOptionPane.showMessageDialog(this, "Campeonato removido com sucesso!");
                carregarTabela();
                limparCampos();
            } catch (PersistenciaException p) {
                JOptionPane.showMessageDialog(this, "Não foi possivel excluir o campeonato: " + p.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}