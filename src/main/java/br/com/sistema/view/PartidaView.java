package br.com.sistema.view;

import br.com.sistema.controller.CampeonatoController;
import br.com.sistema.controller.PartidaController;
import br.com.sistema.controller.TimeController;
import br.com.sistema.model.entities.Campeonato;
import br.com.sistema.model.entities.Partida;
import br.com.sistema.model.entities.Time;
import br.com.sistema.model.exceptions.PersistenciaException;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

public class PartidaView extends JPanel {

    private PartidaController partidaController;
    private TimeController timeController;
    private CampeonatoController campeonatoController;

    private JComboBox<Campeonato> comboCampeonatos;
    private JComboBox<Time> comboMandante;
    private JComboBox<Time> comboVisitante;
    private JTextField txtDataHora;

    private JTextField txtGolsMandante;
    private JTextField txtGolsVisitante;

    private JButton btnNovo;
    private JButton btnSalvar;
    private JButton btnEditar;
    private JButton btnRegistrarResultado;
    private JButton btnAtualizar;

    private JTable tabela;
    private DefaultTableModel defaultTableModel;

    private Long idPartida = null;

    private final DateTimeFormatter formato = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    public PartidaView(PartidaController partidaController, TimeController timeController, CampeonatoController campeonatoController) {
        this.partidaController = partidaController;
        this.timeController = timeController;
        this.campeonatoController = campeonatoController;
        setLayout(new BorderLayout());
        setSize(1024, 728);
        iniciarComponentes();
        carregarCampeonatos();
        carregarTimes();
        carregarTabela();
    }

    public void iniciarComponentes() {
        JPanel panelMain = new JPanel(new BorderLayout(10, 10));
        panelMain.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel formulario = new JPanel(new GridLayout(4, 2, 5, 5));
        formulario.setBorder(BorderFactory.createTitledBorder("Lançar Partida"));

        formulario.add(new JLabel("Campeonato:"));
        comboCampeonatos = new JComboBox<>();
        formulario.add(comboCampeonatos);

        formulario.add(new JLabel("Clube Mandante:"));
        comboMandante = new JComboBox<>();
        formulario.add(comboMandante);

        formulario.add(new JLabel("Clube Visitante:"));
        comboVisitante = new JComboBox<>();
        formulario.add(comboVisitante);

        formulario.add(new JLabel("Data e Hora (dd/MM/yyyy HH:mm):"));
        txtDataHora = new JTextField();
        formulario.add(txtDataHora);

        JPanel panelResultado = new JPanel(new GridLayout(2, 2, 5, 5));
        panelResultado.setBorder(BorderFactory.createTitledBorder("Lançar Resultado (selecione uma partida na tabela)"));
        panelResultado.add(new JLabel("Gols Mandante:"));
        txtGolsMandante = new JTextField();
        panelResultado.add(txtGolsMandante);
        panelResultado.add(new JLabel("Gols Visitante:"));
        txtGolsVisitante = new JTextField();
        panelResultado.add(txtGolsVisitante);

        JPanel panelAcoes = new JPanel(new FlowLayout(FlowLayout.CENTER));
        btnNovo = new JButton("Novo");
        btnSalvar = new JButton("Salvar Partida");
        btnEditar = new JButton("Editar");
        btnRegistrarResultado = new JButton("Registrar Resultado");
        btnAtualizar = new JButton("Atualizar Listas");
        panelAcoes.add(btnNovo);
        panelAcoes.add(btnSalvar);
        panelAcoes.add(btnEditar);
        panelAcoes.add(btnRegistrarResultado);
        panelAcoes.add(btnAtualizar);

        JPanel panelTabela = new JPanel(new BorderLayout());
        panelTabela.setBorder(BorderFactory.createTitledBorder("Partidas (selecione para editar)"));

        defaultTableModel = new DefaultTableModel(new String[]{"ID", "Campeonato", "Mandante", "Visitante", "Data/Hora", "Placar", "Finalizada"}, 0) {
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
        panelSuperior.add(panelResultado, BorderLayout.CENTER);
        panelSuperior.add(panelAcoes, BorderLayout.SOUTH);

        panelMain.add(panelSuperior, BorderLayout.NORTH);
        panelMain.add(panelTabela, BorderLayout.CENTER);

        add(panelMain, BorderLayout.CENTER);

        configurarAcoes();
    }

    public void configurarAcoes() {
        btnNovo.addActionListener(e -> limparCampos());
        btnSalvar.addActionListener(e -> salvarPartida());
        btnEditar.addActionListener(e -> editarPartida());
        btnRegistrarResultado.addActionListener(e -> registrarResultado());
        btnAtualizar.addActionListener(e -> {
            carregarCampeonatos();
            carregarTimes();
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

    private void carregarCampeonatos() {
        comboCampeonatos.removeAllItems();
        for (Campeonato c : campeonatoController.listaTodos()) {
            comboCampeonatos.addItem(c);
        }
    }

    private void carregarTimes() {
        comboMandante.removeAllItems();
        comboVisitante.removeAllItems();
        List<Time> times = timeController.listarTimes();
        for (Time t : times) {
            comboMandante.addItem(t);
            comboVisitante.addItem(t);
        }
    }

    private void carregarTabela() {
        defaultTableModel.setRowCount(0);
        List<Partida> lista = partidaController.listarPartidas();
        for (Partida p : lista) {
            String placar = p.isFinalizada() ? (p.getGolsMandante() + " x " + p.getGolsVisitante()) : "-";
            String finalizada = p.isFinalizada() ? "Sim" : "Não";
            String nomeCampeonato = (p.getCampeonato() != null) ? p.getCampeonato().getNomeCampeonato() : "(sem campeonato)";
            defaultTableModel.addRow(new Object[]{
                    p.getIdPartida(),
                    nomeCampeonato,
                    p.getClubeMandante().getNomeTime(),
                    p.getClubeVisitante().getNomeTime(),
                    p.getDataHora().format(formato),
                    placar,
                    finalizada
            });
        }
    }

    private void preencherFormularioComLinhaSelecionada() {
        int row = tabela.getSelectedRow();
        if (row == -1) {
            return;
        }

        idPartida = (Long) tabela.getValueAt(row, 0);
        Partida p = partidaController.buscarPartidaPorId(idPartida);
        if (p != null) {
            selecionarCampeonatoNoCombo(p.getCampeonato());
            selecionarTimeNoCombo(comboMandante, p.getClubeMandante());
            selecionarTimeNoCombo(comboVisitante, p.getClubeVisitante());
            txtDataHora.setText(p.getDataHora().format(formato));
        }
    }

    private void selecionarCampeonatoNoCombo(Campeonato alvo) {
        if (alvo == null) {
            return;
        }
        for (int i = 0; i < comboCampeonatos.getItemCount(); i++) {
            if (comboCampeonatos.getItemAt(i).getIdCampeonato().equals(alvo.getIdCampeonato())) {
                comboCampeonatos.setSelectedIndex(i);
                return;
            }
        }
    }

    private void selecionarTimeNoCombo(JComboBox<Time> combo, Time alvo) {
        if (alvo == null) {
            return;
        }
        for (int i = 0; i < combo.getItemCount(); i++) {
            if (combo.getItemAt(i).getIdTime().equals(alvo.getIdTime())) {
                combo.setSelectedIndex(i);
                return;
            }
        }
    }

    public void limparCampos() {
        idPartida = null;
        txtDataHora.setText("");
        txtGolsMandante.setText("");
        txtGolsVisitante.setText("");
        tabela.clearSelection();
    }

    public void salvarPartida() {
        Campeonato campeonato = (Campeonato) comboCampeonatos.getSelectedItem();
        Time mandante = (Time) comboMandante.getSelectedItem();
        Time visitante = (Time) comboVisitante.getSelectedItem();
        String dataHoraTexto = txtDataHora.getText().trim();

        if (campeonato == null) {
            JOptionPane.showMessageDialog(this, "Cadastre um campeonato antes de criar a partida!", "Atenção", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (mandante == null || visitante == null) {
            JOptionPane.showMessageDialog(this, "Cadastre os times antes de criar a partida!", "Atenção", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            LocalDateTime dataHora = LocalDateTime.parse(dataHoraTexto, formato);

            if (idPartida == null) {
                partidaController.cadastrarPartida(campeonato, mandante, visitante, dataHora);
                JOptionPane.showMessageDialog(this, "Partida cadastrada!");
            } else {
                if (mandante.getIdTime().equals(visitante.getIdTime())) {
                    JOptionPane.showMessageDialog(this, "Um clube nao pode jogar contra ele mesmo!");
                    return;
                }
                Partida p = partidaController.buscarPartidaPorId(idPartida);
                if (p != null) {
                    p.setCampeonato(campeonato);
                    p.setClubeMandante(mandante);
                    p.setClubeVisitante(visitante);
                    p.setDataHora(dataHora);
                    partidaController.atualizarPartida(p);
                    JOptionPane.showMessageDialog(this, "Partida atualizada!");
                }
            }
            limparCampos();
            carregarTabela();
        } catch (DateTimeParseException d) {
            JOptionPane.showMessageDialog(this, "Data/hora invalida! Use o formato dd/MM/yyyy HH:mm");
        } catch (IllegalArgumentException i) {
            JOptionPane.showMessageDialog(this, "Erro: " + i.getMessage());
        } catch (PersistenciaException p) {
            JOptionPane.showMessageDialog(this, "Erro de persistencia: " + p.getMessage());
        }
    }

    public void editarPartida() {
        if (tabela.getSelectedRow() == -1) {
            JOptionPane.showMessageDialog(this, "Escolha uma partida para editar", "Atenção", JOptionPane.WARNING_MESSAGE);
            return;
        }
        preencherFormularioComLinhaSelecionada();
    }

    public void registrarResultado() {
        int linha = tabela.getSelectedRow();
        if (linha == -1) {
            JOptionPane.showMessageDialog(this, "Selecione uma partida na tabela!", "Atenção", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Long id = (Long) defaultTableModel.getValueAt(linha, 0);

        try {
            int golsMandante = Integer.parseInt(txtGolsMandante.getText().trim());
            int golsVisitante = Integer.parseInt(txtGolsVisitante.getText().trim());

            partidaController.registrarResultado(id, golsMandante, golsVisitante);
            JOptionPane.showMessageDialog(this, "Resultado registrado e pontuações recalculadas!");
            limparCampos();
            carregarTabela();
        } catch (NumberFormatException n) {
            JOptionPane.showMessageDialog(this, "Informe um numero valido de gols!");
        } catch (IllegalArgumentException i) {
            JOptionPane.showMessageDialog(this, "Erro: " + i.getMessage());
        } catch (PersistenciaException p) {
            JOptionPane.showMessageDialog(this, "Erro de persistencia: " + p.getMessage());
        }
    }
}