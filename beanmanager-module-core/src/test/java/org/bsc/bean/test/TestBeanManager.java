package org.bsc.bean.test;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import junit.framework.Assert;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import org.bsc.bean.BeanManager;
import org.bsc.bean.BeanManagerFactory;
import org.bsc.bean.StoreConstraints;
import org.bsc.bean.metadata.TableBean;
import org.bsc.bean.metadata.TableBeanInfo;
import org.junit.AfterClass;
import static org.bsc.bean.test.BaseTestUtils.connect;
import static org.bsc.bean.test.BaseTestUtils.disconnect;
import static org.bsc.bean.test.BaseTestUtils.executeCommands;
import static org.bsc.bean.test.BaseTestUtils.initLogger;
import static org.bsc.bean.test.BaseTestUtils.loadDriver;

/**
 * 
 * @author Sorrentino
 *
 */
public class TestBeanManager {
	private static BeanManager<Customer> customerManager = null; 
	private static BeanManager<Account> accountManager = null; 
	private static BeanManager<CustomerAccount> customerAccountManager = null; 

        private Connection conn = null;
	
	@BeforeClass
	@SuppressWarnings("unchecked")
	public static void init() throws Exception {
            
            loadDriver();
            
            initLogger();
            
            Connection conn = connect();
            
            try { 
                
                createDB( conn );

                BeanManagerFactory factory = BeanManagerFactory.getFactory();
                
                customerManager = (BeanManager<Customer>)factory.createBeanManager(Customer.class);
                accountManager = (BeanManager<Account>) factory.createBeanManager(Account.class);
                customerAccountManager = (BeanManager<CustomerAccount>) factory.createBeanManager(CustomerAccount.class);
            }
            finally {
                disconnect( conn );
            }
        }
  	
        @AfterClass
	public static void term() throws Exception {
	
            Connection conn = connect();
            
            try {
            dropDB( conn );
            }
            finally {
                disconnect( conn );
            }
                
	}

      
	@Before
	@SuppressWarnings("unchecked")
	public void openConnection() throws Exception {

                conn = connect(); 
		
	
        }
	
	@After
	public void closeConnection() throws Exception {
	
            disconnect( conn );
	}
	
	public static void createDB( Connection conn ) throws Exception {

		String sql[] = {
			"CREATE TABLE CUSTOMER ( " +
				"FIRST_NAME VARCHAR(30) NOT NULL," +
				"LAST_NAME VARCHAR(30) NOT NULL," +
				"ID INTEGER NOT NULL CONSTRAINT EMP_NO_PK PRIMARY KEY,"+
				"ACCOUNT_ID INTEGER NOT NULL, "+
				"VIP CHAR(1) NOT NULL "+
				")"
                                ,
                         "CREATE TABLE ACCOUNT ( " +
				"NUMBER VARCHAR(30) NOT NULL," +
				"BALANCE INTEGER DEFAULT 0 NOT NULL," +
				"ID INTEGER NOT NULL CONSTRAINT ACC_NO_PK PRIMARY KEY"+
				")"
                };
		
		executeCommands( conn, sql );
		
		
	}

        public static void dropDB( Connection conn ) throws Exception {

		String sql[] = {
                    "DROP TABLE CUSTOMER"
                            ,
                    "DROP TABLE ACCOUNT"
                            
                };
             
		
		executeCommands(conn, sql);
		
		
	}

        @Test
	public void getTables() throws Exception {
             String catalog = null; 
             String schemaPattern = null; 

            
            DatabaseMetaData dbmd = conn.getMetaData();
            
            ResultSet rs = dbmd.getTables(  catalog, schemaPattern, "%", null );
            
            BeanManager<TableBean> manager = (BeanManager<TableBean>) BeanManagerFactory.getFactory().createBeanManager(TableBean.class, new TableBeanInfo());
        
            System.out.printf( "==== TABLES ====\n");
            
            while( rs.next() ) {

                TableBean bean = manager.instantiateBean();

                manager.setBeanProperties(bean, rs);

                System.out.printf( "table.schema=%s table.name=%s table.type=%s\n", bean.getSchema(), bean.getName(), bean.getType());
                
            }

        }
        
	@Test
        public void customerManagement() throws Exception {
            
            createCustomer( 100 );
        
            CustomerAccount ca = customerAccountManager.findById(conn, 1);
            
            findCustomerById();
            
            updateCustomer( "BARTOLOMEO", "SORRENTINO" );
            
            updateCustomerInclude( "LUIGI");
            

            findCustomers();
            
            removeCustomer();
            
        }
        
        
	Customer createCustomer( int id ) throws SQLException {
		
                Account account = createAccount(id);
                
		Customer bean = new Customer();
		
		bean.setCustomerId(1);
		bean.setFirstName("name1");
		bean.setLastName("sname1");
                bean.setAccountId( account.getAccountId() );
		
		customerManager.create(conn, bean);
		
                return bean;
		
	}

        Account createAccount( int id) throws SQLException {
		
		Account bean = new Account();
		
		bean.setAccountId(id);
		bean.setBalance(1000);
		bean.setNumber("N0001");
		
		accountManager.create(conn, bean);
		
                return bean;
		
	}
	
	void findCustomerById() throws SQLException {
		
		Customer bean = customerManager.findById(conn, 1);
		
		Assert.assertNotNull("Customer retreived is null", bean );
		Assert.assertEquals( "Customer.id doesn't match", bean.getCustomerId(), 1 );
		Assert.assertFalse( "Customer.vip isn't false", bean.isVip() );
		
		
	}

	void updateCustomerInclude( String firstName ) throws SQLException {
		
		Customer bean = new Customer();
		
		bean.setCustomerId(1);
		bean.setFirstName(firstName);
		
		customerManager.store(conn, bean, StoreConstraints.INCLUDE_PROPERTIES, "firstName");
		
		bean = customerManager.findById(conn, 1);
		
		Assert.assertNotNull("Customer retreived is null", bean );
		Assert.assertEquals( "Customer.id doesn't match", bean.getCustomerId(), 1 );
		Assert.assertEquals( "Customer.firstName doesn't match", bean.getFirstName(), firstName );
		
		
	}

	void updateCustomernExclude() throws SQLException {
		
		Customer bean = new Customer();
		
		bean.setCustomerId(1);
		bean.setLastName("sorrentino");
		
		customerManager.store(conn, bean, StoreConstraints.EXCLUDE_PROPERTIES, "firstName");
		
		bean = customerManager.findById(conn, 1);
		
		Assert.assertNotNull("Customer retreived is null", bean );
		Assert.assertEquals( "Customer.id doesn't match", bean.getCustomerId(), 1 );
		Assert.assertEquals( "Customer.lastName doesn't match", bean.getLastName(), "sorrentino" );
		
		
	}

	void updateCustomer( String firstName, String lastName ) throws SQLException {
		
		Customer bean = customerManager.findById(conn, 1);
		
		Assert.assertNotNull("Customer retreived is null", bean );
		Assert.assertEquals( "Customer.id doesn't match", bean.getCustomerId(), 1 );

		bean.setFirstName(firstName);
		bean.setLastName(lastName);
		customerManager.store(conn, bean);
		
		bean = customerManager.findById(conn, 1);
		
		Assert.assertNotNull("Customer retreived is null", bean );
		Assert.assertEquals( "Customer.id doesn't match", bean.getCustomerId(), 1 );
		Assert.assertEquals( "Customer.firstName doesn't match", bean.getFirstName(), firstName );
		Assert.assertEquals( "Customer.lastName doesn't match", bean.getLastName(), lastName );
		
		
	}
	
	public void findCustomers() throws SQLException {
		List<Customer> customers = new ArrayList<Customer>();
		
		customerManager.find(conn, customers, "${firstName} LIKE ? OR ${lastName}=? ORDER BY #{lastName}", "b%", "sorrentino");
		
		for( Customer c: customers ) {
			
			System.out.printf( "Customer name=%s, surname=%s, id=%d\n", c.getFirstName(), c.getLastName(), c.getCustomerId());
		}
		
	}
	
	public void removeCustomer() throws SQLException {
		
		customerManager.removeById(conn, 1);
		
		
	}
		
}
