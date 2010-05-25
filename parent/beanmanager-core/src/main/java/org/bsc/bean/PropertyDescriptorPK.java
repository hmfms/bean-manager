

package org.bsc.bean;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
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

 public static final String AUTO_GENERATE = "auto.generate";

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

 public PropertyDescriptorPK( PropertyDescriptor pd ) throws IntrospectionException
 {
  super( pd.getName(), pd.getReadMethod(), pd.getWriteMethod() );
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

 
 public final boolean isAutoGenerate() {
     return null!=getValue(AUTO_GENERATE);
 }

 public ValueGenerator<?> getValueGenerator() {
     return (ValueGenerator<?>) getValue( AUTO_GENERATE );
 }
 public final PropertyDescriptorPK setValueGenerator( ValueGenerator<?> vg ) {
     setValue( AUTO_GENERATE, vg );
     return this;
 }
}
