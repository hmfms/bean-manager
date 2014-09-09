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
public class MyUserJpaController {

    public MyUserJpaController() {
        emf = Persistence.createEntityManagerFactory("org.bsc_beanmanager-jpa_jar_1.0-SNAPSHOTPU");
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(MyUser myUser) {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            em.persist(myUser);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(MyUser myUser) throws NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            myUser = em.merge(myUser);
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                String id = myUser.getId();
                if (findMyUser(id) == null) {
                    throw new NonexistentEntityException("The myUser with id " + id + " no longer exists.");
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
            MyUser myUser;
            try {
                myUser = em.getReference(MyUser.class, id);
                myUser.getId();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The myUser with id " + id + " no longer exists.", enfe);
            }
            em.remove(myUser);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<MyUser> findMyUserEntities() {
        return findMyUserEntities(true, -1, -1);
    }

    public List<MyUser> findMyUserEntities(int maxResults, int firstResult) {
        return findMyUserEntities(false, maxResults, firstResult);
    }

    private List<MyUser> findMyUserEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(MyUser.class));
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

    public MyUser findMyUser(String id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(MyUser.class, id);
        } finally {
            em.close();
        }
    }

    public int getMyUserCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<MyUser> rt = cq.from(MyUser.class);
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
