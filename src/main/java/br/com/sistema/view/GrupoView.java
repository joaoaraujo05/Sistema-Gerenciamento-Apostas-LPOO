package br.com.sistema.view;

import br.com.sistema.controller.GrupoController;
import br.com.sistema.model.entities.Grupo;
import br.com.sistema.model.exceptions.LimiteCadastroException;
import br.com.sistema.model.exceptions.PersistenciaException;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

public class GrupoView extends JPanel {

    private GrupoController grupoController;

    private JTextField txtNomeGrupo;

    private JButton btnNovo;
    private JButton btnSalvar;
    private JButton btnEditar;
    private JButton btnExcluir;

    private JTable tabela;
    private DefaultTableModel defaultTableModel;

    private Long idGrupo = null;

    public GrupoView(GrupoController grupoController) {
        this.grupoController = grupoController;
        setLayout(new BorderLayout());
        setSize(1024, 728);
        iniciarComponentes();
        carregarTabela();
    }

    public void iniciarComponentes() {
        JPanel panelMain = new JPanel(new BorderLayout(10, 10));
        panelMain.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel formulario = new JPanel(new GridLayout(1, 2, 5, 5));
        formulario.setBorder(BorderFactory.createTitledBorder("Dados (máximo 5)"));
        formulario.add(new JLabel("Nome do grupo:"));
        txtNomeGrupo = new JTextField();
        formulario.add(txtNomeGrupo);

        JPanel panelAcoes = new JPanel(new FlowLayout(FlowLayout.CENTER));
        btnNovo = new JButton("Novo");
        btnSalvar = new JButton("Salvar");
        btnEditar = new JButton("Editar");
        btnExcluir = new JButton("Excluir");
        panelAcoes.add(btnNovo);
        panelAcoes.add(btnSalvar);
        panelAcoes.add(btnEditar);
        panelAcoes.add(btnExcluir);

        JPanel panelTabela = new JPanel(new BorderLayout());
        panelTabela.setBorder(BorderFactory.createTitledBorder("Consulta"));

        defaultTableModel = new DefaultTableModel(new String[]{"ID", "Nome Grupo"}, 0) {
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
        panelSuperior.add(panelAcoes, BorderLayout.SOUTH);

        panelMain.add(panelSuperior, BorderLayout.NORTH);
        panelMain.add(panelTabela, BorderLayout.CENTER);

        add(panelMain, BorderLayout.CENTER);

        configurarAcoes();
    }

    public void configurarAcoes() {
        btnNovo.addActionListener(e -> limparCampos());
        btnSalvar.addActionListener(e -> salvarGrupo());
        btnEditar.addActionListener(e -> editarGrupo());
        btnExcluir.addActionListener(e -> excluirGrupo());

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
        List<Grupo> lista = grupoController.listarGrupos();
        for (Grupo g : lista) {
            defaultTableModel.addRow(new Object[]{g.getIdGrupo(), g.getNomeGrupo()});
        }
    }

    private void preencherFormularioComLinhaSelecionada() {
        int row = tabela.getSelectedRow();
        if (row != -1) {
            idGrupo = (Long) tabela.getValueAt(row, 0);
            txtNomeGrupo.setText(tabela.getValueAt(row, 1).toString());
        }
    }

    public void limparCampos() {
        txtNomeGrupo.setText("");
        tabela.clearSelection();
        idGrupo = null;
        txtNomeGrupo.requestFocus();
    }

    public void salvarGrupo() {
        String nome = txtNomeGrupo.getText().trim();

        if (nome.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Informe o nome do grupo!", "Atenção", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            if (idGrupo == null) {
                grupoController.cadastrarGrupo(nome);
                JOptionPane.showMessageDialog(this, "Grupo cadastrado!");
            } else {
                Grupo g = grupoController.buscarGrupoPorId(idGrupo);
                if (g != null) {
                    g.setNomeGrupo(nome);
                    grupoController.atualizarGrupo(g);
                    JOptionPane.showMessageDialog(this, "Grupo atualizado!");
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

    public void editarGrupo() {
        if (tabela.getSelectedRow() == -1) {
            JOptionPane.showMessageDialog(this, "Escolha um grupo para editar", "Atenção", JOptionPane.WARNING_MESSAGE);
            return;
        }
        preencherFormularioComLinhaSelecionada();
    }

    public void excluirGrupo() {
        int linha = tabela.getSelectedRow();

        if (linha == -1) {
            JOptionPane.showMessageDialog(this, "Escolha um grupo para ser excluido", "Atenção", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Long id = (Long) defaultTableModel.getValueAt(linha, 0);

        int resposta = JOptionPane.showConfirmDialog(
                this,
                "Deseja realmente excluir o grupo?",
                "SIM",
                JOptionPane.YES_NO_OPTION
        );

        if (resposta == JOptionPane.YES_OPTION) {
            try {
                grupoController.removerGrupo(id);
                JOptionPane.showMessageDialog(this, "Grupo removido com sucesso!");
                carregarTabela();
                limparCampos();
            } catch (PersistenciaException p) {
                JOptionPane.showMessageDialog(this, "Não foi possivel excluir o grupo: " + p.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}