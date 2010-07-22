package org.bsc.bean;

import java.beans.BeanInfo;
import java.beans.PropertyDescriptor;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;

import org.bsc.util.Configurator;
import org.bsc.util.Log;

/**
*
* This class provides a skeletal implementation of the BeanManager interface
* to minimize the effort required to implement this interface. The only method
* that you need to implement is instantiateBean to customize the bean
* instance creation
*
* @author BARTOLOMEO Sorrentino
* @version 2.2.1
*/
public abstract class AbstractBeanManager<T> implements BeanManager<T> {
  protected static java.util.Properties customCommands  = Configurator.loadCustomCommands();

  private static final String CREATECMD     = "INSERT INTO {0} {1} VALUES {2}";
  private static final String STORECMD      = "UPDATE {0} SET {1} WHERE {2}";
  private static final String STOREALLCMD   = "UPDATE {0} SET {1} ";
  private static final String REMOVECMD     = "DELETE FROM {0} WHERE {1}";
  private static final String REMOVEALLCMD  = "DELETE FROM {0} {1}";

  /**
  * {0} --> field list
  * {1} --> entity name
  * {2} --> join conditions
  * {3} --> where conditions
  * 
  */
  private static final String FINDCMD     = "SELECT {0} FROM {1} {2} WHERE {3} ";

  // {0} --> field list
  // {1} --> entity name
  // {2} --> join conditions
  // {3} --> identity conditions
  private static final String FINDALLCMD  = "SELECT {0} FROM {1} {2} {3}";

  // {0} --> field list
  // {1} --> entity name
  // {2} --> join conditions
  // {3} --> where conditions
  private static final String FINDBYIDCMD = "SELECT {0} FROM {1} {2} WHERE {3} ";


  //private static final String CALLCMD     = "'{' CALL {0} ( {1} ) '}'";
  protected static final String CALLCMDWITHRESULT = "'{' ? = {0} ( {1} ) '}'";

  protected BeanDescriptorEntity entity = null;
  private ManagedBeanInfoProxy<T> beanInfo;

  private PrimaryKey primaryKey = null;
  private PropertyDescriptorField props [] = null;
  private PropertyDescriptorJoin []  propsJoin = null;

  private JoinRelations joinRelations = null;

  /** all properties cache */
  protected java.util.Map<String,PropertyDescriptorField> allProps = null;

  /**
   *
   * constructor for provide a custom bean info
   *
  @param bean the bean class
  @param beanInfo custom BeanInfo object
  */
  protected AbstractBeanManager( Class<T> bean, BeanInfo beanInfo ) {

    this.beanInfo = new CachedManagedBeanInfo<T>(beanInfo,bean);

    init();

  }

  /**
   *
   * @param beanInfo custom ManagedBeanInfo object
  */
  protected AbstractBeanManager( ManagedBeanInfo<T> beanInfo ) {

    this.beanInfo = new CachedManagedBeanInfo<T>(beanInfo);

    init();

  }

  /**
   *
   * @return
   */
  public BeanDescriptorEntity getBeanDescriptor(){
    return this.entity;
  }


  /**
   *
   * @return
   */
  public BeanInfo getBeanInfo() {
    return this.beanInfo;
  }

  /**
  get the db entity associate with bean
  */
  public String getEntity() {
    return this.entity.getEntityName();
  }

  /**
  get the bean class
  */
  public Class<T> getBeanClass() {
    return beanInfo.getBeanClass();
  }

  /**
  test if the bean have a primary key
  */
  public boolean hasPrimaryKey() {
    return (primaryKey!=null);
  }

  /**
  @return primary key object
  @see bsc.bean.PrimaryKey
  */
  public PrimaryKey getPrimaryKey() {
    return primaryKey;
  }

  /**
  @return array of PropertyDescriptorField inside beanInfo
  @see bsc.bean.PropertyDescriptorField
  */
  public PropertyDescriptorField [] getPropertyDescriptorsFields() {
    return props;
  }

  /**
  @param name property name
  @return PropertyDescriptorField  ( if not found return <b>null</b> )
  */
  public PropertyDescriptorField getPropertyByName( String name ) {
    String key = name.toUpperCase();
    PropertyDescriptorField p = null;

    for( int i=0; i<props.length; ++i ) {
      p = props[i];
      if( key.equals(p.getName().toUpperCase()) ) {
          return p;
      }
    }

    return null;
  }

  /**
   * enable/disable identity conditions on all find commands
   *
   * @param enable
   * @see BeanDescriptorEntity#setIdentityConditions
   */
  public void setEnableIdentityConditions( boolean enable ) {
    this.entity.setEnableIdentityConditions( enable );
  }

  /**
   * utility method
   *
   * @param value
   * @return true if value is null or length = 0
   */
  protected boolean isEmpty( String value ) {
    return (value==null || value.length()==0);
  }

  /**
   *
   * @return identity conditions or null
   */
  protected String getIdentityConditions() {

    if( this.entity.isEnableIdentityConditions() ) {
      return this.entity.getIdentityConditions();
    }

    return null;
  }

  /**
   *
  */
@SuppressWarnings("unchecked")
private final void init() {
      this.entity = BeanDescriptorUtils.createDescriptorEntity(this.beanInfo.getBeanDescriptor());

      PropertyDescriptor [] pp = BeanManagerUtils.getBeanProperties( beanInfo );

      this.allProps = BeanManagerUtils.getPropertyFieldMap( pp );


      java.util.ArrayList<PropertyDescriptor> propsDB = new java.util.ArrayList<PropertyDescriptor>(pp.length);
      java.util.ArrayList<PropertyDescriptor> propsDBJ = new java.util.ArrayList<PropertyDescriptor>(pp.length);

      primaryKey = new PrimaryKey();

      PropertyDescriptorField p ;

      for( int i=0; i<pp.length ; ++i ) {

        if( pp[i] instanceof PropertyDescriptorJoin ) {
            propsDBJ.add( pp[i] );
            continue;
        }

        if( pp[i] instanceof PropertyDescriptorField ) {

          p = (PropertyDescriptorField)pp[i];

          // SET THE ENTITY NAME
          //p.setValue( PropertyDescriptorField.ENTITY, this.entity.getEntityName() );

          if( p.isPrimaryKey() )
            primaryKey.add((PropertyDescriptorPK)p);
          else
            propsDB.add( p );
        }
      }

      if( primaryKey.getKeyCount()==0 ) {
        throw new Error( BeanManagerUtils.getMessage("ex.need_primary_key") );
      }

      props = new  PropertyDescriptorField[propsDB.size()];
      propsJoin = new  PropertyDescriptorJoin[propsDBJ.size()];

      propsDB.toArray(props);
      propsDBJ.toArray(propsJoin);

      joinRelations = this.entity.getJoinRelations();

      //this.allProps = BeanManagerUtils.getPropertyFields(pp);

      propsDB = null;
      propsDBJ = null;

  }

  /**
  */
  private String getCreatePropertyValues()
  {
    StringBuilder sb = new StringBuilder(255);

    sb.append('(');

    for( int i=0; i<props.length ; ++i ) {
      if( !props[i].isReadOnly() ){
        sb.append( "?," );
      }
    }

    getPrimaryKey().appendToPropertyValues(sb);

    // replace last char ',' with ')'
    sb.setCharAt( sb.length()-1, ')' );

    return sb.toString();
  }

  /**
  */
  private String getCreatePropertyList(String begin,String end,String sep )
  {

    StringBuilder sb = new StringBuilder(begin);

    getPrimaryKey().appendToCreatePropertyList(sb,sep);

    if( props.length==0 ) {

      sb.setCharAt( sb.length()-1, ' ');

    }
    else {

      int i;
      for( i=0; i<props.length-1 ; ++i ) {
        if( !props[i].isReadOnly() ) {
          sb.append( props[i].getFieldName() );
          sb.append( sep );
        }
      }
      if( !props[i].isReadOnly() ) {
        sb.append( props[i].getFieldName() );
      }
      else {
        sb.deleteCharAt( sb.length()-1 ); // remove last sep
      }

    }
    sb.append( end );

    return sb.toString();
  }

  /**
   *
   * @param begin
   * @param end
   * @param sep
   * @return
   */
  private String getStorePropertyList(String begin,String end,String sep )
  {
    // PRE - CONDITION
    if( props.length==0 )
      return null;

    StringBuilder sb = new StringBuilder(begin);

    int i;
    for( i=0; i<props.length ; ++i ) {
      if( !props[i].isReadOnly() ) {
        sb.append( props[i].getFieldName() );
        sb.append( sep );
      }
    }

    sb.deleteCharAt( sb.length()-1 ); // remove last sep

    sb.append( end );

    return sb.toString();
  }

  /**
   *
   * @param properties
   * @param include
   * @param begin
   * @param end
   * @param sep
   * @return
   */
  @SuppressWarnings("unchecked")
protected String getStorePropertyListInclude(  String begin,
                                        String end,
                                        String sep,
                                        String[] properties )
  {
    // PRE - CONDITION
    if( allProps.isEmpty() )
      return null;

    StringBuilder sb = new StringBuilder(begin);

    PropertyDescriptorField p;

    for( int i=0; i<properties.length ; ++i ) {

      p = (PropertyDescriptorField) allProps.get(properties[i]);

      if( p==null) {
        throw new PropertyNotFoundException( BeanManagerUtils.getMessage( "ex.store_props_not_found",
                  new Object[]{properties[i]}) );
      }

      if( p.isReadOnly() || p instanceof PropertyDescriptorJoin )
        continue;

      sb.append( p.getFieldName() );
      sb.append( sep );
    }

    if( sb.length()>0 ) {
      sb.deleteCharAt( sb.length()-1 ); // remove last sep
    }

    sb.append( end );

    return sb.toString();
  }

  /**
   *
   * @param begin
   * @param end
   * @param sep
   * @param properties
   * @return
   */
  protected String getStorePropertyListExclude(  String begin,
                                        String end,
                                        String sep,
                                        String[] properties )
  {
    // PRE - CONDITION
    if( props.length==0 )
      return null;

    StringBuilder sb = new StringBuilder(begin);

    int i;
    for( i=0; i<props.length ; ++i ) {

      if( !props[i].isReadOnly() &&
        java.util.Arrays.binarySearch(properties,props[i].getName())<0 )
      {
        sb.append( props[i].getFieldName() );
        sb.append( sep );
      }
    }

    sb.deleteCharAt( sb.length()-1 ); // remove last sep

    sb.append( end );

    return sb.toString();
  }


  /**
   *
   * @param begin
   * @param end
   * @param sep
   * @return
   */
  private String getFindPropertyList( String begin,String end,String sep )
  {

    int i;
    StringBuilder sb = new StringBuilder(255);

    sb.append( begin );

    // ADD PRIMARY KEY(S)
    getPrimaryKey().appendToFindPropertyList(sb,sep,this.getEntity());

    // ADD JOIN FIELD(S)
    for( i=0; i<propsJoin.length ; ++i ) {
      //sb.append( propsJoin[i].getJoinTable() );
      //sb.append('.');
      //sb.append( propsJoin[i].getQueryFieldName() );
      propsJoin[i].appendQueryFieldName(sb,propsJoin[i].getJoinTable() );
      sb.append( sep );
    }


    // ADD FIELD(S)
    if( props.length > 0 ) {
      for( i=0; i<props.length-1 ; ++i ) {
        //sb.append( this.entity );
        //sb.append('.');
        //sb.append( props[i].getQueryFieldName() );
        props[i].appendQueryFieldName(sb,entity.getEntityName());
        sb.append( sep );
      }

      //sb.append( this.entity );
      //sb.append('.');
      //sb.append( props[i].getQueryFieldName() );
      props[i].appendQueryFieldName(sb,entity.getEntityName());
      sb.append( end );
    }
    else {
      // REMOVE LAST sep CHARACTER
      sb.deleteCharAt( sb.length() - 1);
    }

    return sb.toString();
  }

  /**
   *
   * @return SQL statement used into <b>create</b> method
   */
  public final String getCreateStatement() {

    return java.text.MessageFormat.format(CREATECMD,
                        this.entity,
                        this.getCreatePropertyList( "(",")","," ),
                        this.getCreatePropertyValues()
                        );
  }

  /**
   * @return SQL statement used into <b>store</b> method
  */
  public final String getStoreStatement( ) {

    return java.text.MessageFormat.format(STORECMD,      
            this.entity,
            this.getStorePropertyList( "","","=?,"),
            this.getPrimaryKey().getStoreStatementParameters()
            );
  }

  /**
   * @return SQL statement used into <b>store</b> method
  */
  public final String getStoreStatement( String[] properties, StoreConstraints constraints  ) {

    if( properties==null || properties.length==0 ) {
      throw new IllegalArgumentException(
              BeanManagerUtils.getMessage("ex.parameter_is_empty","properties") );
    }


    String propertyList = (constraints==StoreConstraints.INCLUDE_PROPERTIES) ?
            getStorePropertyListInclude("","","=?,",properties) :
            getStorePropertyListExclude("","","=?,",properties) ;

    String result = null;

    if( !isEmpty(propertyList) ) {

      result =  java.text.MessageFormat.format(STORECMD, 
                    this.entity,
                    propertyList,
                    this.getPrimaryKey().getStoreStatementParameters()
                );
    }

    return result;
  }

  /**
   * @return SQL statement used into <b>store</b> method
  */
  public final String getRemoveStatement() {

    return java.text.MessageFormat.format(REMOVECMD,      
                    this.entity,
                    this.getPrimaryKey().getRemoveStatementParameters()
                );
  }
  
  /**
   * @return SQL statement used into <b>removeAll</b> method
  */
  public final String getRemoveAllStatement() {
    String c = this.getIdentityConditions();

    // BUILD WHERE CONDITIONS //////////////////////////////////////////
    StringBuilder conditions = new StringBuilder(256);
    if( !isEmpty(c) ) {
      conditions.append( "WHERE ");
      conditions.append( c );
    }
    ///////////////////////////////////////////////////////////////////

    return java.text.MessageFormat.format(REMOVEALLCMD, this.entity, conditions);
  }

  /**
   * obtain the SQL SELECT for findById method
   * @see #findById
   * @return SQL command
  */
  public final String getFindByIdStatement() {
    String c = this.getIdentityConditions();

    // BUILD WHERE CONDITIONS //////////////////////////////////////////
    StringBuilder conditions = new StringBuilder(256);
    if( !isEmpty(c) ) {
      conditions.append(c);
      conditions.append(" AND ");
    }
    conditions.append(
      this.getPrimaryKey().getFindByIdStatementParameters(getEntity())
      );
    ////////////////////////////////////////////////////////////////////

    return java.text.MessageFormat.format(FINDBYIDCMD,      
                        this.getFindPropertyList( "","","," ),
                        this.getEntity(),
                        (joinRelations!=null) ? joinRelations.toString() : "",
                        conditions
                );
   }

  /**
   *
   * @param where
   * @return
   */
  public final String getFindStatement( String where ) {
    return this.getFindStatement( where, new java.util.ArrayList<PropertyPosition>(10) );
  }

  /**
   *
   * @param where
   * @param positions
   * @return
   */
  private String getFindStatement( String where, java.util.List<PropertyPosition> positions ) {
    String wc = parseWhere( where, positions );
    String c = this.getIdentityConditions();

    // BUILD WHERE CONDITIONS //////////////////////////////////////////
    StringBuilder conditions = new StringBuilder(256);
    if( !isEmpty(c) ) {
      conditions.append( c );
      if( !isEmpty( wc ) ) {
        conditions.append( " AND ");
      }
    }
    conditions.append( wc );
    ///////////////////////////////////////////////////////////////////

    Object [] args = {
      this.getFindPropertyList( "","",","),
      this.entity,
      (joinRelations!=null) ? joinRelations.toString() : "",
      conditions
    };

    return java.text.MessageFormat.format(FINDCMD,args);
  }

  /**
   * SQL statement used to implement findAll method
   * @return statement string
   * @see BeanManagerBase#findAll
  */
  public final String getFindAllStatement() {
    String c = this.getIdentityConditions();

    // BUILD WHERE CONDITIONS //////////////////////////////////////////
    StringBuilder conditions = new StringBuilder(256);
    if( !isEmpty(c) ) {
      conditions.append( "WHERE ");
      conditions.append( c );
    }
    ///////////////////////////////////////////////////////////////////

    Object [] args = {
      this.getFindPropertyList( "","",","),
      this.entity,
      (joinRelations!=null) ? joinRelations.toString() : "",
      conditions
    };

    return java.text.MessageFormat.format(FINDALLCMD,args);
  }

  /**
  * assign the value to the bean property
  */
  protected void setPropertyValue( Object bean, ResultSet rs, PropertyDescriptorField p ) throws SQLException {

    Object value = null;

    try {

    DataAdapter adapter = BeanManagerUtils.lookupAdapter( p );

    if( null==adapter ) {
        Log.warn("no adapter found for property {0} ", p.getName() );
        return ;
    }
    
    
    value = adapter.getValue(rs,p);

    Log.debug("ADAPTER {2} used={0} value={1}", adapter.getClass().getName(), value, p.getName());

    if(value!=null) {
      invokeWriteMethod(p, bean, value);
    }

    }
    catch( Exception ex ) {
      String valueClass = (value!=null)?value.getClass().getName():"undefined";
      Object[] params = { ex.getClass().getName(), p.getName(), p.getFieldName(), value, valueClass };
      throw new SQLException( BeanManagerUtils.getMessage("ex.set_prop_value",params) );
    }
  }

  /**
   * initialize PreparedStatement parameters
   *
   * @param ps
   * @param ordinal
   * @param value
   * @param p
   * @throws SQLException
   */
  private void setStatementValue(
                PreparedStatement ps,
                int ordinal,
                Object value,
                PropertyDescriptorField p ) throws java.sql.SQLException

  {

    DataAdapter adapter = BeanManagerUtils.lookupAdapter( p );

    adapter.setValue( ps, ordinal, value, p);

  }

  /**
   *
   * @param bean
   * @param rs
   * @return
   * @throws SQLException
   */
  public T setBeanProperties( T bean, ResultSet rs ) throws SQLException {

   try {

    for( int k=0; k<getPrimaryKey().getKeyCount(); ++k ) {
      setPropertyValue( bean, rs, getPrimaryKey().get(k) );
    }

    for( int i=0; i<props.length ; ++i )
      setPropertyValue( bean, rs, props[i] );

    for( int i=0; i<propsJoin.length ; ++i )
      setPropertyValue( bean, rs, propsJoin[i] );

    }
    catch( SQLException sqlex ) {
      throw sqlex;
    }

    return bean;
  }


  /**
   *
   * @param ps
   * @param bean
   * @return
   * @throws SQLException
   */
  protected int setStoreStatement( PreparedStatement ps, Object bean ) throws java.sql.SQLException
  {

    int i = 0;
    int ordinal = 1;
    Object value;
    try {

    for( i=0; i<props.length ; ++i ) {
      if( !props[i].isReadOnly() ) {
        value = invokeReadMethod(props[i], bean);
        setStatementValue( ps, ordinal++, value, props[i] );
      }
    }

    /////////////////////////////////////////
    // SET PRIMARY KEY FROM BEAN ATTRIBUTE
    /////////////////////////////////////////
    for( int k=0; k<getPrimaryKey().getKeyCount(); ++k ) {
      PropertyDescriptorPK pk = getPrimaryKey().get(k);

      value = invokeReadMethod(pk, bean);
      setStatementValue( ps, ordinal++, value, pk );
    }

    }
    catch( Exception ex ) {
      Object[] params = { ex.getMessage(), props[i].getName() };
      throw new SQLException( BeanManagerUtils.getMessage("ex.set_store_stmt",params) );
    }

    return ordinal;

  }

  /**
   *
   * @param ps
   * @param bean
   * @param properties
   * @param include
   * @return
   * @throws SQLException
   */
  @SuppressWarnings("unchecked")
private int setStoreStatementInclude( PreparedStatement ps,
                         Object bean,
                         String [] properties ) throws java.sql.SQLException
  {

    int i = 0;
    int ordinal = 1;
    Object value;

    try {

    PropertyDescriptorField p;

    for( i=0; i<properties.length ; ++i ) {

      p = (PropertyDescriptorField)allProps.get(properties[i]);

      if( p.isReadOnly() || p instanceof PropertyDescriptorJoin )
        continue;

      value = invokeReadMethod(p, bean);
      setStatementValue( ps, ordinal++, value, p );
    }

    /////////////////////////////////////////
    // SET PRIMARY KEY FROM BEAN ATTRIBUTE
    /////////////////////////////////////////
    for( int k=0; k<getPrimaryKey().getKeyCount(); ++k ) {
      PropertyDescriptorPK pk = getPrimaryKey().get(k);

      value = invokeReadMethod(pk, bean);
      setStatementValue( ps, ordinal++, value, pk );
    }

    }
    catch( Exception ex ) {
      Object[] params = { ex.getMessage(), props[i].getName() };
      throw new SQLException( BeanManagerUtils.getMessage("ex.set_store_stmt",params) );
    }

    return ordinal;

  }

  /**
   *
   * @param ps
   * @param bean
   * @param properties
   * @param include
   * @return
   * @throws SQLException
   */
  private int setStoreStatementExclude( PreparedStatement ps,  Object bean, String [] properties ) throws java.sql.SQLException
  {

    int i = 0;
    int ordinal = 1;
    Object value;

    try {

    for( i=0; i<props.length ; ++i ) {

      if( !props[i].isReadOnly() &&
        java.util.Arrays.binarySearch(properties,props[i].getName())<0 )
      {

        if( !props[i].isReadOnly() ) {
          value = invokeReadMethod(props[i], bean);
          setStatementValue( ps, ordinal++, value, props[i] );
        }
      }
    }

    /////////////////////////////////////////
    // SET PRIMARY KEY FROM BEAN ATTRIBUTE
    /////////////////////////////////////////
    for( int k=0; k<getPrimaryKey().getKeyCount(); ++k ) {
      PropertyDescriptorPK pk = getPrimaryKey().get(k);

      value = invokeReadMethod(pk, bean);
      setStatementValue( ps, ordinal++, value, pk );
    }

    }
    catch( Exception ex ) {
      Object[] params = { ex.getMessage(), props[i].getName() };
      throw new SQLException( BeanManagerUtils.getMessage("ex.set_store_stmt",params) );
    }

    return ordinal;

  }


  protected Object invokeReadMethod( PropertyDescriptor pd, Object beanInstance ) throws Exception {
    return  BeanManagerUtils.invokeReadMethod(pd, beanInstance);

  }

  protected Object invokeWriteMethod( PropertyDescriptor pd, Object beanInstance, Object value ) throws Exception {
    return  BeanManagerUtils.invokeWriteMethod(pd, beanInstance, value);
  }

  /*
   * @param ps
   * @param bean
   * @return
   * @throws SQLException
   */
  protected int setCreateStatement(
                    Connection connection,
                    PreparedStatement ps,
                    Object bean  ) throws java.sql.SQLException
  {

    int i = 0;
    int ordinal = 1;
    Object value;
    try {

    // SET THE PRIMARY KEY VALUE
    for( int k=0; k<getPrimaryKey().getKeyCount(); ++k ) {
      PropertyDescriptorPK pk = getPrimaryKey().get(k);

      if( pk.isAutoGenerate() ) {
         ValueGenerator<?> generator = pk.getValueGenerator();

         Object pkValue = generator.generate(connection, pk);
         Log.debug( "Auto generated value {0} for attribute {1} ", pkValue, pk.getName());
         invokeWriteMethod(pk, bean, pkValue);
         setStatementValue( ps, ordinal++, pkValue, pk );
      }
      else {
          value = invokeReadMethod(pk, bean);
          setStatementValue( ps, ordinal++, value, pk );
      }
    }

    // SET FIELD VALUE
    for( i=0; i<props.length ; ++i ) {
      if( !props[i].isReadOnly() ) {
        value = invokeReadMethod(props[i], bean);          
        setStatementValue( ps, ordinal++, value, props[i] );
      }
    }

    }
    catch( Exception ex ) {
      Object[] params = { ex.getMessage(), props[i].getName() };
      throw new SQLException( BeanManagerUtils.getMessage("ex.set_create_stmt", params) );
    }

    return ordinal;

  }

  /**
   * parse SQL WHERE clause
   *
   * @param where
   * @param positions
   * @return
   */
  private String parseWhere( String where, java.util.Collection<PropertyPosition> positions ) {

	String parsedWhere = PropertyParser.parseWhere(where, allProps, positions);
	
/*	
    PropertyFinder<T> propFinder = new PropertyFinder<T>(where,allProps);

    StringBuilder sb = null;

    int start = 0;

    while( propFinder.hasMoreElements() ) {

        if(null==sb) sb = new StringBuilder();

        PropertyPosition<T> pp = (PropertyPosition<T>)propFinder.nextElement();

        if( !pp.isSkip() ) positions.add( pp );

        sb.append( where.substring(start, pp.getIndex()) );

        //----------------------------------------------------------
        // Add field name <table>.<fieldName>
        sb.append( this.getPropertyEntity(pp.getProperty()) );
        sb.append('.');
        sb.append( pp.getProperty().getFieldName() );
        //----------------------------------------------------------

        start = propFinder.getStartPosition();
    }

    // not modified original where
    if( null==sb ) return where;

    sb.append( where.substring(start) );

    return sb.toString();
*/
	return parsedWhere;
  }

 /**
 insert beans into db

 @param conn database connectio
 @param beans objects to insert into db
 @exception SQLException
 */
 public int create( Connection conn, T... beans ) throws SQLException {
    String sql = this.getCreateStatement();

    Log.TRACE_CMD( "create", sql );

    PreparedStatement ps = conn.prepareStatement(sql);

    int result = 0;
    
    for( T bean : beans ) {
        this.setCreateStatement( conn, ps, bean );

        result += ps.executeUpdate();
        
        
    }
    
    ps.close();
    
    return result;

 }

 /**
 update bean into db

 @param conn database connection
 @param beans objects to update into db
 @exception SQLException
 */
 public int store( Connection conn,  T ... beans ) throws SQLException {
    String sql = this.getStoreStatement();

    Log.TRACE_CMD("store", sql );

    PreparedStatement ps = conn.prepareStatement(sql);

    int result = 0;

    for( T bean : beans ) {
        ps.clearParameters();
        this.setStoreStatement( ps, bean );

        result += ps.executeUpdate();
    }
    
    ps.close();

    return result;

 }

 /**
 update bean into db having the possibility of
 include/exclude properties into update command

 <pre>
 Ex.:

  int result = manager.store( conn, myBean, true, "prop1",  "prop2", ... ); // include

  OR

  int result = manager.store( conn, myBean, false, "prop1",  "prop2" ); // exclude

 </pre>

 @param conn database connection
 @param bean object to update into db
 @param properties properties to include/exclude to update
 @param constraints allow to include or exclude properties from update
 @exception SQLException
 */
 public int store( Connection conn, T bean, StoreConstraints constraints, String... properties ) throws SQLException {
    int result = 0;
    
    if( StoreConstraints.EXCLUDE_PROPERTIES==constraints ) {
        java.util.Arrays.sort(properties);
    }

    String sql = getStoreStatement(properties,constraints);

    Log.TRACE_CMD("store", sql );

    if( !isEmpty(sql) ) {

      PreparedStatement ps = conn.prepareStatement(sql);

      if( StoreConstraints.INCLUDE_PROPERTIES==constraints ) {
        setStoreStatementInclude( ps, bean, properties);
      }
      else {
        setStoreStatementExclude( ps, bean, properties);
      }

      result = ps.executeUpdate();

      ps.close();

    }

    return result;

 }

 
 /**
  * 
  * @param properties
  * @param constraints
  * @return
  */
 public final String getStoreAllStatement( String[] properties, StoreConstraints constraints  ) {

    if( properties==null || properties.length==0 ) {
      throw new IllegalArgumentException(
              BeanManagerUtils.getMessage("ex.parameter_is_empty",new Object[]{"properties"}) );
    }

    String propertyList = (constraints==StoreConstraints.INCLUDE_PROPERTIES) ?
            getStorePropertyListInclude("","","=?,",properties) :
            getStorePropertyListExclude("","","=?,",properties) ;

    String result = null;

    if( !isEmpty(propertyList) ) {
      result =  java.text.MessageFormat.format(STOREALLCMD, this.entity, propertyList );
    }

    return result;
  }

 /**
   *
   * @param ps
   * @param bean
   * @param properties
   * @param include
   * @return
   * @throws SQLException
   */
    private int setStoreAllStatementExclude( PreparedStatement ps,  Object bean, String... properties ) throws java.sql.SQLException
    {
        int i = 0;
        int ordinal = 1;
        Object value;

        try {

            for( i=0; i<props.length ; ++i ) {

              if( !props[i].isReadOnly() &&
                java.util.Arrays.binarySearch(properties,props[i].getName())<0 )
              {

                if( !props[i].isReadOnly() ) {
                  value = invokeReadMethod(props[i], bean);
                  setStatementValue( ps, ordinal++, value, props[i] );
                }
              }
            }

        }
        catch( Exception ex ) {
          Object[] params = { ex.getMessage(), props[i].getName() };
          throw new SQLException( BeanManagerUtils.getMessage("ex.set_store_stmt",params) );
        }

        return ordinal;

    }
    
    /**
    *
    * @param ps
    * @param bean
    * @param properties
    * @param include
    * @return
    * @throws SQLException
    */
    @SuppressWarnings("unchecked")
    private int setStoreAllStatementInclude( PreparedStatement ps, Object bean, String... properties ) throws java.sql.SQLException
    {

        int i = 0;
        int ordinal = 1;
        Object value;

        try {

            PropertyDescriptorField p;

            for( i=0; i<properties.length ; ++i ) {

              p = (PropertyDescriptorField)allProps.get(properties[i]);

              if( p.isReadOnly() || p instanceof PropertyDescriptorJoin )
                continue;

              value = invokeReadMethod(p, bean);
              setStatementValue( ps, ordinal++, value, p );
            }

        }
        catch( Exception ex ) {
          Object[] params = { ex.getMessage(), props[i].getName() };
          throw new SQLException( BeanManagerUtils.getMessage("ex.set_store_stmt",params) );
        }

        return ordinal;

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
  public int storeAll(Connection conn, T bean, StoreConstraints constraints, String... properties) throws SQLException {
   int result = 0;
    
    if( StoreConstraints.EXCLUDE_PROPERTIES==constraints ) {
        java.util.Arrays.sort(properties);
    }

    String sql = getStoreAllStatement(properties,constraints);

    Log.TRACE_CMD("store", sql );

    if( !isEmpty(sql) ) {

      PreparedStatement ps = conn.prepareStatement(sql);

      if( StoreConstraints.INCLUDE_PROPERTIES==constraints ) {
        setStoreAllStatementInclude( ps, bean, properties);
      }
      else {
        setStoreAllStatementExclude( ps, bean, properties);
      }

      result = ps.executeUpdate();

      ps.close();

    }

    return result;

  }

  /**
  *
  * @param ps
  * @param id
  * @param ordinal
  * @throws SQLException
  */
  private void setPrimaryKeyValues( PreparedStatement ps, Object[] ids, int ordinal ) throws SQLException {
    //int ordinal = 1;

    if( getPrimaryKey().getKeyCount() == 1 ) {
      this.setStatementValue(ps,ordinal,ids[0],getPrimaryKey().get(0));
    }
    else {

      if ( ids.length != getPrimaryKey().getKeyCount() ) {
        throw new java.lang.IllegalArgumentException(
          BeanManagerUtils.getMessage( "ex.invalid_key_param_length" )
          );
      }

      for( int i=0; i<getPrimaryKey().getKeyCount(); ++i ) {
        this.setStatementValue(ps,ordinal++,ids[i],getPrimaryKey().get(i));
      }

    }
 }

 /**
 delete bean from db using an id

 @param conn database connection
 @param id id value ( for composite key must be an Object array )
 @exception SQLException
 */
 public int removeById( Connection conn,  Object...id ) throws SQLException {
    String sql = this.getRemoveStatement();

    Log.TRACE_CMD( "removeById", sql );

    PreparedStatement ps = conn.prepareStatement(sql);

    this.setPrimaryKeyValues(ps,id, 1);

    int result = ps.executeUpdate();

    ps.close();

    return result;

 }

 /**
 delete bean from db

 <pre>
 Ex.:

 MyBean bean = (Bean)manager.findById( conn, ..  );

 ....

 int result = manager.remove( conn, bean );


 </pre>


 @param conn database connection
 @param bean instance to remove
 @exception SQLException
 */
 public int remove( Connection conn,  Object bean ) throws SQLException {
    try {

    String sql = this.getRemoveStatement();

    Log.TRACE_CMD( "remove", sql );

    PreparedStatement ps = conn.prepareStatement(sql);

    int ordinal = 1;
    Object value;

    for( int k=0; k<getPrimaryKey().getKeyCount(); ++k ) {
      PropertyDescriptorPK pk = getPrimaryKey().get(k);

      value = invokeReadMethod(pk, bean);
      setStatementValue( ps, ordinal++, value, pk );
    }

    int result = ps.executeUpdate();

    ps.close();

    return result;

    }
    catch( Exception ex ) {
      throw new SQLException( ex.getMessage() );
    }

 }

 /**
  * remove all bean from db
  * 
  * @param conn
  * @return
  * @throws java.sql.SQLException
  */
 public int removeAll( Connection conn ) throws SQLException {
    final String method = "removeAll";
    
    String sql = this.getRemoveAllStatement();

    Log.TRACE_CMD( method, sql );

    java.sql.Statement ps = null;
    
    int result = 0;
    
    try {
        ps = conn.createStatement();

        result = ps.executeUpdate( sql );

    }
    finally {
        close(ps);
    }
    
    return result;
     
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
        final String method = "findAndRemove";
        
        java.util.List<PropertyPosition> positions = new java.util.ArrayList<PropertyPosition>(10);

        String wc = parseWhere( where, positions );
        String c = this.getIdentityConditions();

        // BUILD WHERE CONDITIONS //////////////////////////////////////////
        StringBuilder conditions = new StringBuilder(256);
        if( !isEmpty(c) ) {
          conditions.append( c );
          if( !isEmpty( wc ) ) {
            conditions.append( " AND ");
          }
        }
        conditions.append( wc );
        ///////////////////////////////////////////////////////////////////

        String sql = java.text.MessageFormat.format(REMOVECMD, this.entity, conditions);

        Log.TRACE_CMD( method, sql );

        //Debug.assert( values.length==positions.size() );

        if( values.length<positions.size() )
          throw new Error( BeanManagerUtils.getMessage("find.assertion") );

        PreparedStatement ps = null;

        ps = conn.prepareStatement(sql);

        java.util.Collections.sort(positions);

        int startIndex = 1;

        for( int i=0; i<positions.size(); ++i ) {
          PropertyPosition pp = positions.get(i);
          this.setStatementValue(ps,startIndex++,values[i], pp.getProperty() );
        }

        int result = ps.executeUpdate();

        ps.close();

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
 public T findById( Connection conn, Object...id ) throws SQLException {
    String sql      = null;
    T bean     = null;
    PreparedStatement ps = null;

    try {

    sql = this.getFindByIdStatement();

    Log.TRACE_CMD( "findById", sql);

    ps = conn.prepareStatement(sql);

    this.setPrimaryKeyValues(ps,id,1);

    ResultSet rs =  ps.executeQuery();

    if( rs.next() ) {
      bean = instantiateBean();
      setBeanProperties( bean, rs );
    }

    rs.close();

    return bean;

    }
    catch( SQLException sqlex ) {
      throw sqlex;
    }
    catch( Exception ex ) {
      //ex.printStackTrace();
      throw new SQLException( ex.getMessage() );
    }

  }

 /**
 reload bean instance from db

 @param conn database connection
 @param bean  bean intance
 @return bean instance updated (same of parameter bean) - null if not found
 @exception SQLException
 @see #findById
 */
 public T loadBean( Connection conn, T bean ) throws SQLException {
    String sql      = null;
    PreparedStatement ps = null;
    T result = null;

    try {

    if( null==bean ) {
      throw new java.lang.IllegalArgumentException(
        BeanManagerUtils.getMessage("ex.param_bean_null") );
    }

    sql = this.getFindByIdStatement();

    Log.TRACE_CMD( "loadBean", sql);

    ps = conn.prepareStatement(sql);

    //////////////////////////////
    // SET PRIMARY KEY VALUES
    //////////////////////////////
    int ordinal = 1;
    Object value;

    for( int k=0; k<getPrimaryKey().getKeyCount(); ++k ) {
      PropertyDescriptorPK pk = getPrimaryKey().get(k);

      value = invokeReadMethod(pk, bean);
      setStatementValue( ps, ordinal++, value, pk );
    }
    /////////////////////////////////////////////////////////

    ResultSet rs =  ps.executeQuery();

    if( rs.next() ) {
      this.setBeanProperties( bean, rs );
      result = bean;
    }

    rs.close();

    return result;

    }
    catch( SQLException sqlex ) {
      throw sqlex;
    }
    catch( Exception ex ) {
      //ex.printStackTrace();
      throw new SQLException( ex.getMessage() );
    }

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
  public java.util.Collection<T> find( Connection conn, java.util.Collection<T> result, String where, Object... values ) throws SQLException
  {

    PreparedStatement ps =  this.prepareFind(conn, where, values);

    return this.find( ps, result );

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
  public BeanEnumeration<T> find( Connection conn, String where, Object... values ) throws SQLException
  {

    PreparedStatement ps = this.prepareFind(conn, where, values);

    return this.find(ps);


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
  public BeanEnumeration<T> find( PreparedStatement statement ) throws SQLException {

    ResultSet rs = statement.executeQuery();
    return new BeanEnumeration<T>( this, rs );

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
  public java.util.Collection<T> find( PreparedStatement statement, java.util.Collection<T> result ) throws SQLException {

    try {

    long time = Log.TRACE_TIME_BEGIN();

    ResultSet rs = statement.executeQuery();

    Log.TRACE_TIME_END("execution quey", time);

    T bean;

    time = Log.TRACE_TIME_BEGIN();

    while( rs.next() ) {
      bean = instantiateBean();
      setBeanProperties(bean,rs);
      result.add( bean );
    }

    Log.TRACE_TIME_END( "fill collection", time);

    rs.close();

    return result;

    }
    catch( SQLException sqlex ) {
      throw sqlex;
    }
    catch( Exception ex ) {
      throw new SQLException( ex.getMessage() );
    }
    finally {
      statement.close();
    }

  }

  /**
   *
   * @param conn
   * @param commandKey
   * @param where
   * @return
   * @throws SQLException
   */
  public PreparedStatement prepareCustomFind( Connection conn, String commandKey, String where ) throws SQLException
  {

    if( commandKey == null ) throw new IllegalArgumentException( BeanManagerUtils.getMessage("ex.param_0_is_null", "commandKey") );
    
    String cmd = entity.getCustomFindCommand(commandKey);

    if( isEmpty(cmd) ) {
      //throw new Error( BeanManagerUtils.getMessage("ex.custom_cmd_null") );
      cmd = commandKey;
    }

    String conditions = "";

    if( !isEmpty(where) ) {
        conditions = parseWhere( where, new java.util.ArrayList<PropertyPosition>() );
    }

    PreparedStatement ps = null;

    Object [] args = {
      this.getFindPropertyList( "","",","),
      this.entity,
      (joinRelations!=null) ? joinRelations.toString() : "",
      conditions
    };

    String sql = java.text.MessageFormat.format(cmd,args);

    Log.TRACE_CMD( "customFind", sql );

    ps = conn.prepareStatement(sql);

    return ps;
  }

  /**
   *
   * @param conn
   * @param where
   * @param values
   * @param startIndex
   * @return
   * @throws SQLException
   */
  protected PreparedStatement prepareFind(
            Connection conn,
            String where,
            Object... values ) throws SQLException
  {

    String sql      = null;
    PreparedStatement ps = null;
    java.util.List<PropertyPosition> positions = new java.util.ArrayList<PropertyPosition>();

    sql = this.getFindStatement( where, positions );

    Log.TRACE_CMD( "find", sql );

    //Debug.assert( values.length==positions.size() );

    if( values.length<positions.size() )
      throw new Error( BeanManagerUtils.getMessage("find.assertion") );

    ps = conn.prepareStatement(sql);

    java.util.Collections.sort(positions);

    int startIndex = 1;

    for( int i=0; i<positions.size(); ++i ) {
        PropertyPosition pp = positions.get(i);
        if( values[i] instanceof Collection ) {
            Collection<Object> valueCollection = (Collection<Object>) values[i];

            for( Object v : valueCollection ) {
                this.setStatementValue(ps,startIndex++, v, pp.getProperty() );
            }
        }
        else {
            this.setStatementValue(ps,startIndex++,values[i], pp.getProperty() );
        }
        //ps.setObject( i+1, values[i], pp.getProperty().getSQLType(), 0 );
    }

    return ps;
  }

 /**
 select all beans from db

 @param conn database connection
 @param collection used for store all the instance of bean
 @param sqlClouse sql clouse to append command ( like order by or group by ... ) cannot enter parameters can be null
 @return collection that contain all the instance of bean ( same of result parameter )
 @exception SQLException
 */
  public java.util.Collection<T> findAll( Connection conn, java.util.Collection<T> result, String sqlClouse ) throws SQLException
  {
    // PRE CONDITION
    if( null==result )
      throw new java.lang.IllegalArgumentException( BeanManagerUtils.getMessage("ex.param_result_null") );

    String sql      = null;
    T bean     = null;
    PreparedStatement ps = null;

    try {

    sql = this.getFindAllStatement();

    if( null!=sqlClouse && sqlClouse.length()>0 ) {
      sql = sql.concat( this.parseWhere( sqlClouse, null ) );
    }

    Log.TRACE_CMD( "findAll", sql );

    ps = conn.prepareStatement(sql);

    ResultSet rs =  ps.executeQuery();

    while( rs.next() ) {
      bean = instantiateBean();
      setBeanProperties(bean,rs);

      result.add( bean );
    }

    rs.close();

    return result;

    }
    catch( SQLException sqlex ) {
      throw sqlex;
    }
    catch( Exception ex ) {
      //ex.printStackTrace();
      throw new SQLException( ex.getMessage() );
    }

  }

 /**
 select all beans from db

 @param conn database connection
 @param sqlClouse sql clouse to append command ( like order by or group by ... ) cannot enter parameters can be null
 @return enumeration of retreived beans
 @exception SQLException
 */
  public BeanEnumeration<T> findAll( Connection conn, String sqlClouse ) throws SQLException  {

    String sql      = null;
    PreparedStatement ps = null;
    BeanEnumeration<T> result = null;
    try {

    sql = this.getFindAllStatement();

    
    if( !isEmpty(sqlClouse) ) {
      sql = sql.concat( this.parseWhere( sqlClouse, null ) );
    }

    Log.TRACE_CMD( "findAll", sql );

    ps = conn.prepareStatement(sql);

    ResultSet rs =  ps.executeQuery();

    result = new BeanEnumeration<T>( this, rs );

    }
    catch( SQLException sqlex ) {
      throw sqlex;
    }
    catch( Exception ex ) {
      //ex.printStackTrace();
      throw new SQLException( ex.getMessage() );
    }

    return result;
  }

  /**
   * select beans from db using a stored procedure
   * <pre>
   * the stored procedure must be designed for return a resultset
   * and the input parameter must be only of IN type , the OUT and INOUT parameters
   * are not currently supported.
   *
   * The <b>entity name</b> stored in BeanDescriptor object must be the name of the
   * stored procedure.
   * </pre>
   *
   * <b>NB</b>
   * can close the connection before you use the collection
   *
   *@param conn database connection
   *@param result collection where append the instance of bean
   *@param  params stored procedure parameter ( only IN parameter accepted )
   *@return the collection that contain the bean instances ( see result parameter)
   */
  public java.util.Collection<T> execFind( Connection conn, java.util.Collection<T> result, Object... params ) throws SQLException
  {
    try {

    // PRE CONDITION
    if( null==result )
      throw new java.lang.IllegalArgumentException( BeanManagerUtils.getMessage("ex.param_result_null") );

    ResultSet rs = DataBaseUtils.callStoredQuery( conn, this.getEntity(), params );

    // FILL THE RESULT COLLECTION
    T bean;

    while( rs.next() ) {
      bean = instantiateBean();
      setBeanProperties(bean,rs);

      result.add( bean );
    }

    rs.close();

    }
    catch( SQLException sqlex ) {
      throw sqlex;
    }
    catch( Exception ex ) {
      //ex.printStackTrace();
      throw new SQLException( ex.getMessage() );
    }

    return result;


  }
  
  /**
   * safe resultset close
   * 
   * @param rs
   */
  protected void close( ResultSet rs  ) {
        if (null == rs) {
            return;
        }
        try {
            rs.close();
        } catch (SQLException ex) {
            Log.warn("close(ResultSet)", ex);
        }
     
     
  }
  /**
   * safe statement close
   * 
   * @param s
   */
  protected void close( java.sql.Statement s  ) {
        if (null == s) {
            return;
        }
        try {
            s.close();
        } catch (SQLException ex) {
            Log.warn("close(Statement)", ex);
        }     
  }
}
