package org.bsc.bean.test;

import java.sql.Connection;


import org.junit.BeforeClass;

import org.bsc.bean.BeanManager;
import org.bsc.bean.BeanManagerFactory;
import org.bsc.bean.dyna.DynaManagedBeanInfo;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.apache.commons.beanutils.DynaBean;
import org.apache.commons.beanutils.DynaProperty;
import org.apache.commons.beanutils.ResultSetDynaClass;
import org.bsc.bean.StoreConstraints;
import org.bsc.bean.metadata.ColumnBean;
import org.bsc.bean.metadata.ColumnBeanInfo;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import static org.bsc.bean.test.BaseTestUtils.*;
/**
 * 
 * @author Sorrentino
 *
 */
public class TestDynaBeanManager {
	private static BeanManager<DynaCustomer> manager = null; 
	
        Connection conn;
	
	@BeforeClass
	@SuppressWarnings("unchecked")
	public static void init() throws Exception {
            
            loadDriver();
            
            initLogger();
            
            Connection conn = connect();
            
            try { 
                
            createTable( conn );

            //
            // CREATE META DATA (ON THE FLY)
            //
            
            
            DynaManagedBeanInfo beanInfo = new DynaCustomerBeanInfo();
            
            beanInfo.addDynaPropertyDescriptors( conn.getMetaData(), null, null, "get", "set");
            
	    manager = (BeanManager<DynaCustomer>) BeanManagerFactory.getFactory().createBeanManager(DynaCustomer.class, beanInfo);
             
            }
            finally {
                disconnect( conn );
            }
            
        }
	
	@AfterClass
	public static void term() throws Exception {
	
            Connection conn = connect();
            
            try {
            dropTable( conn );
            }
            finally {
                disconnect( conn );
            }
                
	}
	
        
	
	private static void createTable( Connection conn ) throws Exception {

		String sql[] = { 
			"CREATE TABLE DCUSTOMER ( " +
				"FIRST_NAME VARCHAR(30) NOT NULL," +
				"LAST_NAME VARCHAR(30) NOT NULL," +
                                "AGE INTEGER DEFAULT 30," +
				"ID INTEGER NOT NULL CONSTRAINT EMP_NO_PK PRIMARY KEY"+
				")" 
                                , 
                                
			"CREATE TABLE DCUSTOMER_EXT ( " +
				"ID INTEGER NOT NULL CONSTRAINT EMP_EX_NO_PK PRIMARY KEY"+
				")" 
                };
		
                executeCommands(conn, sql);
	}

	private static void dropTable( Connection conn ) throws Exception {

		String sql[] = {
                    "DROP TABLE DCUSTOMER",
                    "DROP TABLE DCUSTOMER_EXT"                      
                };
		
                executeCommands(conn, sql);

		
	}

        @Before
        public void openConnection() throws Exception {
            conn = connect();
        }
        
        @After
        public void closeConnection() throws Exception {
            disconnect( conn );
        }
        
        //@Test
        public void test100_describeTable() throws Exception {
            DatabaseMetaData md = conn.getMetaData();
                        
            ResultSet rs = md.getColumns(null, null, "DCUSTOMER", null);

            BeanManager<ColumnBean> columnsManager = (BeanManager<ColumnBean>) BeanManagerFactory.getFactory().createBeanManager(ColumnBean.class, new ColumnBeanInfo());

            System.out.printf( "DESCRIBE TABLE\n"  );

            while( rs.next() ) {
    
                ColumnBean bean = columnsManager.instantiateBean();

                columnsManager.setBeanProperties(bean, rs);

                System.out.println( bean );
            }
        }

        /**
         * 
         * @throws java.sql.SQLException
         */
        //@Test
        public void test101_describeTableJDBC() throws SQLException {
        
            DatabaseMetaData md = conn.getMetaData();
                        
            //ResultSet rs = md.getTables(null, null, "C%", null);
            ResultSet rs = md.getColumns(null, null, "DCUSTOMER", null);
            //ResultSet rs = md.getSchemas();
 
             ResultSetDynaClass rsdc = new ResultSetDynaClass(rs);
             
             Iterator<DynaBean> i = rsdc.iterator();

             System.out.printf( "DESCRIBE TABLE\n"  );
             
             while( i.hasNext()  ) {
                 
                 
                 DynaBean bean = i.next();

                 System.out.printf( "DESCRIBE TABLE %s\n", bean  );
                 
                 for( DynaProperty p : bean.getDynaClass().getDynaProperties() ) {

                     System.out.printf( "COLUMN %s=%s\n", p.getName(), bean.get(p.getName())  );
                     
                 }
             }
         }
        
	@Test
	public void test200_createBean() throws Exception {
		
            for( int i=1; i < 10 ; ++i ) {
		DynaCustomer bean = manager.instantiateBean();
		   
		bean.setId(i);
		
                bean.set( "FIRST_NAME", "name"+i);
		bean.set( "LAST_NAME", "sname"+i);
		
                bean.set("AGE", 20+i);
                
		manager.create(conn, bean);
		
            }
	}
	
	@Test
	public void test300_findBeanById() throws SQLException {
		
		DynaCustomer bean = manager.findById(conn, 1);
		
		Assert.assertNotNull("Customer retreived is null", bean );
		Assert.assertEquals( "Customer.id doesn't match", bean.getId(), 1 );
		
		
	}


	@Test
	public void test400_updateBeanInclude() throws Exception {
		
		DynaCustomer bean = manager.instantiateBean();
		
		bean.setId(1);
		bean.set( "FIRST_NAME", "bartolomeo");
		
		manager.store(conn, bean, StoreConstraints.INCLUDE_PROPERTIES, "FIRST_NAME");
		
		bean = manager.findById(conn, 1);
		
		Assert.assertNotNull("Customer retreived is null", bean );
		Assert.assertEquals( "Customer.id doesn't match", bean.getId(), 1 );
		Assert.assertEquals( "Customer.firstName doesn't match", bean.get("FIRST_NAME"), "bartolomeo" );
		
		
	}

                
/*                
	@Test
	public void updateBeanExclude() throws SQLException {
		
		Customer bean = new Customer();
		
		bean.setId(1);
		bean.setLastName("sorrentino");
		
		manager.store(conn, bean, false, "firstName");
		
		bean = manager.findById(conn, 1);
		
		Assert.assertNotNull("Customer retreived is null", bean );
		Assert.assertEquals( "Customer.id doesn't match", bean.getId(), 1 );
		Assert.assertEquals( "Customer.lastName doesn't match", bean.getLastName(), "sorrentino" );
		
		
	}

	@Test
	public void updateBean() throws SQLException {
		
		Customer bean = manager.findById(conn, 1);
		
		Assert.assertNotNull("Customer retreived is null", bean );
		Assert.assertEquals( "Customer.id doesn't match", bean.getId(), 1 );

		bean.setFirstName("bartolo");
		
		manager.store(conn, bean);
		
		bean = manager.findById(conn, 1);
		
		Assert.assertNotNull("Customer retreived is null", bean );
		Assert.assertEquals( "Customer.id doesn't match", bean.getId(), 1 );
		Assert.assertEquals( "Customer.firstName doesn't match", bean.getFirstName(), "bartolo" );
		
		
	}
*/	
	@Test
	public void test500_findAllBeans() throws SQLException {
		List<DynaCustomer> customers = new ArrayList<DynaCustomer>();
		
		manager.findAll(conn, customers, "");
		
		for( DynaCustomer c: customers ) {
			
			System.out.printf( "Customer %s\n", c);
		}
		
	}
	
	@Test
	public void test600_removeBean() throws SQLException {
		
		manager.removeById(conn, 1);
		
		
	}
		
}
