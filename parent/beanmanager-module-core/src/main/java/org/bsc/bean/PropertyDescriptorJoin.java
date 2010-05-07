

package org.bsc.bean;

import java.beans.IntrospectionException;

/**
property that identify a field joined with another table field
 *
 * <pre>
 *
 * PropertyDescriptorField <b>_propID</b> = new PropertyDescriptorField("propID", ... );
 * .
 * .
 * .
 *
 * PropertyDescriptorJoin _propJ = new PropertyDescriptorJoin("propJ", beanClass, "getPropJ", "setPropJ");
 * _numeroFattura.setFieldName("<i>field name</i>");
 * _numeroFattura.setJoinTable("<i>join table</i>" );
 * </pre>

*/
public class PropertyDescriptorJoin extends PropertyDescriptorField {

public static final String JOINTABLE = PropertyDescriptorField.ENTITY;

 /**
 */
 public PropertyDescriptorJoin(String propertyName,
                           Class<?> beanClass,
                           String getterName,
                           String setterName) throws IntrospectionException
 {
  super( propertyName, beanClass, getterName, setterName );
 }

 /**
  *
  * @param propertyName
  * @param getter
  * @param setter
  * @throws IntrospectionException
  */
 public PropertyDescriptorJoin(String propertyName,
                            java.lang.reflect.Method getter,
                            java.lang.reflect.Method setter) throws IntrospectionException
 {
  super( propertyName, getter, setter );
 }

 /**
  *
  * @return String
  */
    @Override
 public String toString() {
   return "PropertyDescriptorJoin " + getName() + " " + getFieldName() + " " + getJoinTable();
 }

 /**
  *
  */
 public String getJoinTable() {
  return (String)this.getValue(JOINTABLE);
 }

 /**
  *
  */
 public PropertyDescriptorJoin setJoinTable( String joinTable ) {
  this.setValue(JOINTABLE,joinTable);
  return this;
 }

}
