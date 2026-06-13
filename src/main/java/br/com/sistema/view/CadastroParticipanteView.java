package br.com.sistema.view;

import br.com.sistema.controller.UsuarioController;
import br.com.sistema.model.exceptions.PersistenciaException;

import javax.swing.*;
import java.awt.*;

public class CadastroParticipanteView extends JFrame {

    private UsuarioController usuarioController;

    private JTextField txtNome;
    private JTextField txtLogin;
    private JPasswordField txtSenha;
    private JButton btnCadastrar;
    private JButton btnVoltar;

    public CadastroParticipanteView() {
        this.usuarioController = new UsuarioController();
        setTitle("[UNAERP] Cadastro de Participante");
        setSize(380, 240);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        iniciarComponentes();
    }

    public void iniciarComponentes() {
        JPanel panelMain = new JPanel(new BorderLayout(10, 10));
        panelMain.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JPanel formulario = new JPanel(new GridLayout(3, 2, 5, 10));
        formulario.setBorder(BorderFactory.createTitledBorder("Dados"));

        formulario.add(new JLabel("Nome:"));
        txtNome = new JTextField();
        formulario.add(txtNome);

        formulario.add(new JLabel("Login:"));
        txtLogin = new JTextField();
        formulario.add(txtLogin);

        formulario.add(new JLabel("Senha (min. 4 caracteres):"));
        txtSenha = new JPasswordField();
        formulario.add(txtSenha);

        JPanel panelAcoes = new JPanel(new FlowLayout(FlowLayout.CENTER));
        btnCadastrar = new JButton("Cadastrar");
        btnVoltar = new JButton("Voltar");
        panelAcoes.add(btnCadastrar);
        panelAcoes.add(btnVoltar);

        panelMain.add(formulario, BorderLayout.CENTER);
        panelMain.add(panelAcoes, BorderLayout.SOUTH);

        add(panelMain);

        configurarAcoes();
    }

    public void configurarAcoes() {
        btnCadastrar.addActionListener(e -> cadastrar());
        btnVoltar.addActionListener(e -> this.dispose());
    }

    public void cadastrar() {
        String nome = txtNome.getText().trim();
        String login = txtLogin.getText().trim();
        String senha = new String(txtSenha.getPassword());

        try {
            usuarioController.cadastrarParticipante(nome, login, senha);
            JOptionPane.showMessageDialog(this, "Cadastro realizado! Agora faça login com seu usuário.");
            this.dispose();
        } catch (IllegalArgumentException i) {
            JOptionPane.showMessageDialog(this, i.getMessage(), "Atenção", JOptionPane.WARNING_MESSAGE);
        } catch (PersistenciaException p) {
            JOptionPane.showMessageDialog(this, "Erro de persistencia: " + p.getMessage());
        }
    }
}