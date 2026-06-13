package br.com.sistema.dao;


import br.com.sistema.dao.binario.*;
import br.com.sistema.dao.hibernate.ApostaDAOHibernateImpl;
import br.com.sistema.dao.hibernate.CampeonatoDAOHibernateImpl;
import br.com.sistema.dao.hibernate.GrupoDAOHibernateImpl;
import br.com.sistema.dao.hibernate.PartidaDAOHibernateImpl;
import br.com.sistema.dao.hibernate.TimeDAOHibernateImpl;
import br.com.sistema.dao.hibernate.UsuarioDAOHibernateImpl;
import br.com.sistema.dao.jdbc.*;

public class DAOFactory {

    private static Integer persistenciaAtiva;

    public DAOFactory(){}

    public static void configurar(int opcao) {
        switch (opcao) {
            case 1:
                persistenciaAtiva = 1;
                break;
            case 2:
                persistenciaAtiva = 2;
                break;
            case 3:
                persistenciaAtiva = 3;
                break;
            default:
                System.out.println("Opção errada! Persisência padrão é HIBERNATE");
                persistenciaAtiva = 1;
        }
    }

    public static ITimeDAO criarTimeDAO() {
        switch (persistenciaAtiva) {
            case 1:
                return new TimeDAOHibernateImpl();
            case 2:
                return new TimeDAOJDBCImpl();
            case 3:
                return new TimeDAOBinarioImpl();
            default:
                throw new IllegalArgumentException("Persistência errada!");
        }
    }

    public static ICampeonatoDAO criarCampeonatoDAO() {
        switch (persistenciaAtiva) {
            case 1:
                return new CampeonatoDAOHibernateImpl();
            case 2:
                return new CampeonatoDAOJDBCImpl();
            case 3:
                return new CampeonatoDAOBinarioImpl();
            default:
                throw new IllegalArgumentException("Persistência errada!");
        }
    }

    public static IGrupoDAO criarGrupoDAO() {
        switch (persistenciaAtiva) {
            case 1:
                return new GrupoDAOHibernateImpl();
            case 2:
                return new GrupoDAOJDBCImpl();
            case 3:
                return new GrupoDAOBinarioImpl();
            default:
                throw new IllegalArgumentException("Persistência errada!");
        }
    }

    public static IPartidaDAO criarPartidaDAO() {
        switch (persistenciaAtiva) {
            case 1:
                return new PartidaDAOHibernateImpl();
            case 2:
                return new PartidaDAOJDBCImpl();
            case 3:
                return new PartidaDAOBinarioImpl();
            default:
                throw new IllegalArgumentException("Persistência errada!");
        }
    }

    public static IApostaDAO criarApostaDAO() {
        switch (persistenciaAtiva) {
            case 1:
                return new ApostaDAOHibernateImpl();
            case 2:
                return new ApostaDAOJDBCImpl();
            case 3:
                return new ApostaDAOBinarioImpl();
            default:
                throw new IllegalArgumentException("Persistência errada!");
        }
    }

    public static IUsuarioDAO criarUsuarioDAO() {
        switch (persistenciaAtiva) {
            case 1:
                return new UsuarioDAOHibernateImpl();
            case 2:
                return new UsuarioDAOJDBCImpl();
            case 3:
                return new UsuarioDAOBinarioImpl();
            default:
                throw new IllegalArgumentException("Persistência errada!");
        }
    }

}