package br.com.sistema.view;

import br.com.sistema.controller.ApostaController;
import br.com.sistema.controller.PartidaController;
import br.com.sistema.model.entities.Aposta;
import br.com.sistema.model.entities.Partida;
import br.com.sistema.model.entities.UsuarioParticipante;
import br.com.sistema.model.exceptions.ApostaInvalidaException;
import br.com.sistema.model.exceptions.PersistenciaException;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

public class ApostaView extends JPanel {

    private ApostaController apostaController;
    private PartidaController partidaController;
    private UsuarioParticipante participanteLogado;

    private JComboBox<Partida> comboPartidas;
    private JComboBox<String> comboResultado;
    private JTextField txtGolsMandante;
    private JTextField txtGolsVisitante;

    private JButton btnNovo;
    private JButton btnSalvar;
    private JButton btnEditar;
    private JButton btnExcluir;
    private JButton btnAtualizar;

    private JTable tabela;
    private DefaultTableModel defaultTableModel;

    private Long idAposta = null;

    public ApostaView(ApostaController apostaController, PartidaController partidaController, UsuarioParticipante participanteLogado) {
        this.apostaController = apostaController;
        this.partidaController = partidaController;
        this.participanteLogado = participanteLogado;
        setLayout(new BorderLayout());
        setSize(1024, 728);
        iniciarComponentes();
        carregarPartidas();
        carregarTabela();
    }

    public void iniciarComponentes() {
        JPanel panelMain = new JPanel(new BorderLayout(10, 10));
        panelMain.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel formulario = new JPanel(new GridLayout(4, 2, 5, 5));
        formulario.setBorder(BorderFactory.createTitledBorder("Aposta - " + participanteLogado.getNome()));

        formulario.add(new JLabel("Partida:"));
        comboPartidas = new JComboBox<>();
        formulario.add(comboPartidas);

        formulario.add(new JLabel("Resultado esperado:"));
        comboResultado = new JComboBox<>(new String[]{"MANDANTE", "VISITANTE", "EMPATE"});
        formulario.add(comboResultado);

        formulario.add(new JLabel("Gols Mandante (previsto):"));
        txtGolsMandante = new JTextField();
        formulario.add(txtGolsMandante);

        formulario.add(new JLabel("Gols Visitante (previsto):"));
        txtGolsVisitante = new JTextField();
        formulario.add(txtGolsVisitante);

        JPanel panelAcoes = new JPanel(new FlowLayout(FlowLayout.CENTER));
        btnNovo = new JButton("Novo");
        btnSalvar = new JButton("Salvar Aposta");
        btnEditar = new JButton("Editar");
        btnExcluir = new JButton("Excluir");
        btnAtualizar = new JButton("Atualizar Listas");
        panelAcoes.add(btnNovo);
        panelAcoes.add(btnSalvar);
        panelAcoes.add(btnEditar);
        panelAcoes.add(btnExcluir);
        panelAcoes.add(btnAtualizar);

        JPanel panelTabela = new JPanel(new BorderLayout());
        panelTabela.setBorder(BorderFactory.createTitledBorder("Minhas Apostas (clique para editar)"));

        defaultTableModel = new DefaultTableModel(new String[]{"ID", "Partida", "Palpite", "Placar Previsto", "Pontos"}, 0) {
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
        btnSalvar.addActionListener(e -> salvarAposta());
        btnEditar.addActionListener(e -> editarAposta());
        btnExcluir.addActionListener(e -> excluirAposta());
        btnAtualizar.addActionListener(e -> {
            carregarPartidas();
            carregarTabela();
        });

        tabela.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (tabela.getSelectedRow() != -1) {
                    preencherFormularioComLinhaSelecionada();
                }
            }
        });
    }

    private void carregarPartidas() {
        comboPartidas.removeAllItems();
        for (Partida p : partidaController.listarPartidas()) {
            comboPartidas.addItem(p);
        }
    }

    private void carregarTabela() {
        defaultTableModel.setRowCount(0);
        List<Aposta> lista = apostaController.listarApostasPorParticipante(participanteLogado.getId());
        for (Aposta a : lista) {
            String partida = (a.getPartida() != null) ? a.getPartida().toString() : "?";
            String placar = a.getGolsMandante() + " x " + a.getGolsVisitante();
            defaultTableModel.addRow(new Object[]{
                    a.getIdAposta(),
                    partida,
                    a.getResultadoEsperado(),
                    placar,
                    a.getPontos()
            });
        }
    }

    private void preencherFormularioComLinhaSelecionada() {
        int row = tabela.getSelectedRow();
        if (row == -1) {
            return;
        }

        idAposta = (Long) tabela.getValueAt(row, 0);
        Aposta a = apostaController.buscarApostaPorId(idAposta);
        if (a != null) {
            if (a.getPartida() != null) {
                selecionarPartidaNoCombo(a.getPartida().getIdPartida());
            }
            comboResultado.setSelectedItem(a.getResultadoEsperado());
            txtGolsMandante.setText(String.valueOf(a.getGolsMandante()));
            txtGolsVisitante.setText(String.valueOf(a.getGolsVisitante()));
        }
    }

    private void selecionarPartidaNoCombo(Long idPartida) {
        for (int i = 0; i < comboPartidas.getItemCount(); i++) {
            if (comboPartidas.getItemAt(i).getIdPartida().equals(idPartida)) {
                comboPartidas.setSelectedIndex(i);
                return;
            }
        }
    }

    public void limparCampos() {
        idAposta = null;
        comboResultado.setSelectedIndex(0);
        txtGolsMandante.setText("");
        txtGolsVisitante.setText("");
        tabela.clearSelection();
    }

    public void salvarAposta() {
        Partida partida = (Partida) comboPartidas.getSelectedItem();
        String resultado = (String) comboResultado.getSelectedItem();

        if (partida == null) {
            JOptionPane.showMessageDialog(this, "Selecione uma partida!", "Atenção", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            int golsMandante = Integer.parseInt(txtGolsMandante.getText().trim());
            int golsVisitante = Integer.parseInt(txtGolsVisitante.getText().trim());

            if (idAposta == null) {
                apostaController.registrarAposta(participanteLogado, partida.getIdPartida(), resultado, golsMandante, golsVisitante);
                JOptionPane.showMessageDialog(this, "Aposta registrada!");
            } else {
                apostaController.editarAposta(idAposta, resultado, golsMandante, golsVisitante);
                JOptionPane.showMessageDialog(this, "Aposta atualizada!");
            }
            limparCampos();
            carregarTabela();
        } catch (NumberFormatException n) {
            JOptionPane.showMessageDialog(this, "Informe um numero valido de gols!");
        } catch (ApostaInvalidaException a) {
            JOptionPane.showMessageDialog(this, a.getMessage(), "Aposta invalida", JOptionPane.WARNING_MESSAGE);
        } catch (IllegalArgumentException i) {
            JOptionPane.showMessageDialog(this, "Erro: " + i.getMessage());
        } catch (PersistenciaException p) {
            JOptionPane.showMessageDialog(this, "Erro de persistencia: " + p.getMessage());
        }
    }

    public void editarAposta() {
        if (tabela.getSelectedRow() == -1) {
            JOptionPane.showMessageDialog(this, "Escolha uma aposta para editar", "Atenção", JOptionPane.WARNING_MESSAGE);
            return;
        }
        preencherFormularioComLinhaSelecionada();
    }

    public void excluirAposta() {
        int linha = tabela.getSelectedRow();

        if (linha == -1) {
            JOptionPane.showMessageDialog(this, "Escolha uma aposta para excluir", "Atenção", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Long id = (Long) defaultTableModel.getValueAt(linha, 0);

        Aposta a = apostaController.buscarApostaPorId(id);
        if (a != null && a.getPartida() != null && a.getPartida().isFinalizada()) {
            JOptionPane.showMessageDialog(this, "A partida já foi finalizada; não é possível excluir essa aposta.", "Atenção", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int resposta = JOptionPane.showConfirmDialog(
                this,
                "Deseja realmente excluir essa aposta?",
                "SIM",
                JOptionPane.YES_NO_OPTION
        );

        if (resposta == JOptionPane.YES_OPTION) {
            try {
                apostaController.removerAposta(id);
                JOptionPane.showMessageDialog(this, "Aposta removida com sucesso!");
                carregarTabela();
                limparCampos();
            } catch (PersistenciaException p) {
                JOptionPane.showMessageDialog(this, "Não foi possivel excluir a aposta: " + p.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}