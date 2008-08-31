package org.bsc.bean;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.sql.Types;

import org.bsc.util.Configurator;

/**
PropertyDescriptor extension for manage generic DataBase field
 *
 * <pre>
 * PropertyDescriptorField _prop = new PropertyDescriptorField("<i>propName</i>", beanClass, "get<i>propName</i>", "set<i>propName</i>");
 * _prop.setFieldName("<i>field name</i>");
 * _prop.setSQLType( java.sql.Types.XXXXX );
 * </pre>

*/
public class PropertyDescriptorField extends PropertyDescriptor {

 public static final String FIELDNAME       = "fieldName";
 public static final String SQLTYPE         = "sqlType";
 public static final String PRIMARYKEY      = "primaryKey";
 public static final String HAVEDEFAULT     = "_hDef";
 public static final String READONLY        = "readOnly";
 public static final String ADAPTER         = "adapter";
 public static final String  ENTITY         = "entity";
 public static final String  DEREF          = "_deref";
 public static final String  FUNC           = "_func";
 public static final String DEFAULT_VALUE   = "def.value";
 public static final String SIZE            = "f.size";
 public static final String REQUIRED        = "f.required";
 
 

  /** to implement intelligent update command */

  //public static final String UPDATEHANDLER  = "updateHandler";
  //private boolean haveDefault;
  //private boolean readOnly;



 /**
 *   for default SQL type is set to Types.VARCHAR
 *   for default field name is set to property name
 */
 public PropertyDescriptorField(String propertyName,
                           Class<?> beanClass,
                           String getterName,
                           String setterName) throws IntrospectionException
 {
  super( propertyName, beanClass, getterName, setterName );
  this.setFieldName( this.getName() );
  this.setDerefName( false );
  this.setValue( SQLTYPE, new Integer(Types.VARCHAR) );
  this.setReadOnly( false );
  this.setSize(0);
  this.setRequired(true);
 }

 /**
 *   for default SQL type is set to Types.VARCHAR
 *   for default field name is set to property name
 */
 public PropertyDescriptorField(String propertyName,
                           Method getter,
                           Method setter) throws IntrospectionException
 {
  super( propertyName, getter, setter );
  this.setFieldName( this.getName() );
  this.setDerefName( false );
  this.setValue( SQLTYPE, new Integer(Types.VARCHAR) );
  this.setReadOnly( false );
  this.setSize(0);
  this.setRequired(true);
 }

 /**
  *
  * @return String
  */
 @Override
 public String toString() {
   return "PropertyDescriptorField " + getName() + " " + getFieldName();
 }
 /**
  *
  * @param key
  * @param defValue
  * @return
  */
  protected void setBooleanValue( String key , boolean value ) {
     this.setValue(key, value);
  }

 /**
  *
  * @param key
  * @param defValue
  * @return
  */
  protected boolean getBooleanValue( String key ) {
    return Boolean.TRUE.equals(this.getValue(key));
  }
  
  /**
   * format the field name for query statements (find,findAll,findById)
   *
   * @return
   */
  protected StringBuilder appendQueryFieldName(StringBuilder sb, String entity) {

    StringBuilder f = new StringBuilder(30);
    f.append(entity);
    f.append('.');
    f.append( this.getValue(FIELDNAME) );

    //StringBuffer sb = new StringBuffer(64);

    if( getFunctionPattern()!=null ) {
      sb.append( java.text.MessageFormat.format( getFunctionPattern(), f ) );
    }
    else {
      sb.append( f );
    }

    //sb.append( this.getValue(FIELDNAME) );
    if( isDerefName() ) {
      sb.append( " AS " );
      sb.append( getName() );
    }
    return sb;
  }

  /**
  * obtain the dereferenced field name
  *
  * NB.:
  *
  * if the property <b>derefName</b> is true return the property name
  * @return
  * @see #isDerefName
  */
  public String getDerefFieldName() {
    return (isDerefName()) ?
      this.getName() : (String)this.getValue(FIELDNAME);
  }

 /**
  * obtain the field name
  *
  * @return
  */
 public String getFieldName() {
    return (String)this.getValue(FIELDNAME);
 }

 /**
  * set the field name associed with property
  *
  * <pre>
  * <b>Note:</b>
  *
  * depends of lowercase setting
  * </pre>
  * @param name
  * @see Configurator#isLowerCase
  */
 public PropertyDescriptorField setFieldName( String name ) {
  if( Configurator.isLowerCase() ) {
    this.setValue( FIELDNAME, name.toLowerCase() );
  }
  else {
    this.setValue( FIELDNAME, name );
  }
  return this;
 }

 /**
  *
  * @return
  */
 public int getSQLType() {
  return ((Integer)this.getValue( SQLTYPE )).intValue();
 }

 /**
 set SQL type
 <pre>
 <table>
 <tr><th>property type<th>SQL type
 <tr><td>bool<td>Types.BIT
 <tr><td>int,long<td>Types.INTEGER
 <tr><td>double,float<td>Types.DOUBLE
 <tr><td>String<td>Types.VARCHAR (<i>default</i>)
 </table>
 </pre>

 @param type SQL type defined into java.sql.Types
 @see java.sql.Types
 */
 public PropertyDescriptorField setSQLType( int type ) {
  this.setValue( SQLTYPE, new Integer(type) );
  return this;
 }

 /**
  *
  * @return
  */
 public boolean isPrimaryKey() {
  return getBooleanValue(PRIMARYKEY);
 }

  /**
  set output format pattern based on java.text.* package

  this method is same of : <i>property</i>.setValue( BeanManagerUtils.FORMATPATTERN, pattern );

  <pre>
  Example.:

  <b>Boolean</b> use {0,choice,0#<i>false string</i>|1#<i>true string</i>}
  <b>Number</b> like class DecimalFormat
  <b>Date</b> like SimpleDateFormat
  </pre>

  @param format pattern
  @see java.text.DecimalFormat
  @see java.text.SimpleDateFormat
  */
  public void setFormatPattern( String pattern ) {
    this.setValue( BeanManagerUtils.FORMATPATTERN, pattern );
  }

  /**
  *
  * @param readOnly
  */
  public void setReadOnly(boolean value) {
    setBooleanValue(READONLY, value);
  }

  /**
  *
  * @return
  */
  public boolean isReadOnly() {
    return getBooleanValue(READONLY);
  }

  /**
   * this property means that into query command this field
   * name will be dereferenced in the follow way
   *
   * SELECT ...., <table>.<field name> AS <property name>
   *
   * @param value
   */
  public void setDerefName(boolean value) {
    setBooleanValue(DEREF,value);
  }

  /**
  * check if this field name will be dereferentiated into
  * query commands
  *
  * @return
  */
  public boolean isDerefName() {
    return getBooleanValue(DEREF);
  }

  /**
   *
   * @param adapter
   */
  public PropertyDescriptorField setAdapter(DataAdapter adapter) {
    if( adapter!=null ) {
      this.setValue(ADAPTER, adapter);
      
    }
    
    return this;
  }

  /**
   *
   * @return
   */
  public DataAdapter getAdapter() {
    return (DataAdapter)this.getValue(ADAPTER);
  }

  /**
   * <pre>
   *
   * set SQL function pattern (MessageFormat compatible) for field.
   * the {0} identify the field name
   *
   * Ex.:
   *
   * <i>property</i>.setFunctionPattern( "MAX( {0} )");
   *
   * </pre>
   *
   * @param pattern a like java.text.MessageFormat pattern
   * @see java.text.MessageFormat#format
   */
  public PropertyDescriptorField setFunctionPattern( String pattern ) {
    if( pattern!=null ) {
      this.setValue(FUNC, pattern);
    }
    return this;
  }

  /**
   *
   * @param pattern
   */
  public String getFunctionPattern() {
    return (String)this.getValue( FUNC );
  }

  /**
   * 
   * @param size
   * @return
   */
  public PropertyDescriptorField setSize( int size ) {
        this.setValue(SIZE, size);          
        return this;
  }

  /**
   * 
   * @return
   */
  public int getSize() {
      return (Integer)this.getValue(SIZE);
  }
  
  /**
   * 
   * @param size
   * @return
   */
  public PropertyDescriptorField setRequired( boolean value ) {
        this.setValue(REQUIRED, value);          
        return this;
  }

  /**
   * 
   * @return
   */
  public boolean isRequided() {
      return getBooleanValue(REQUIRED);
  }
  
}
