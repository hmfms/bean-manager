package org.bsc.bean.test;

import org.bsc.bean.test.beans.CustomerAccountBeanInfo;
import org.bsc.bean.test.beans.CustomerAccount;
import org.bsc.bean.test.beans.BankAccountBeanInfo;
import org.bsc.bean.test.beans.BankAccount;
import org.bsc.bean.test.beans.CustomerBeanInfo;
import org.bsc.bean.test.beans.Customer;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import java.util.Properties;
import junit.framework.Assert;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

import org.bsc.bean.BeanManager;
import org.bsc.bean.BeanManagerFactory;
import org.bsc.bean.StoreConstraints;
import org.bsc.bean.metadata.TableBean;
import org.bsc.bean.metadata.TableBeanInfo;
import org.junit.AfterClass;

import org.apache.ddlutils.model.Database;
import org.apache.ddlutils.Platform;
import org.apache.ddlutils.PlatformFactory;
import org.bsc.bean.BeanManagerUtils;
import org.bsc.util.Configurator;


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
	public static void init() throws Exception {
            
            loadDriver();
            
            initLogger();
            
            Connection conn = connect();
            
            try { 
                
                
                BeanManagerFactory factory = BeanManagerFactory.getFactory();
                
                customerManager = factory.createBeanManager(Customer.class);
                accountManager = factory.createBeanManager(BankAccount.class);
                customerAccountManager = factory.createBeanManager(CustomerAccount.class);
                
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
    public void loadCustomCommands() {

        Properties cc = Configurator.loadCustomCommands();
        //assertEquals("cc.size != 3",  3, cc.size());

        {
        String k = "command0";
        String p = cc.getProperty(k);

        assertNotNull( String.format( "commands[%s] does not exist!", k), p);

        System.out.println(p);

        }

        {
        String k = "command1";
        String p = cc.getProperty(k);

        assertNotNull( String.format( "commands[%s] does not exist!", k), p);

        System.out.println(p);

        }
        
        {
        String k = "command2";
        String p = cc.getProperty(k);

        assertNotNull( String.format( "commands[%s] does not exist!", k), p);

        System.out.println(p);

        }

/*
        Properties commands = Configurator.loadCustomCommands( new File("target/test-classes/commands"), "sql");

        assertEquals("commands.size != 2",  2, commands.size());

        {
        String k = "command1";
        String p = commands.getProperty(k);

        assertNotNull( String.format( "commands[%s] does not exist!", k), p);

        System.out.println(p);

        }

        {
        String k = "command2";
        String p = commands.getProperty(k);

        assertNotNull( String.format( "commands[%s] does not exist!", k), p);

        System.out.println(p);

        }
*/

    }


    /**
     * 
     * @throws java.lang.Exception
     */
    @Test
    public void testINClause() throws Exception {
		Customer bean = new Customer();

        for( int i=0; i<100 ; ++i ) {
            bean.setCustomerId(1000 + i);
            bean.setFirstName("name" + i);
            bean.setLastName("sname" + i);
            bean.setAccountId( i );
            bean.setVip(true);
            bean.setNote( "Note" + i );
            bean.setBirthDate( new java.util.Date() );
            customerManager.create(conn, bean);
        }

        List<String> names = Arrays.asList( "name5", "name6", "name7");

        List<Customer> result  = new ArrayList<Customer>( names.size());

        customerManager.find(conn, result, String.format( "${firstName} IN %s order by #{firstName}", BeanManagerUtils.IN(names) ), names);

        assertTrue( result.size()==3 );

        assertEquals( "name5", result.get(0).getFirstName());
        assertEquals( "name6", result.get(1).getFirstName());
        assertEquals( "name7", result.get(2).getFirstName());

        customerManager.removeAll(conn);

    }

	@Test
    public void customerManagement() throws Exception {
            
            final int id = 100;
            
            final BankAccount account = createAccount(id);
            
            createCustomer( id, account );

            findCustomerById( id );
        
            //CustomerAccount ca = customerAccountManager.findById(conn, 1);
            
            updateCustomer( id,  "BARTOLOMEO", "SORRENTINO" );
            
            updateCustomerInclude( id, "LUIGI");
         
            findCustomers();
            
            removeCustomer( id );
            
        }
        
        
	private Customer createCustomer( int id, BankAccount account ) throws SQLException {
		
                
		Customer bean = new Customer();
		
		bean.setCustomerId(id);
		bean.setFirstName("name" + id);
		bean.setLastName("sname1" + id);
        bean.setAccountId( account.getAccountId() );
        bean.setVip(true);
        bean.setNote( "NOTE " + id);
		bean.setBirthDate( new java.util.Date() ); 

        customerManager.create(conn, bean);
		
        return bean;
		
	}

    private BankAccount createAccount( int id) throws SQLException {
		
		BankAccount bean = new BankAccount();
		
		bean.setAccountId(id);
		bean.setBalance(1000);
		bean.setNumber("N000" + id);
		
		accountManager.create(conn, bean);
		
        return bean;
		
	}
	
	void findCustomerById( int id) throws SQLException {
		
		Customer bean = customerManager.findById(conn, id);
		
                
		Assert.assertNotNull("Customer retreived is null", bean );
		Assert.assertEquals( "Customer.id doesn't match", bean.getCustomerId(), id );
		Assert.assertTrue( "Customer.vip isn't true", bean.isVip() );
		Assert.assertNotNull( "Customer.note is Null", bean.getNote());
                
                
         System.out.printf( "Customer %s\ndate=%s\n", bean, DateFormat.getDateTimeInstance(DateFormat.LONG,DateFormat.LONG).format( bean.getBirthDate() ) );
		
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
