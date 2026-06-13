package br.com.sistema.dao.hibernate;

import br.com.sistema.dao.IPartidaDAO;
import br.com.sistema.model.entities.Partida;
import br.com.sistema.model.exceptions.PersistenciaException;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.util.List;

public class PartidaDAOHibernateImpl implements IPartidaDAO {

    @Override
    public void salvarPartida(Partida p) {
        Transaction tx = null;

        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();

            if (p != null) {
                session.save(p);
            }

            tx.commit();
        } catch (HibernateException e) {
            if (tx != null) {
                tx.rollback();
            }
            throw new PersistenciaException("Erro ao salvar a partida: " + e.getMessage());
        }
    }

    @Override
    public void atualizarPartida(Partida p) {
        Transaction tx = null;

        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();

            if (p != null) {
                session.merge(p);
            }

            tx.commit();
        } catch (HibernateException e) {
            if (tx != null) {
                tx.rollback();
            }
            throw new PersistenciaException("Erro ao atualizar a partida: " + e.getMessage());
        }
    }

    @Override
    public void removerPartidaPorId(Long id) {
        if (id == null) {
            throw new RuntimeException("É necessário informar um ID!");
        }

        Transaction tx = null;

        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            Partida p = session.get(Partida.class, id);

            if (p != null) {
                session.delete(p);
            }

            tx.commit();
        } catch (HibernateException e) {
            if (tx != null) {
                tx.rollback();
            }
            throw new PersistenciaException("Erro ao remover a partida: " + e.getMessage());
        }
    }

    @Override
    public Partida encontrarPartidaPorId(Long id) {
        if (id == null) {
            throw new RuntimeException("É necessário informar um ID!");
        }

        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.get(Partida.class, id);
        } catch (HibernateException e) {
            throw new PersistenciaException("Erro ao encontrar uma partida: " + e.getMessage());
        }
    }

    @Override
    public List<Partida> listarPartidas() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("FROM Partida", Partida.class).list();
        } catch (HibernateException e) {
            throw new PersistenciaException("Erro ao listar todas as partidas: " + e.getMessage());
        }
    }
}