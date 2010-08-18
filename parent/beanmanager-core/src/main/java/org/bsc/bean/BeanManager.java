package org.bsc.bean;


import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;

//import org.bsc.processor.annotation.ResourceBundle;

/**
It's an implementation of DAO interface (see DAO pattern ) for a jdbc data source
to define a bean persistent model based on a extension of BeanInfo meta data
<br/>

<u>Examples:</u><br/>

<pre>

Connection conn = <i> open connection ... </i>

MyBean bean = new MyBean();

<i> set properties of bean </i>

// Create a bean manager instance that manage the MyBean persistence
BeanManager<MyBean> manager = (BeanManager<MyBean>)BeanManagerFactory.createBeanManager( MyBean.class );

//CREATE(INSERT) BEAN ON DB
manager.create( conn, bean );

//STORE(UPDATE) BEAN ON DB
manager.store( conn, bean );

//REMOVE (DELETE) BEAN ON DB
manager.remove( conn, <i>id value</i> );

//FIND (SELECT) BEAN BY ID ON DB

MyBean bean  = manager.findById( conn, <i>id value</i> );

//
//FIND (SELECT) BEAN(S) BY CONDITION ON DB
// APPROACH CURSOR BASED
BeanEnumeration<MyBean> result = manager.find( conn,  "${<i>propertyName</i>} LIKE ?", "FOR%" );

// FETCH BEANS
for( MyBean b : result ) {
		......
}
result.close();

//
//FIND (SELECT) BEAN(S) BY CONDITION ON DB
// APPROACH CACHE BASED
List<MyBean> resultList = manager.find( conn, new List<MyBean>(), "${<i>propertyName</i>} LIKE ?", "FOR%" );

// GET BEANS
for( MyBean b : resultList ) {
		......
}

conn.close();

@see bsc.bean.PropertyDescriptorField
@see bsc.bean.BeanDescriptorEntity
*/
//@ResourceBundle
public interface BeanManager<T> {

  /**
   * get bean descriptor of persistent bean
   *
   * @return BeanDescriptorEntity
   */
  public BeanDescriptorEntity getBeanDescriptor();

  /**
  @return array of PropertyDescriptorField inside beanInfo
  @see bsc.bean.PropertyDescriptorField
  */
  public PropertyDescriptorField [] getPropertyDescriptorsFields();


  /**
   *
   * @return primary key object
   * @see bsc.bean.PrimaryKey
   */
  public PrimaryKey getPrimaryKey();

  /**
  * get persistent bean class
  *
  * @return Class
  */
  public Class<T> getBeanClass() ;

  /**
   * enable/disable identity conditions on all find commands
   *
   * @param enable
   * @see BeanDescriptorEntity#setIdentityConditions
   */
  public void setEnableIdentityConditions( boolean enable );

  /**
   * factory method
   *
   * create new persistent bean instance
   *
   * @return new bean instance
   * @throws java.lang.InstantiationException
   */
  public T instantiateBean( ) throws java.lang.InstantiationException;

  /**
   * set the bean property values using ResultSet current row data
   *
   * @param bean
   * @param rs
   * @return filled bean instance
   * @throws SQLException
   */
  public T setBeanProperties( T bean, ResultSet rs ) throws SQLException;

 /**
  * insert beans into db ( perform SQL INSERT )
  *
  *@param conn database connectio
  *@param beans objects to insert into db
  *@exception SQLException
  */
  public int create(Connection conn, T ... beans) throws SQLException;

  /**
   * update bean into db ( perform SQL UPDATE )
   *
   *@param conn database connection
   *@param bean object to update into db
   *@exception SQLException
   */
  public int store(Connection conn, T ... bean) throws SQLException;

  /**
  * update bean into db having the possibility of
  * include/exclude properties into update command
  *
  * <pre>
  * Ex.:
  * int result = manager.store( conn, myBean, StoreConstraints.INCLUDE_PROPERTIES, "prop1",  "prop2, ..." ); // include
  * OR
  * int result = manager.store( conn, myBean, StoreConstraints.EXCLUDE_PROPERTIES, "prop1", "prop2", ...  ); // exclude
  * </pre>
  * @param conn database connection
  * @param bean object to update into db
  * @param properties properties to include/exclude to update
  * @param constraints allow to include or exclude properties from update
  * @exception SQLException
  */
  public int store( Connection conn, T bean, StoreConstraints constraints, String... properties ) throws SQLException ;

  /**
   * update all rows using the property's values of the given bean having the possibility of
   * include/exclude properties into update command
   * 
   * @param conn
   * @param bean
   * @param constraints
   * @param properties
   * @return
   * @throws java.sql.SQLException
   */
  public int storeAll( Connection conn, T bean, StoreConstraints constraints, String... properties) throws SQLException;
  
  /**
  * delete bean from db using an id
  * Note:
  * if you have a composite key must pass all key's values in right order (i.e. the same declared within BeanInfo)
  * <pre>
  * Ex.:
  * SINGLE KEY (string)
  * String id = "xxxxx";
  * manager.removeById( conn, id );
  * MULTIPLE KEY (string,integer)
  * manager.removeById( conn, "xxxx", 1 );
  * </pre>
  * @param conn database connection
  * @param id  primary key value(s)  
  * @return 1 if success removed
  * @exception SQLException
  */
  public int removeById(Connection conn, Object...id) throws SQLException;

  /**
  * delete bean from db ( perform SQL DELETE )
  * <pre>
  * Ex.:
  * MyBean bean = (Bean)manager.findById( conn, ..  );
  * ....
  * int result = manager.remove( conn, bean );
  * </pre>
  * @param conn database connection
  * @param bean instance to remove
  * @exception SQLException
  */
  public int remove(Connection conn, T bean) throws SQLException;

  /**
   * delete all bean from db
   * 
   * @param conn
   * @return
   * @throws java.sql.SQLException
   */
  public int removeAll( Connection conn ) throws SQLException;
  
  /**
   * delete all rows that accomplish the filter criterias
   * 
   * @param conn
   * @param where
   * @param values
   * @return
   * @throws java.sql.SQLException
   */
  public int findAndRemove( Connection conn, String where, Object... values ) throws SQLException;
  
  /**
  * select bean from db using an primary key value
  * Note:
  * if you have a composite key must pass all key's values in right order (i.e. the same declared within BeanInfo)
  * <pre>
  * Ex.:
  * SINGLE KEY (string)
  * String id = "xxxxx";
  * MyBean bean = (MyBean)manager.findById( conn, id );
  * MULTIPLE KEY (string,integer)
  * MyBean bean = (MyBean)manager.findById( conn, "xxxx", 1 );
  * </pre>
  * @param conn database connection
  * @param id  primary key value(s)  
  * @return bean | <b>null</b>
  * @exception SQLException
  */
  public T findById(Connection conn, Object...id) throws SQLException;

  /**
   reload bean instance from db
   @param conn database connection
   @param bean  bean intance
   @return bean instance updated (same of parameter bean) - null if not found
   @exception SQLException
   @see #findById
   */
  public T loadBean(Connection conn, T bean) throws SQLException;

  /**
   * <pre>
   * select beans from db using a where condition and cache the result into collection
   *
   * <b>NB</b>
   * can close the connection before you use the collection
   * </pre>
   *
   @param conn database connection
   @param result instance of collection that will contains all the instance of beans
   @param where condition formatted like PreparedStatement
   @param values Object array contains a parameters value specified into where condition
                <br><b>create an entry for each ? into where condition</b>
   @return collection that contains all the instance of beans ( same of result parameter )
   @exception SQLException
   @see java.sql.PreparedStetement
   */
  public Collection<T> find(Connection conn, Collection<T> result, String where, Object... values) throws SQLException;

  /**
   * <pre>
   * select beans from db using a where condition
   *
   *  <b>NB</b>
   *  cannot close the connection before you have obtain all element from enumeration
   *  </pre>
   *
   @param conn database connection
   @param where condition formatted like PreparedStatement
   @param values Object array contains a parameters value specified into where condition
                <br><b>create an entry for each ? into where condition</b>
   @return enumeration of retreived beans
   @exception SQLException
   @see java.sql.PreparedStetement
   */
  public BeanEnumeration<T> find(Connection conn, String where, Object... values) throws SQLException;

  /**
   * Build the PreparedStatement used into find method, using a custom find
   * command stored into related BeanDescriptorEntity
   *
   * @param conn
   * @param commandKey
   * @param where
   * @return
   * @throws SQLException
   *
   * @see BeanDescriptorEntity#getCustomFindCommand
   *
   */
  public PreparedStatement prepareCustomFind( Connection conn, String commandKey, String where ) throws SQLException;

  /**
   * <pre>
   * select beans from db using a custom SQL SELECT command
   *
   * <b>NB</b>
   * cannot close the connection before you have obtain all element from enumeration
   * </pre>
   *
   * @param statement custom SQL SELECT command
   * @return enumeration of retreived beans
   * @throws SQLException
   */
  public BeanEnumeration<T> find(PreparedStatement statement) throws SQLException;

  /**
   * <pre>
   * select beans from db using a custom SQL SELECT and cache the result into collection
   *
   * <b>NB</b>
   * you can close the connection before you use the collection
   * </pre>
   *
   * @param statement custom SQL SELECT command
   * @param result instance of collection that will contains all the instance of beans
   * @return collection that contains all the instance of beans ( same of result parameter )
   * @throws SQLException
   */
  public Collection<T> find(PreparedStatement statement, Collection<T> result) throws SQLException;

  /**
   select all beans from db
   @param conn database connection
   @param collection used for store all the instance of bean
   @param sqlClouse sql clouse to append command ( like order by or group by ... ) cannot enter parameters can be null
   @return collection that contain all the instance of bean ( same of result parameter )
   @exception SQLException
   */
  public Collection<T> findAll(Connection conn, Collection<T> result,
                            String sqlClouse) throws SQLException;

  /**
   select all beans from db
   @param conn database connection
   @param sqlClouse sql clouse to append command ( like order by or group by ... ) cannot enter parameters can be null
   @return enumeration of retreived beans
   @exception SQLException
   */
  public BeanEnumeration<T> findAll(Connection conn, String sqlClouse) throws SQLException;

}
