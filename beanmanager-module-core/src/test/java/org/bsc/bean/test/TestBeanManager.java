package org.bsc.bean.test;

import java.beans.BeanInfo;
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

import org.apache.ddlutils.model.Database;
import org.apache.ddlutils.Platform;
import org.apache.ddlutils.PlatformFactory;


/**
 * 
 * @author Sorrentino
 *
 */
public class TestBeanManager extends BaseTestUtils {
	private static BeanManager<Customer> customerManager = null; 
	private static BeanManager<BankAccount> accountManager = null; 
	private static BeanManager<CustomerAccount> customerAccountManager = null; 

        private Connection conn = null;
	
	@BeforeClass
	@SuppressWarnings("unchecked")
	public static void init() throws Exception {
            
            loadDriver();
            
            initLogger();
            
            Connection conn = connect();
            
            try { 
                
                
                BeanManagerFactory factory = BeanManagerFactory.getFactory();
                
                customerManager = (BeanManager<Customer>)factory.createBeanManager(Customer.class);
                accountManager = (BeanManager<BankAccount>) factory.createBeanManager(BankAccount.class);
                customerAccountManager = (BeanManager<CustomerAccount>) factory.createBeanManager(CustomerAccount.class);
                
		Database db = new Database();
		
		createTablesModel(db,                         
                        new CustomerBeanInfo(),
                        new BankAccountBeanInfo(),
                        new CustomerAccountBeanInfo() 
                        );
		
              
		Platform p = PlatformFactory.createNewPlatformInstance(DRIVER, CONNECTION_URL);
		
                // void createTables(Connection connection, Database model, boolean dropTablesFirst, boolean continueOnError)
                // Creates the tables defined in the database model.
                p.createTables( conn, db, true, true);

                
            }
            finally {
                disconnect( conn );
            }
        }
  	
        @AfterClass
	public static void term() throws Exception {
	                
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
	
        //@Test
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
            
            final int id = 100;
            
            createCustomer( id );
            findCustomerById( id );
        
            CustomerAccount ca = customerAccountManager.findById(conn, 1);
            
            updateCustomer( id,  "BARTOLOMEO", "SORRENTINO" );
            
            updateCustomerInclude( id, "LUIGI");
         
            findCustomers();
            
            removeCustomer( id );
            
        }
        
        
	Customer createCustomer( int id ) throws SQLException {
		
                BankAccount account = createAccount(id);
                
		Customer bean = new Customer();
		
		bean.setCustomerId(id);
		bean.setFirstName("name1");
		bean.setLastName("sname1");
                bean.setAccountId( account.getAccountId() );
                bean.setVip(true);
                bean.setNote( "XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX");
		
		customerManager.create(conn, bean);
		
                return bean;
		
	}

        BankAccount createAccount( int id) throws SQLException {
		
		BankAccount bean = new BankAccount();
		
		bean.setAccountId(id);
		bean.setBalance(1000);
		bean.setNumber("N0001");
		
		accountManager.create(conn, bean);
		
                return bean;
		
	}
	
	void findCustomerById( int id) throws SQLException {
		
		Customer bean = customerManager.findById(conn, id);
		
                
		Assert.assertNotNull("Customer retreived is null", bean );
		Assert.assertEquals( "Customer.id doesn't match", bean.getCustomerId(), id );
		Assert.assertTrue( "Customer.vip isn't true", bean.isVip() );
		Assert.assertNotNull( "Customer.note is Null", bean.getNote());
                
                System.out.printf( "Customer %s\n", bean );
		
	}

	void updateCustomerInclude( int id, String firstName ) throws SQLException {
		
		Customer bean = new Customer();
		
		bean.setCustomerId(id);
		bean.setFirstName(firstName);
		
		customerManager.store(conn, bean, StoreConstraints.INCLUDE_PROPERTIES, "firstName");
		
		bean = customerManager.findById(conn, id);
		
		Assert.assertNotNull("Customer retreived is null", bean );
		Assert.assertEquals( "Customer.id doesn't match", bean.getCustomerId(), id );
		Assert.assertEquals( "Customer.firstName doesn't match", bean.getFirstName(), firstName );
		
		
	}

	void updateCustomernExclude( int id ) throws SQLException {
		
		Customer bean = new Customer();
		
		bean.setCustomerId(id);
		bean.setLastName("sorrentino");
		
		customerManager.store(conn, bean, StoreConstraints.EXCLUDE_PROPERTIES, "firstName");
		
		bean = customerManager.findById(conn, id);
		
		Assert.assertNotNull("Customer retreived is null", bean );
		Assert.assertEquals( "Customer.id doesn't match", bean.getCustomerId(), id );
		Assert.assertEquals( "Customer.lastName doesn't match", bean.getLastName(), "sorrentino" );
		
		
	}

	void updateCustomer( int id,  String firstName, String lastName ) throws SQLException {
		
		Customer bean = customerManager.findById(conn, id);
		
		Assert.assertNotNull("Customer retreived is null", bean );
		Assert.assertEquals( "Customer.id doesn't match", bean.getCustomerId(), id );

		bean.setFirstName(firstName);
		bean.setLastName(lastName);
		customerManager.store(conn, bean);
		
		bean = customerManager.findById(conn, id);
		
		Assert.assertNotNull("Customer retreived is null", bean );
		Assert.assertEquals( "Customer.id doesn't match", bean.getCustomerId(), id );
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
	
	public void removeCustomer( int id ) throws SQLException {
		
		customerManager.removeById(conn, id);
		
		
	}
		
}
