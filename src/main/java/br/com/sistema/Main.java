package br.com.sistema;

import br.com.sistema.controller.UsuarioController;
import br.com.sistema.dao.DAOFactory;
import br.com.sistema.dao.hibernate.HibernateUtil;
import br.com.sistema.dao.jdbc.ConnectionFactory;
import br.com.sistema.view.LoginView;

import javax.swing.*;

public class Main {
    //private static final int TIPO_PERSISTENCIA = 1; // 1 = Hibernate
    //private static final int TIPO_PERSISTENCIA = 2; // 2 = JDBC
    private static final int TIPO_PERSISTENCIA = 3;    // 3 = Arquivo Binário

    public static void main( String[] args ) {

        try {
            DAOFactory.configurar(TIPO_PERSISTENCIA);

            if (TIPO_PERSISTENCIA == 2) {
                ConnectionFactory.createDatabase();
            }

            new UsuarioController().garantirAdminPadrao();

        } catch (Exception e) {
            System.err.println("Erro na inicialização do sistema: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }

        if (TIPO_PERSISTENCIA == 1) {
            Runtime.getRuntime().addShutdownHook(new Thread(HibernateUtil::shutdown));
        }

        SwingUtilities.invokeLater(() -> {
            LoginView login = new LoginView();
            login.setVisible(true);
        });

    }
}