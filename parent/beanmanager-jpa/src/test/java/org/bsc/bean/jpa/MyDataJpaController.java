/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.bsc.bean.jpa;

import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import org.bsc.bean.jpa.exceptions.NonexistentEntityException;

/**
 *
 * @author softphone
 */
public class MyDataJpaController {

    public MyDataJpaController() {
        emf = Persistence.createEntityManagerFactory("org.bsc_beanmanager-jpa_jar_1.0-SNAPSHOTPU");
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();


    }

    public void create(MyData myData) {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            em.persist(myData);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(MyData myData) throws NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            myData = em.merge(myData);
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                String id = myData.getId();
                if (findMyData(id) == null) {
                    throw new NonexistentEntityException("The myData with id " + id + " no longer exists.");
                }
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void destroy(String id) throws NonexistentEntityException {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            MyData myData;
            try {
                myData = em.getReference(MyData.class, id);
                myData.getId();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The myData with id " + id + " no longer exists.", enfe);
            }
            em.remove(myData);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<MyData> findMyDataEntities() {
        return findMyDataEntities(true, -1, -1);
    }

    public List<MyData> findMyDataEntities(int maxResults, int firstResult) {
        return findMyDataEntities(false, maxResults, firstResult);
    }

    private List<MyData> findMyDataEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(MyData.class));
            Query q = em.createQuery(cq);
            if (!all) {
                q.setMaxResults(maxResults);
                q.setFirstResult(firstResult);
            }
            return q.getResultList();
        } finally {
            em.close();
        }
    }

    public MyData findMyData(String id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(MyData.class, id);
        } finally {
            em.close();
        }
    }

    public int getMyDataCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<MyData> rt = cq.from(MyData.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }

    public void close() {
        emf.close();
    }
}
