package br.com.sistema.dao.hibernate;

import br.com.sistema.model.entities.*;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

public class HibernateUtil {

    private static final SessionFactory sessionFactory = buildSessionFactory();

    private static SessionFactory buildSessionFactory() {
        try {
            return new Configuration()
                    .configure()
                    .addAnnotatedClass(Time.class)
                    .addAnnotatedClass(Campeonato.class)
                    .addAnnotatedClass(Usuario.class)
                    .addAnnotatedClass(UsuarioAdministrador.class)
                    .addAnnotatedClass(UsuarioParticipante.class)
                    .addAnnotatedClass(Grupo.class)
                    .addAnnotatedClass(Partida.class)
                    .addAnnotatedClass(Aposta.class)
                    .buildSessionFactory();
        } catch (Throwable ex) {
            System.out.println("Falha ao iniciar o Hibernate: " + ex);
            throw new ExceptionInInitializerError(ex);
        }
    }

    public static SessionFactory getSessionFactory() {
        return sessionFactory;
    }

    public static void shutdown() {
        if (sessionFactory != null && !sessionFactory.isClosed()) {
            sessionFactory.close();
        }
    }
}