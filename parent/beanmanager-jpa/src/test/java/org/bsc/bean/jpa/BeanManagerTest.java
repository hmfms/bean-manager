/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.bsc.bean.jpa;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.sql.DriverManager;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Properties;
import java.util.logging.LogManager;
import org.bsc.bean.AbstractBeanManager;


import org.bsc.bean.BeanManager;
import org.bsc.bean.PropertyDescriptorField;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
/**
 *
 * @author softphone
 */
public class BeanManagerTest {
    public static final String DRIVER = "org.apache.derby.jdbc.EmbeddedDriver";
    public static final String CONNECTION_URL = "jdbc:derby:target/LOCALDB;create=true"; ;

    @BeforeClass
    public static void loadDriver() throws Exception {
            Class.forName(DRIVER);
            LogManager.getLogManager().readConfiguration( BeanManagerTest.class.getResourceAsStream("/logging.properties"));
    }


    java.sql.Connection conn;

    @Before
    public void openConnection() throws Exception {
            Properties p = new Properties();
            p.setProperty("create", "true");

	    conn = DriverManager.getConnection(CONNECTION_URL, p);

    }

    @After
    public void closeConnection() throws Exception {
            if( null!=conn ) {
                    conn.close();
            }
    }

    @Test
    public void inheritance() throws Exception  {
    	final String id = "ID1";
    	
        BeanManager<MyBean> em = JPABeanManagerFactory.createBeanManager(MyBean.class);
         
         MyBean bean1 = em.findById( conn, id);
         if( bean1!=null )
         	em.remove(conn, bean1);
         
         MyBean bean = new MyBean();

         bean.setId(id);
         bean.setContact( "TEST");
         bean.setBirthDate( new java.util.Date() );
         em.create(conn, bean);

         MyBean bean2 = em.findById( conn, id);
         
         Assert.assertNotNull( bean2 );
         Assert.assertTrue( bean2.getBirthDate() instanceof java.util.Date );
         
    }
    
    @Test //@Ignore
    public void testJOINED() throws Exception {
        BeanManager<MyEntityBean1> myEntityBean1Manager = JPABeanManagerFactory.createBeanManager(MyEntityBean1.class);
        //BeanManager<MyEntityBean2> myEntityBean2Manager = JPABeanManagerFactory.createBeanManager(MyEntityBean2.class);

        String id = null;

    	{
    		MyEntityBean1 bean1 = new MyEntityBean1();
    		bean1.setProperty1_1("@1.1");
    		bean1.setProperty1_2("@1.2");
    		bean1.setProperty2_1("@2.1");
    		bean1.setProperty2_2("@2.2");
    		
    		myEntityBean1Manager.create(conn, bean1);
    		
    		id = bean1.getId();
    		
    	}
   	
    	{
    		MyEntityBean1 bean = myEntityBean1Manager.findById(conn, id);
    		
    		assertNotNull( bean );
    		
    	}
    	
    	{
    		
    		myEntityBean1Manager.removeById(conn, id);
    	}
        
    
        
    }


    @Test 
    public void findAll() throws Exception {


        BeanManager<MyUser> myUserManager = JPABeanManagerFactory.createBeanManager(MyUser.class);
        BeanManager<MyData> myDataManager = JPABeanManagerFactory.createBeanManager(MyData.class);
        
        assertNotNull( myUserManager );
        assertNotNull( myDataManager );

        MyData data = new MyData();
        data.setBalance(1000L);

        myDataManager.create(conn, data);

        MyUser us = new MyUser();
        us.setName( "BSC");
        us.setData( data );
        
        myUserManager.create(conn, us);

        MyUser us1 = myUserManager.findById(conn, us.getId());

        assertEquals( us.getName(), us1.getName());
        assertEquals( "BSC", us1.getName());
        assertEquals( data.getId(), us.getData().getId());

        data.setBalance( 1200 );
        myDataManager.store(conn, data);
        
        us1 = myUserManager.findById(conn, us.getId());

        assertEquals( us.getName(), us1.getName());
        assertEquals( "BSC", us1.getName());
        assertEquals( data.getId(), us.getData().getId());
        assertEquals( 1200L, us.getData().getBalance());
        

        Collection<MyUser> users = myUserManager.findAll(conn, new ArrayList<MyUser>(), null);
        
        for( MyUser u : users ) System.out.println( u );

    }

    @Test
    public void checkNonEntityInheritance() {

        AbstractBeanManager<ItemCifF00091> m = (AbstractBeanManager<ItemCifF00091>) JPABeanManagerFactory.createBeanManager(ItemCifF00091.class);

        PropertyDescriptorField fieldM = m.getPropertyByName("M");

        Assert.assertNull(fieldM);

        {
        String cs = m.getCreateStatement();

        System.out.println( "CREATE STATEMENT\n" + cs );
        }

        {
        String cs = m.getFindAllStatement();

        System.out.println( "FINDALL STATEMENT\n" + cs );

        }
    }
}
