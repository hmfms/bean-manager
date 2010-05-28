package org.bsc.bean.jpa;

import org.junit.Ignore;
import org.junit.After;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Hello world!
 *
 */
public class TopLInkJPATest
{

    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();


    }

    @Before
    public void openEMF() {
        emf = Persistence.createEntityManagerFactory("org.bsc_beanmanager-jpa_jar_1.0-SNAPSHOTPU");
    }

    @After
    public void closeEMF() {
        emf.close();
    }

    @Test
    public void inheritance() {
    	final String id = "ID1";
    	
        EntityManager em = null;
         try {
             em = emf.createEntityManager();
             em.getTransaction().begin();
             
             MyBean bean1 = em.find(MyBean.class, id);
             if( bean1!=null )
             	em.remove(bean1);
             
             MyBean bean = new MyBean();

             bean.setId(id);
             bean.setContact( "TEST");

             em.persist(bean);

             em.getTransaction().commit();
         } finally {
             if (em != null) {
                 em.close();
             }
         }
    	
    }
    
    @Test
    //@Ignore
    public void createJOINED() {

    	final String id = "ID1";
    	
       EntityManager em = null;
        try {
            em = emf.createEntityManager();
            em.getTransaction().begin();
            
            MyEntityBean1 bean1 = em.find(MyEntityBean1.class, id);
            if( bean1!=null )
            	em.remove(bean1);
            
            MyEntityBean1 bean = new MyEntityBean1();

            bean.setId(id);
            bean.setProperty1_1( "1.1");
            bean.setProperty1_2("1.2");
            bean.setProperty2_1("2.1");
            bean.setProperty2_2("2.2");

            em.persist(bean);

            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }

 
    }


    @Test 
    //@Ignore
    public void create(  )
    {
        System.out.println( "Hello World!" );


        MyDataJpaController myDataController = new MyDataJpaController();

        MyUserJpaController myUserController = new MyUserJpaController();

        MyData data = new MyData();
        data.setBalance(1000L);

        myDataController.create(data);

        MyUser user = new MyUser();

        user.setName( "TEST" );
        user.setData(data);


        myUserController.create(user);

        MyUser u = myUserController.findMyUser(user.getId());
        
        assertNotNull( u );
        
        assertNotNull( u.getData() );

        myDataController.close();
        myUserController.close();


        
    }
}
