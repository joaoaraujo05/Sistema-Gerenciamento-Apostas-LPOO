package br.com.sistema.view;

import br.com.sistema.controller.UsuarioController;
import br.com.sistema.model.entities.Usuario;
import br.com.sistema.model.exceptions.PersistenciaException;

import javax.swing.*;
import java.awt.*;

public class LoginView extends JFrame {

    private UsuarioController usuarioController;

    private JTextField txtLogin;
    private JPasswordField txtSenha;
    private JButton btnEntrar;
    private JButton btnCadastrar;

    public LoginView() {
        this.usuarioController = new UsuarioController();
        setTitle("[UNAERP] Login - Sistema de Apostas");
        setSize(440, 220);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        iniciarComponentes();
    }

    public void iniciarComponentes() {
        JPanel panelMain = new JPanel(new BorderLayout(10, 10));
        panelMain.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JPanel formulario = new JPanel(new GridLayout(2, 2, 5, 10));
        formulario.add(new JLabel("Login:"));
        txtLogin = new JTextField();
        formulario.add(txtLogin);

        formulario.add(new JLabel("Senha:"));
        txtSenha = new JPasswordField();
        formulario.add(txtSenha);

        JPanel panelAcoes = new JPanel(new FlowLayout(FlowLayout.CENTER));
        btnEntrar = new JButton("Entrar");
        btnCadastrar = new JButton("Cadastrar-se");
        panelAcoes.add(btnEntrar);
        panelAcoes.add(btnCadastrar);

        JLabel rodape = new JLabel("Credencial admin: senha 'admin123'", SwingConstants.CENTER);

        panelMain.add(formulario, BorderLayout.CENTER);
        panelMain.add(panelAcoes, BorderLayout.SOUTH);
        panelMain.add(rodape, BorderLayout.NORTH);

        add(panelMain);

        configurarAcoes();
    }

    public void configurarAcoes() {
        btnEntrar.addActionListener(e -> entrar());
        btnCadastrar.addActionListener(e -> abrirCadastro());
    }

    public void abrirCadastro() {
        CadastroParticipanteView cadastro = new CadastroParticipanteView();
        cadastro.setVisible(true);
    }

    public void entrar() {
        String login = txtLogin.getText().trim();
        String senha = new String(txtSenha.getPassword());

        try {
            Usuario usuario = usuarioController.autenticar(login, senha);

            if (usuario == null) {
                JOptionPane.showMessageDialog(this, "Login ou senha inválidos!", "Atenção", JOptionPane.WARNING_MESSAGE);
                return;
            }

            ViewPrincipal principal = new ViewPrincipal(usuario);
            principal.setVisible(true);
            this.dispose();

        } catch (IllegalArgumentException i) {
            JOptionPane.showMessageDialog(this, i.getMessage());
        } catch (PersistenciaException p) {
            JOptionPane.showMessageDialog(this, "Erro de persistencia: " + p.getMessage());
        }
    }
}