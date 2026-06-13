package br.com.sistema.dao.hibernate;

import br.com.sistema.dao.IUsuarioDAO;
import br.com.sistema.model.entities.Usuario;
import br.com.sistema.model.entities.UsuarioParticipante;
import br.com.sistema.model.exceptions.PersistenciaException;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import java.util.List;

public class UsuarioDAOHibernateImpl implements IUsuarioDAO {

    @Override
    public void salvarUsuario(Usuario u) {
        Transaction tx = null;

        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();

            if (u != null) {
                session.save(u);
            }

            tx.commit();
        } catch (HibernateException e) {
            if (tx != null) {
                tx.rollback();
            }
            throw new PersistenciaException("Erro ao salvar o usuario: " + e.getMessage());
        }
    }

    @Override
    public void atualizarUsuario(Usuario u) {
        Transaction tx = null;

        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();

            if (u != null) {
                session.merge(u);
            }

            tx.commit();
        } catch (HibernateException e) {
            if (tx != null) {
                tx.rollback();
            }
            throw new PersistenciaException("Erro ao atualizar o usuario: " + e.getMessage());
        }
    }

    @Override
    public void removerUsuarioPorId(Long id) {
        if (id == null) {
            throw new RuntimeException("É necessário informar um ID!");
        }

        Transaction tx = null;

        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            Usuario u = session.get(Usuario.class, id);

            if (u != null) {
                session.delete(u);
            }

            tx.commit();
        } catch (HibernateException e) {
            if (tx != null) {
                tx.rollback();
            }
            throw new PersistenciaException("Erro ao remover o usuario: " + e.getMessage());
        }
    }

    @Override
    public Usuario encontrarUsuarioPorId(Long id) {
        if (id == null) {
            throw new RuntimeException("É necessário informar um ID!");
        }

        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.get(Usuario.class, id);
        } catch (HibernateException e) {
            throw new PersistenciaException("Erro ao encontrar o usuario: " + e.getMessage());
        }
    }

    @Override
    public Usuario encontrarPorLogin(String login) {
        if (login == null) {
            throw new RuntimeException("É necessário informar um login!");
        }

        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query<Usuario> query = session.createQuery("FROM Usuario WHERE login = :login", Usuario.class);
            query.setParameter("login", login);
            return query.uniqueResult();
        } catch (HibernateException e) {
            throw new PersistenciaException("Erro ao buscar usuario por login: " + e.getMessage());
        }
    }

    @Override
    public List<UsuarioParticipante> listarParticipantes() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("FROM UsuarioParticipante", UsuarioParticipante.class).list();
        } catch (HibernateException e) {
            throw new PersistenciaException("Erro ao listar os participantes: " + e.getMessage());
        }
    }

    @Override
    public List<UsuarioParticipante> listarParticipantesPorGrupo(Long idGrupo) {
        if (idGrupo == null) {
            throw new RuntimeException("É necessário informar um ID de grupo!");
        }

        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query<UsuarioParticipante> query = session.createQuery(
                    "SELECT DISTINCT p FROM UsuarioParticipante p JOIN p.grupos g WHERE g.idGrupo = :id", UsuarioParticipante.class);
            query.setParameter("id", idGrupo);
            return query.list();
        } catch (HibernateException e) {
            throw new PersistenciaException("Erro ao listar participantes do grupo: " + e.getMessage());
        }
    }
}