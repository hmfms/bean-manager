

package org.bsc.bean;

import java.beans.IntrospectionException;
import java.sql.Types;

/**
 * property that identify a PrimaryKey field
 * 
 * Default data type integer
 *
 * <pre>
 * PropertyDescriptorPK _prop = new PropertyDescriptorPK("<i>propName</i>", beanClass, "get<i>propName</i>", "set<i>propName</i>");
 * _prop.setFieldName("<i>field name</i>");
 * _prop.setSQLType( java.sql.Types.XXXXX );
 * </pre>

*/
public class PropertyDescriptorPK extends PropertyDescriptorField {

 public static final String COUNTER = "counter";

 /**
 for default SQL type is set to Types.INTEGER
 */
 public PropertyDescriptorPK(String propertyName,
                           Class<?> beanClass,
                           String getterName,
                           String setterName) throws IntrospectionException
 {
  super( propertyName, beanClass, getterName, setterName );
  this.setValue( SQLTYPE, new Integer(Types.INTEGER) );
  this.setValue( PRIMARYKEY, Boolean.TRUE );
 }

 /**
  *
  * @return String
  */
 public String toString() {
   return "PropertyDescriptorPK " + getName() + " " + getFieldName() ;
 }

 public boolean isCounter() {
  Object v = this.getValue(COUNTER);
  if( v==null ) { return false; }
  return ((Boolean)v).booleanValue();
 }

}
