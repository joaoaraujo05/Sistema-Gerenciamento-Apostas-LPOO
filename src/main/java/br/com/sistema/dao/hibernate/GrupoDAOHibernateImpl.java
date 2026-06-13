package br.com.sistema.dao.hibernate;

import br.com.sistema.dao.IGrupoDAO;
import br.com.sistema.model.entities.Grupo;
import br.com.sistema.model.exceptions.PersistenciaException;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.util.List;

public class GrupoDAOHibernateImpl implements IGrupoDAO {

    @Override
    public void salvarGrupo(Grupo g) {
        Transaction tx = null;

        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();

            if (g != null) {
                session.save(g);
            }

            tx.commit();
        } catch (HibernateException e) {
            if (tx != null) {
                tx.rollback();
            }
            throw new PersistenciaException("Erro ao salvar o grupo: " + e.getMessage());
        }
    }

    @Override
    public void atualizarGrupo(Grupo g) {
        Transaction tx = null;

        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();

            if (g != null) {
                session.merge(g);
            }

            tx.commit();
        } catch (HibernateException e) {
            if (tx != null) {
                tx.rollback();
            }
            throw new PersistenciaException("Erro ao atualizar o grupo: " + e.getMessage());
        }
    }

    @Override
    public void removerGrupoPorId(Long id) {
        if (id == null) {
            throw new RuntimeException("É necessário informar um ID!");
        }

        Transaction tx = null;

        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            Grupo g = session.get(Grupo.class, id);

            if (g != null) {
                session.delete(g);
            }

            tx.commit();
        } catch (HibernateException e) {
            if (tx != null) {
                tx.rollback();
            }
            throw new PersistenciaException("Erro ao remover o grupo: " + e.getMessage());
        }
    }

    @Override
    public Grupo encontrarGrupoPorId(Long id) {
        if (id == null) {
            throw new RuntimeException("É necessário informar um ID!");
        }

        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.get(Grupo.class, id);
        } catch (HibernateException e) {
            throw new PersistenciaException("Erro ao encontrar um grupo: " + e.getMessage());
        }
    }

    @Override
    public List<Grupo> listarGrupos() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("FROM Grupo", Grupo.class).list();
        } catch (HibernateException e) {
            throw new PersistenciaException("Erro ao listar todos os grupos: " + e.getMessage());
        }
    }
}