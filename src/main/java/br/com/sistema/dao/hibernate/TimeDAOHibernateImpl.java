package br.com.sistema.dao.hibernate;

import br.com.sistema.dao.ITimeDAO;
import br.com.sistema.model.entities.Time;
import br.com.sistema.model.exceptions.PersistenciaException;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.util.List;

public class TimeDAOHibernateImpl implements ITimeDAO {

    @Override
    public void salvarTime(Time t) {
        Transaction tx = null;

        try (Session session = HibernateUtil.getSessionFactory().openSession()) {

            tx = session.beginTransaction();

            if (t != null) {
                session.save(t);
            }

            tx.commit();

        } catch (HibernateException e) {
            if (tx != null) {
                tx.rollback();
            }
            throw new PersistenciaException("Erro ao salvar um time: " + e.getMessage());
        }
    }

    @Override
    public void atualizarTime(Time t) {
        Transaction tx = null;

        try (Session session = HibernateUtil.getSessionFactory().openSession()) {

            tx = session.beginTransaction();

            if (t != null) {
                session.merge(t);
            }

            tx.commit();

        } catch (HibernateException e) {
            if (tx != null) {
                tx.rollback();
            }

            throw new PersistenciaException("Erro ao atualizar um time: " + e.getMessage());
        }
    }

    @Override
    public void removerTimePorId(Long id) {
        if (id == null) {
            throw new RuntimeException("É necessário informar um ID!");
        }

        Transaction tx = null;

        try (Session session = HibernateUtil.getSessionFactory().openSession()) {

            tx = session.beginTransaction();
            Time t = session.get(Time.class, id);

            if (t != null) {
                session.delete(t);
            }

            tx.commit();

        } catch (HibernateException e) {
            if (tx != null) {
                tx.rollback();
            }

            throw new PersistenciaException("Erro ao remover o time: " + e.getMessage());
        }
    }

    @Override
    public Time encontrarTimePorId(Long id) {
        if (id == null) {
            throw new RuntimeException("É necessário informar um ID!");
        }

        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.get(Time.class, id);
        } catch (HibernateException e) {
            throw new PersistenciaException("Erro ao encontrar um time: " + e.getMessage());
        }
    }

    @Override
    public List<Time> listarTimes() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("FROM Time", Time.class).list();
        } catch (HibernateException e) {
            throw new PersistenciaException("Erro ao listar todos os times: " + e.getMessage());
        }
    }
}
