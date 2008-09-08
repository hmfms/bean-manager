package org.bsc.bean;

import java.beans.BeanInfo;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import org.bsc.util.Log;

/**
manage bean persistence operation on DB using BeanInfo and
PropertyDescriptorField extention and support operations create, store and remove
on the bean mapped on multiple table.

<pre>
Connection conn = <i> open connection ... </i>

MyBean bean = new MyBean();

<i> set properties of bean </i>

BeanManagerBase beanManager = new BeanManagerBase(MyBean.class);

//CREATE(INSERT) BEAN ON DB
beanManager.create( conn, bean );

//STORE(UPDATE) BEAN ON DB
beanManager.store( conn, bean );

//REMOVE (DELETE) BEAN ON DB
beanManager.remove( conn, <i>id value</i> );

//FIND (SELECT) BEAN BY ID ON DB

MyBean bean  = (MyBean)beanManager.findById( conn, <i>id value</i> );

//FIND (SELECT) BEAN(S) BY CONDITION ON DB

java.util.Enumeration e = beanManager.find( conn,
        "<i>property name</i> like ?",
        new Object[] { "FOR%" } );

while( e.hasMoreElements() ) {
      bean = (MyBean)e.nextElement();
}

conn.close();
</pre>
@see bsc.bean.PropertyDescriptorField
@see java.beans.BeanDescriptor
*/
public class AggregateBeanManager<T> implements BeanManager<T> {

  private BeanManager<T> base;

  private BeanManager<T>[] aggregate;

  /**
   *
   * @param beanClass
   * @param beanInfo
   * @param aggregateBeanInfo
   * @throws IntrospectionException
   */
  @SuppressWarnings("unchecked")
public AggregateBeanManager( BeanManager<T> base, BeanInfo[] aggregateBeanInfo ) /*throws IntrospectionException*/ {

    this.base = base;

    BeanManagerFactory factory = BeanManagerFactory.getFactory();

    aggregate = new BeanManager[ aggregateBeanInfo.length ];

    for( int i=0; i<aggregateBeanInfo.length; ++i ) {

      aggregate[i] = (BeanManager<T>)factory.createBeanManager( base.getBeanClass(), aggregateBeanInfo[i] );

    }

  }

  /**
   *
   * @param beanClass
   * @param beanInfo
   * @param aggregateBeanInfo
   * @throws IntrospectionException
   */
  @SuppressWarnings("unchecked")
public AggregateBeanManager( BeanManager<T> base, BeanManager<T>[] aggregateManager ) /*throws IntrospectionException*/ {

    this.base = base;

    aggregate = new BeanManager[ aggregateManager.length ];

    System.arraycopy( aggregateManager, 0, aggregate, 0, aggregateManager.length );

  }

  /**
   *
   * @return
   */
  public BeanDescriptorEntity getBeanDescriptor() {
    return base.getBeanDescriptor();
  }


  /**
   *get the bean class
   *@return
  */
  public Class<T> getBeanClass() {
    return base.getBeanClass();
  }


  /**
   * enable/disable identity conditions on all find commands
   *
   * @param enable
   * @see BeanDescriptorEntity#setIdentityConditions
   */
  public void setEnableIdentityConditions( boolean enable ) {
    base.setEnableIdentityConditions( enable );
  }

  /**
   create a new bean into db ( same of SQL INSERT command )
   <pre>
   if the bean is mapped on multiple tables ( see BeanInfo.getAdditionalBeanInfo() )
   this method perform INSERT on every table
   </pre>
   @param conn database connectio
   @param beans object to insert into db
   @exception SQLException
   */
  public int create(Connection conn, T... beans) throws java.sql.SQLException {

    int result = 0;
    Integer aggregateOrder = base.getBeanDescriptor().getAggregateOrder();

    for( T bean : beans ) {
        if( aggregateOrder==BeanDescriptorEntity.ORDER_FIRST ) {
          result = base.create( conn, bean );
        }

        for( int i=0; i<aggregate.length; ++i ) {
            aggregate[i].create( conn, bean );
        }

        if( aggregateOrder==BeanDescriptorEntity.ORDER_LAST ) {
          result = base.create( conn, bean );
        }
    }
    return result;

  }

 /**
 remove bean from db (same of SQL DELETE command )
 <pre>
 if the bean is mapped on multiple tables ( see BeanInfo.getAdditionalBeanInfo() )
 this method perform the DELETE on every table
 </pre>

 @param conn database connection
 @param bean instance to remove from db
 @exception SQLException
 */
  public int remove(Connection conn, T bean) throws java.sql.SQLException {
    int result = 0;
    Integer aggregateOrder = base.getBeanDescriptor().getAggregateOrder();

    if( aggregateOrder==BeanDescriptorEntity.ORDER_LAST ) {
      result += base.remove( conn, bean );
    }

    for( int i=aggregate.length-1; i>=0; --i ) {
        result += aggregate[i].remove( conn, bean );
    }

    if( aggregateOrder==BeanDescriptorEntity.ORDER_FIRST ) {
      result += base.remove( conn, bean );
    }

    return result;
  }

  /**
   delete bean from db using an id
   @param conn database connection
   @param id id value ( for composite key must be an Object array )
   @exception SQLException
   */
  public int removeById(Connection conn, Object id) throws SQLException {
    int result = 0;
    Integer aggregateOrder = base.getBeanDescriptor().getAggregateOrder();

    if( aggregateOrder==BeanDescriptorEntity.ORDER_LAST ) {
      result += base.removeById( conn, id );
    }

    for( int i=aggregate.length-1; i>=0; --i ) {
        result += aggregate[i].removeById( conn, id );
    }

    if( aggregateOrder==BeanDescriptorEntity.ORDER_FIRST ) {
      result += base.removeById( conn, id );
    }

    return result;
  }
  
  /**
   * 
   * @param conn
   * @return
   * @throws java.sql.SQLException
   */
  public int removeAll(Connection conn) throws SQLException {
    int result = 0;
    Integer aggregateOrder = base.getBeanDescriptor().getAggregateOrder();

    if( aggregateOrder==BeanDescriptorEntity.ORDER_LAST ) {
      result += base.removeAll( conn );
    }

    for( int i=aggregate.length-1; i>=0; --i ) {
        result += aggregate[i].removeAll( conn );
    }

    if( aggregateOrder==BeanDescriptorEntity.ORDER_FIRST ) {
      result += base.removeAll( conn );
    }

    return result;
  }

 /**
 store bean into db ( same of SQL UPDATE command )
 <pre>
 if the bean is mapped on multiple tables ( see BeanInfo.getAdditionalBeanInfo() )
 this method perform the UPDATE on every table
 </pre>

 @param conn database connection
 @param bean object to update into db
 @exception SQLException
 */
  public int store(Connection conn, T bean) throws java.sql.SQLException {

    int result = 0;
    Integer aggregateOrder = base.getBeanDescriptor().getAggregateOrder();

    if( aggregateOrder==BeanDescriptorEntity.ORDER_FIRST ) {
      result += base.store( conn, bean );
    }

    for( int i=0; i<aggregate.length; ++i ) {
        result += aggregate[i].store( conn, bean );
    }

    if( aggregateOrder==BeanDescriptorEntity.ORDER_LAST ) {
      result += base.store( conn, bean );
    }
    return result;
  }

   /**
   update bean into db having the possibility of
   include/exclude properties into update command

   <pre>
   Ex.:

    String props[] = { "prop1",  "prop2", ... };

    int result = manager.store( conn, myBean, props, true ); // include

    OR

    int result = manager.store( conn, myBean, props, false ); // exclude

   </pre>

   @param conn database connection
   @param bean object to update into db
   @param properties properties to include/exclude to update
   @param constraints allow to include or exclude properties from update
   @exception SQLException
   */
   public int store( Connection conn, T bean, StoreConstraints constraints, String... properties ) throws java.sql.SQLException {
    int result = 0;

    Integer aggregateOrder = base.getBeanDescriptor().getAggregateOrder();

    if( aggregateOrder==BeanDescriptorEntity.ORDER_FIRST ) {
      result += base.store( conn, bean, constraints, properties );
    }

    for( int i=0; i<aggregate.length; ++i ) {
      try {
        result += aggregate[i].store(conn, bean, constraints, properties);
      } catch( PropertyNotFoundException pnfEx ) { 
        Log.warn( "Property not found!", pnfEx );
      }
    }

    if( aggregateOrder==BeanDescriptorEntity.ORDER_LAST ) {
      result += base.store( conn, bean, constraints, properties );
    }

    return result;

   }

   /**
    * 
    * @param conn
    * @param bean
    * @param constraints
    * @param properties
    * @return
    * @throws java.sql.SQLException
    */
   public int storeAll( Connection conn, T bean, StoreConstraints constraints, String... properties ) throws java.sql.SQLException {
    int result = 0;

    Integer aggregateOrder = base.getBeanDescriptor().getAggregateOrder();

    if( aggregateOrder==BeanDescriptorEntity.ORDER_FIRST ) {
      result += base.storeAll( conn, bean, constraints, properties );
    }

    for( int i=0; i<aggregate.length; ++i ) {
      try {
        result += aggregate[i].storeAll(conn, bean, constraints, properties);
      } catch( PropertyNotFoundException pnfEx ) { 
        Log.warn( "Property not found!", pnfEx );
      }
    }

    if( aggregateOrder==BeanDescriptorEntity.ORDER_LAST ) {
      result += base.storeAll( conn, bean, constraints, properties );
    }

    return result;

   }

   /**
    select bean from db using an primary key value
    Note:
    if you have a composite key must pass into id parameter a
    Object array that contains the PK values
    <pre>
    Ex.:
// SINGLE KEY (string)
    String id = "xxxxx";
    MyBean bean = (MyBean)manager.findById( conn, id );
// MULTIPLE KEY (string,integer)
    String id[] = {"xxxxx", new Integer(1) };
    MyBean bean = (MyBean)manager.findById( conn, id );
    </pre>
    @param conn database connection
    @param id  primary key value
    @return bean | <b>null</b>
    @exception SQLException
    */
   public T findById(Connection conn, Object id) throws SQLException {
     return base.findById(conn,id);
   }

   /**
    reload bean instance from db
    @param conn database connection
    @param bean  bean intance
    @return bean instance updated (same of parameter bean)
    @exception SQLException
    @see #findById
    */
   public T loadBean(Connection conn, T bean) throws SQLException {
     return base.loadBean(conn,bean);
   }

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
   public Collection<T> find(Connection conn, Collection<T> result, String where, Object... values) throws SQLException {
     return base.find( conn, result, where, values );
   }

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
   public BeanEnumeration<T> find(Connection conn, String where, Object... values) throws SQLException {
     return base.find(conn,where,values);
   }

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
   public BeanEnumeration<T> find(PreparedStatement statement) throws SQLException {
     return base.find(statement);
   }

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
   public Collection<T> find(PreparedStatement statement, Collection<T> result) throws SQLException {
     return base.find( statement, result );
   }

   /**
    select all beans from db
    @param conn database connection
    @param collection used for store all the instance of bean
    @param sqlClouse sql clouse to append command ( like order by or group by ... ) cannot enter parameters can be null
    @return collection that contain all the instance of bean ( same of result parameter )
    @exception SQLException
    */
   public Collection<T> findAll(Connection conn, Collection<T> result, String sqlClouse) throws SQLException {
     return base.findAll( conn, result, sqlClouse );
   }

   /**
    select all beans from db
    @param conn database connection
    @param sqlClouse sql clouse to append command ( like order by or group by ... ) cannot enter parameters can be null
    @return enumeration of retreived beans
    @exception SQLException
    */
   public BeanEnumeration<T> findAll(Connection conn, String sqlClouse) throws SQLException {
     return base.findAll( conn, sqlClouse );
   }

   /**
    * factory method
    *
    * create bean instance and set the property values using ResultSet current row data
    *
    * @param rs
    * @return
    * @throws java.lang.InstantiationException
    */
   public T instantiateBean() throws java.lang.InstantiationException{
    return base.instantiateBean();
   }

  /**
   *
   * @param bean
   * @param rs
   * @return
   */
  public T setBeanProperties(T bean, ResultSet rs) throws SQLException {
    return base.setBeanProperties(bean,rs);
  }

  /**
   * prepareCustomFind
   *
   * @param conn Connection
   * @param commandKey String
   * @param where String
   * @return PreparedStatement
   */
  public PreparedStatement prepareCustomFind(   Connection conn, 
                                                String commandKey,
                                                String where) throws SQLException
  {
    return base.prepareCustomFind(conn,commandKey,where);
  }

    /**
     * 
     * @param conn
     * @param where
     * @param values
     * @return
     * @throws java.sql.SQLException
     */
    public int findAndRemove(Connection conn, String where, Object... values) throws SQLException {
        throw new UnsupportedOperationException("This method is not supported for aggregateBean!");
    }

}
