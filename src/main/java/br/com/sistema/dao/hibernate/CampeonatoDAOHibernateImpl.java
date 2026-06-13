package br.com.sistema.dao.hibernate;

import br.com.sistema.dao.ICampeonatoDAO;
import br.com.sistema.model.entities.Campeonato;
import br.com.sistema.model.entities.Time;
import br.com.sistema.model.exceptions.PersistenciaException;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.util.List;

public class CampeonatoDAOHibernateImpl implements ICampeonatoDAO {


    @Override
    public void salvarCampeonato(Campeonato c) {
        Transaction tx = null;

        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();

            if (c != null) {
                session.save(c);
            }

            tx.commit();
        } catch (HibernateException e) {
            if (tx != null) {
                tx.rollback();
            }
            throw new PersistenciaException("Erro ao salvar o campeonato: " + e.getMessage());
        }
    }

    @Override
    public void atualizarCampeonato(Campeonato c) {
        Transaction tx = null;

        try (Session session = HibernateUtil.getSessionFactory().openSession()) {

            tx = session.beginTransaction();

            if (c != null) {
                session.merge(c);
            }

            tx.commit();

        } catch (HibernateException e) {
            if (tx != null) {
                tx.rollback();
            }

            throw new PersistenciaException("Erro ao atualizar o campeonato: " + e.getMessage());
        }
    }

    @Override
    public void removerCampeonatoPorId(Long id) {
        if (id == null) {
            throw new RuntimeException("É necessário informar um ID!");
        }

        Transaction tx = null;

        try (Session session = HibernateUtil.getSessionFactory().openSession()) {

            tx = session.beginTransaction();
            Campeonato c = session.get(Campeonato.class, id);

            if (c != null) {
                session.delete(c);
            }

            tx.commit();

        } catch (HibernateException e) {
            if (tx != null) {
                tx.rollback();
            }
            throw new PersistenciaException("Erro ao remover o campeonato: " + e.getMessage());
        }
    }

    @Override
    public Campeonato encontrarCampeonatoPorId(Long id) {
        if (id == null) {
            throw new RuntimeException("É necessário informar um ID!");
        }

        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.get(Campeonato.class, id);
        } catch (HibernateException e) {
            throw new PersistenciaException("Erro ao encontrar um campeonato: " + e.getMessage());
        }
    }

    @Override
    public List<Campeonato> listarCampeonatos() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("FROM Campeonato", Campeonato.class).list();
        } catch (HibernateException e) {
            throw new PersistenciaException("Erro ao listar todos os times: " + e.getMessage());
        }
    }
}
