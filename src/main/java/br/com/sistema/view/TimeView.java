package br.com.sistema.view;

import br.com.sistema.controller.TimeController;
import br.com.sistema.model.entities.Time;
import br.com.sistema.model.exceptions.PersistenciaException;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

public class TimeView extends JPanel {

    private TimeController timeController;

    private JTextField txtTime;

    private JButton btnNovo;
    private JButton btnSalvar;
    private JButton btnEditar;
    private JButton btnExcluir;

    private JTable tabela;
    private DefaultTableModel defaultTableModel;

    private Long idTime = null;

    public TimeView(TimeController timeController) {
        this.timeController = timeController;
        setLayout(new BorderLayout());
        setSize(1024,728);
        iniciarComponentes();
        carregarTabela();
    }

    public void iniciarComponentes() {
        JPanel panelMain = new JPanel(new BorderLayout(10,10));
        panelMain.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));

        JPanel formulario = new JPanel(new GridLayout(3, 2, 5, 5));
        formulario.setBorder(BorderFactory.createTitledBorder("Dados"));

        formulario.add(new JLabel("Nome do time:"));
        txtTime = new JTextField();
        txtTime.setEditable(true);
        formulario.add(txtTime);

        JPanel panelCenter = new JPanel(new BorderLayout());

        JPanel panelAcoes = new JPanel(new FlowLayout(FlowLayout.CENTER));
        btnNovo = new JButton("Novo");
        btnSalvar = new JButton("Salvar");
        btnEditar = new JButton("Editar");
        btnExcluir = new JButton("Excluir");
        panelAcoes.add(btnNovo);
        panelAcoes.add(btnSalvar);
        panelAcoes.add(btnEditar);
        panelAcoes.add(btnExcluir);

        panelCenter.add(panelAcoes, BorderLayout.SOUTH);

        JPanel panelTabela = new JPanel(new BorderLayout());
        panelTabela.setBorder(BorderFactory.createTitledBorder("Consulta"));

        defaultTableModel = new DefaultTableModel(new String[]{"ID", "Nome Time"}, 0) {
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
        panelSuperior.add(panelCenter, BorderLayout.SOUTH);

        panelMain.add(panelSuperior, BorderLayout.NORTH);
        panelMain.add(panelTabela, BorderLayout.CENTER);

        add(panelMain, BorderLayout.CENTER);

        configurarAcoes();
    }

    public void configurarAcoes() {

        btnNovo.addActionListener(e-> limparCampos());
        btnSalvar.addActionListener(e-> salvarTime());
        btnEditar.addActionListener(e-> editarTime());
        btnExcluir.addActionListener(e -> excluirTime());

        tabela.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (tabela.getSelectedRow() != -1) {
                    preencherFormularioComLinhaSelecionada();
                }
            }
        });
    }

    private void carregarTabela() {
        defaultTableModel.setRowCount(0);
        List<Time> lista = timeController.listarTimes();
        for (Time time : lista) {
            defaultTableModel.addRow(new Object[]{time.getIdTime(),time.getNomeTime()});
        }
    }

    private void preencherFormularioComLinhaSelecionada() {
        int row = tabela.getSelectedRow();
        if (row != -1) {
            idTime = (Long) tabela.getValueAt(row, 0);
            txtTime.setText(tabela.getValueAt(row, 1).toString());
        }
    }

    public void limparCampos() {
        txtTime.setText("");
        tabela.clearSelection();
        idTime = null;
        txtTime.requestFocus();
    }

    public void salvarTime() {
        String nomeTime = txtTime.getText().trim();

        try {
            if (idTime == null) {
                timeController.cadastrarTime(nomeTime);
                JOptionPane.showMessageDialog(this,"Time cadastrado!");
            } else {
                Time t = timeController.buscarTimePorId(idTime);
                if (t != null)  {
                    t.setNomeTime(nomeTime);
                    timeController.atualizarTime(t);
                    JOptionPane.showMessageDialog(this,"Time cadastrado!");
                }
            }
            limparCampos();
            carregarTabela();
        } catch (IllegalArgumentException i) {
            JOptionPane.showMessageDialog(this,"Erro de argumentos passados: " + i.getMessage());
        } catch (PersistenciaException p) {
            JOptionPane.showMessageDialog(this,"Erro de persistencia: " + p.getMessage());
        }
    }

    public void editarTime() {
        int linha = tabela.getSelectedRow();

        if (linha == -1) {
            JOptionPane.showMessageDialog(this, "Escolha um time para ser editado", "Atenção", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Long id = (Long) defaultTableModel.getValueAt(linha,0);
        try {
            Time t = timeController.buscarTimePorId(id);
            if (t != null) {
                txtTime.setText(t.getNomeTime());
                idTime = id;
            }
        } catch (PersistenciaException p) {
            JOptionPane.showMessageDialog(this,"Erro ao editar o time");
        }
    }

    public void excluirTime() {
        int linha = tabela.getSelectedRow();

        if (linha == -1) {
            JOptionPane.showMessageDialog(this,"Escolha um time para ser excluido", "Atenção", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Long id = (Long) defaultTableModel.getValueAt(linha, 0);
        String nomeTime = (String) defaultTableModel.getValueAt(linha, 1);

        int resposta = JOptionPane.showConfirmDialog(
                this,
                "Deseja realmente excluir o time?",
                "SIM",
                JOptionPane.YES_NO_OPTION
        );

        if (resposta == JOptionPane.YES_OPTION) {
            try {
                timeController.removerTime(id);
                JOptionPane.showMessageDialog(this,"Time removido com sucesso!");
                carregarTabela();
                limparCampos();
            } catch (PersistenciaException p) {
                JOptionPane.showMessageDialog(this,"Não foi possuir excluir o time: " + p.getMessage(),"Erro", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

}
