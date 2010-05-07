/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.bsc.bean.jpa;

import org.bsc.bean.BeanManager;
import java.sql.DriverManager;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Properties;
import java.util.logging.LogManager;
import org.junit.Test;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;

import static org.junit.Assert.*;
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
}
