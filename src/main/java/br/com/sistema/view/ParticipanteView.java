package br.com.sistema.view;

import br.com.sistema.controller.UsuarioController;
import br.com.sistema.model.entities.Grupo;
import br.com.sistema.model.entities.UsuarioParticipante;
import br.com.sistema.model.exceptions.PersistenciaException;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

public class ParticipanteView extends JPanel {

    private UsuarioController usuarioController;

    private JTextField txtNome;
    private JTextField txtLogin;
    private JPasswordField txtSenha;

    private JButton btnNovo;
    private JButton btnSalvar;
    private JButton btnEditar;
    private JButton btnExcluir;

    private JTable tabela;
    private DefaultTableModel defaultTableModel;

    private Long idParticipante = null;

    public ParticipanteView(UsuarioController usuarioController) {
        this.usuarioController = usuarioController;
        setLayout(new BorderLayout());
        setSize(1024, 728);
        iniciarComponentes();
        carregarTabela();
    }

    public void iniciarComponentes() {
        JPanel panelMain = new JPanel(new BorderLayout(10, 10));
        panelMain.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel formulario = new JPanel(new GridLayout(3, 2, 5, 5));
        formulario.setBorder(BorderFactory.createTitledBorder("Dados"));

        formulario.add(new JLabel("Nome do participante:"));
        txtNome = new JTextField();
        formulario.add(txtNome);

        formulario.add(new JLabel("Login:"));
        txtLogin = new JTextField();
        formulario.add(txtLogin);

        formulario.add(new JLabel("Senha (min. 4 caracteres):"));
        txtSenha = new JPasswordField();
        formulario.add(txtSenha);

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

        defaultTableModel = new DefaultTableModel(new String[]{"ID", "Nome", "Login", "Grupos"}, 0) {
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
        btnSalvar.addActionListener(e -> salvarParticipante());
        btnEditar.addActionListener(e -> editarParticipante());
        btnExcluir.addActionListener(e -> excluirParticipante());

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
        List<UsuarioParticipante> lista = usuarioController.listarParticipantes();
        for (UsuarioParticipante p : lista) {
            defaultTableModel.addRow(new Object[]{p.getId(), p.getNome(), p.getLogin(), nomesDosGrupos(p)});
        }
    }

    private String nomesDosGrupos(UsuarioParticipante p) {
        if (p.getGrupos() == null || p.getGrupos().isEmpty()) {
            return "(sem grupo)";
        }
        StringBuilder sb = new StringBuilder();
        for (Grupo g : p.getGrupos()) {
            if (sb.length() > 0) {
                sb.append(", ");
            }
            sb.append(g.getNomeGrupo());
        }
        return sb.toString();
    }

    private void preencherFormularioComLinhaSelecionada() {
        int row = tabela.getSelectedRow();
        if (row != -1) {
            idParticipante = (Long) tabela.getValueAt(row, 0);
            txtNome.setText(tabela.getValueAt(row, 1).toString());
            txtLogin.setText(tabela.getValueAt(row, 2).toString());
            txtSenha.setText("");
        }
    }

    public void limparCampos() {
        txtNome.setText("");
        txtLogin.setText("");
        txtSenha.setText("");
        tabela.clearSelection();
        idParticipante = null;
        txtNome.requestFocus();
    }

    public void salvarParticipante() {
        String nome = txtNome.getText().trim();
        String login = txtLogin.getText().trim();
        String senha = new String(txtSenha.getPassword());

        try {
            if (idParticipante == null) {
                usuarioController.cadastrarParticipante(nome, login, senha);
                JOptionPane.showMessageDialog(this, "Participante cadastrado!");
            } else {
                // na edicao, senha em branco mantem a atual
                usuarioController.atualizarParticipante(idParticipante, nome, login, senha);
                JOptionPane.showMessageDialog(this, "Participante atualizado!");
            }
            limparCampos();
            carregarTabela();
        } catch (IllegalArgumentException i) {
            JOptionPane.showMessageDialog(this, "Erro de argumentos passados: " + i.getMessage());
        } catch (PersistenciaException p) {
            JOptionPane.showMessageDialog(this, "Erro de persistencia: " + p.getMessage());
        }
    }

    public void editarParticipante() {
        if (tabela.getSelectedRow() == -1) {
            JOptionPane.showMessageDialog(this, "Escolha um participante para editar", "Atenção", JOptionPane.WARNING_MESSAGE);
            return;
        }
        preencherFormularioComLinhaSelecionada();
    }

    public void excluirParticipante() {
        int linha = tabela.getSelectedRow();

        if (linha == -1) {
            JOptionPane.showMessageDialog(this, "Escolha um participante para ser excluido", "Atenção", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Long id = (Long) defaultTableModel.getValueAt(linha, 0);

        int resposta = JOptionPane.showConfirmDialog(
                this,
                "Deseja realmente excluir o participante?",
                "SIM",
                JOptionPane.YES_NO_OPTION
        );

        if (resposta == JOptionPane.YES_OPTION) {
            try {
                usuarioController.removerParticipante(id);
                JOptionPane.showMessageDialog(this, "Participante removido com sucesso!");
                carregarTabela();
                limparCampos();
            } catch (PersistenciaException p) {
                JOptionPane.showMessageDialog(this, "Não foi possivel excluir o participante: " + p.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}