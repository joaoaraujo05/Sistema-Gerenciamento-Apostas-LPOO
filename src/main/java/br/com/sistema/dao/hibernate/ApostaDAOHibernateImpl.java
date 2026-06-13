package br.com.sistema.dao.hibernate;

import br.com.sistema.dao.IApostaDAO;
import br.com.sistema.model.entities.Aposta;
import br.com.sistema.model.exceptions.PersistenciaException;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import java.util.List;

public class ApostaDAOHibernateImpl implements IApostaDAO {

    @Override
    public void salvarAposta(Aposta a) {
        Transaction tx = null;

        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();

            if (a != null) {
                session.save(a);
            }

            tx.commit();
        } catch (HibernateException e) {
            if (tx != null) {
                tx.rollback();
            }
            throw new PersistenciaException("Erro ao salvar a aposta: " + e.getMessage());
        }
    }

    @Override
    public void atualizarAposta(Aposta a) {
        Transaction tx = null;

        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();

            if (a != null) {
                session.merge(a);
            }

            tx.commit();
        } catch (HibernateException e) {
            if (tx != null) {
                tx.rollback();
            }
            throw new PersistenciaException("Erro ao atualizar a aposta: " + e.getMessage());
        }
    }

    @Override
    public void removerApostaPorId(Long id) {
        if (id == null) {
            throw new RuntimeException("É necessário informar um ID!");
        }

        Transaction tx = null;

        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            Aposta a = session.get(Aposta.class, id);

            if (a != null) {
                session.delete(a);
            }

            tx.commit();
        } catch (HibernateException e) {
            if (tx != null) {
                tx.rollback();
            }
            throw new PersistenciaException("Erro ao remover a aposta: " + e.getMessage());
        }
    }

    @Override
    public Aposta encontrarApostaPorId(Long id) {
        if (id == null) {
            throw new RuntimeException("É necessário informar um ID!");
        }

        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.get(Aposta.class, id);
        } catch (HibernateException e) {
            throw new PersistenciaException("Erro ao encontrar uma aposta: " + e.getMessage());
        }
    }

    @Override
    public List<Aposta> listarApostas() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("FROM Aposta", Aposta.class).list();
        } catch (HibernateException e) {
            throw new PersistenciaException("Erro ao listar todas as apostas: " + e.getMessage());
        }
    }

    @Override
    public List<Aposta> listarApostasPorPartida(Long idPartida) {
        if (idPartida == null) {
            throw new RuntimeException("É necessário informar um ID de partida!");
        }

        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query<Aposta> query = session.createQuery("FROM Aposta WHERE partida.idPartida = :id", Aposta.class);
            query.setParameter("id", idPartida);
            return query.list();
        } catch (HibernateException e) {
            throw new PersistenciaException("Erro ao listar apostas da partida: " + e.getMessage());
        }
    }
}