package org.bsc.bean;

import static org.bsc.bean.PropertyDescriptorField.DEFAULT_VALUE;

import java.beans.BeanDescriptor;
import java.beans.BeanInfo;
import java.beans.Beans;
import java.beans.EventSetDescriptor;
import java.beans.MethodDescriptor;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;

import org.bsc.bean.dyna.DynaPropertyDescriptor;
import org.bsc.bean.dyna.DynaPropertyDescriptorJoin;
import org.bsc.util.Configurator;
import org.bsc.util.Log;

/**
 * Class utility
 *
 * <p>Title: Bean Manager </p>
 * <p>Description: ORM framework</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: </p>
 * @author BARTOLOMEO Sorrentino
 * @version 1.0
 */
public class BeanManagerUtils {

public static final String                      FORMATPATTERN                   = "formatPattern";
public static final EventSetDescriptor[]	EMPTY_EVENTDESCRIPTOR		= new EventSetDescriptor[0];
public static final MethodDescriptor[]		EMPTY_METHODDESCRIPTOR		= new MethodDescriptor[0];
public static final PropertyDescriptor[]	EMPTY_PROPERTYDESCRIPTOR	= new PropertyDescriptor[0];
public static final BeanInfo[] 			EMPTY_ADDITIONALBEANINFO	= new BeanInfo[0];

public static final BeanManagerMessages messages = Configurator.getMessages();

/**
load a BeanInfo for URI

If uri string start with character '/'  the bean info will be loaded using
XmlBeanInfo.loadFromUri factory method, otherwise the uri will be
loaded using Class.forName

@todo implement support of xml using digester
@param loader classloader used for load the resource ( null for default )
@param uri resource uri
*/
static public BeanInfo loadBeanInfo( ClassLoader loader, String uri ) {
    if( uri==null ) return null;

    BeanInfo result = null;

    try {

    if( loader==null ) loader = BeanManagerUtils.class.getClassLoader();

    if( uri.startsWith("/") ) {
      //java.io.InputStream is = loader.getResourceAsStream( uri.substring(1) );
      //result = XmlBeanInfo.loadFromUri( new InputSource(is) );
      throw new UnsupportedOperationException( "load beanInfo from external source (e.g. xml) is not allowed");
    }
    else {
      Class<?> beanInfoClass =  Class.forName(uri,true,loader);

      result = (BeanInfo)beanInfoClass.newInstance();
    }

    return  result;

    }
    catch( Exception ex ) {
      Log.error( "BeanUtil.loadBeanInfo exception", ex );
    }

    return null;
}

/**
 * return an unmodifiable Map that contains only PropertyDescriptorField
 *
 * @param pp property descriptor array
 *
 * @see bsc.bean.PropertyDescriptorField
*/
@SuppressWarnings("unchecked")
static public <T> java.util.Map<String, PropertyDescriptorField> getPropertyFieldMap( PropertyDescriptor [] pp ) {

  if(pp==null) return Collections.emptyMap();

  try {

  java.util.Map<String,PropertyDescriptorField> fields = new java.util.HashMap<String,PropertyDescriptorField>(pp.length);

  for( int i=0; i<pp.length ; ++i ) {
    if( pp[i] instanceof PropertyDescriptorField ) {
      fields.put( pp[i].getName(), (PropertyDescriptorField)pp[i] );
    }
  }

  return java.util.Collections.unmodifiableMap(fields);

  }
  catch( Exception ex ) {
    Log.error( "BeanUtil.getPropertyFields exception", ex );
  }

  return Collections.emptyMap();

}

/**
load a BeanInfo associate with bean

@param loader classloader used for load the BeanInfo
@param loader classloader used for load the class ( null for default )
@param beanClass bean class

*/
static public BeanInfo loadBeanInfo( ClassLoader loader, Class<?> beanClass ) {
    if( beanClass==null ) return null;

    try {

    String className = beanClass.getName().concat("BeanInfo");

    return loadBeanInfo( loader, className );

    }
    catch( Exception ex ) {
      Log.error( "BeanUtil.getBeanInfo exception", ex );
    }

    return null;
}

/**
map into Hashtable all the PropertyDescriptor inside BeanInfo

@param beanInfo BeanInfo instance

@see BeanManagerUtils#getBeanInfo
@see java.beans.PropertyDescriptor
*/
static public java.util.Map<String,PropertyDescriptor> getProperties( BeanInfo beanInfo ) {
  if(beanInfo==null) return null;

  try {

  PropertyDescriptor [] pp = beanInfo.getPropertyDescriptors();

  java.util.Map<String,PropertyDescriptor> fields = new java.util.HashMap<String,PropertyDescriptor>(pp.length);

  for( int i=0; i<pp.length ; ++i ) {
      fields.put( pp[i].getName(), pp[i] );
  }

  return fields;

  }
  catch( Exception ex ) {
    Log.error( "BeanUtil.getProperties exception", ex );
  }

  return null;

}

/**
create a default BeanDescriptor
<pre>
  Example:

  public BeanDescriptor getBeanDescriptor() {
    return BeanManagerUtils.createBeanDescriptor( beanClass, "<i>&lt;entity&gt;</i>" );
  }

</pre>

@param beanClass bean Class
@param entityName name of db entity (table)
@return new BeanDescriptor

@see java.beans.BeanDescriptor
@see java.beans.BeanInfo#getBeanDescriptor
 */
public static java.beans.BeanDescriptor createBeanDescriptor( Class<?> beanClass, String entityName ) {
    BeanDescriptor bs = new BeanDescriptor(beanClass);
    bs.setValue( "entityName", entityName );
    return bs;
}

 /**
 * return property value
 *
 *@param property
 *@param bean bean instance
 *@return property value
 */
public static Object getPropertyValue( PropertyDescriptor p, Object bean ) {

    java.lang.reflect.Method get = p.getReadMethod();
    try {
    return get.invoke( bean );
    }
    catch( Exception ex ) {
      Log.error("getPropertyValue", ex);
    }

    return null;
}

 /**
  * get all properties of bean info ( getAdditionalBeanInfo also )
  *
  * - if there is a name conflict the beanInfo properties have the precedence
  *
  * @todo implement the join condition(s) inheritance
  * @param beanInfo BeanInfo
  * @return PropertyDescriptor[] all properties as array
  * @Deprecated use aggregateProperties
  */
 @Deprecated
 public static PropertyDescriptor[] getBeanProperties( BeanInfo beanInfo ) {
   if (beanInfo == null) {
     throw new java.lang.IllegalArgumentException(getMessage(
         "ex.param_0_is_null", new Object[] {"beanInfo"}));
   }

   java.util.Map<String,PropertyDescriptor> propMap = _getInheritProperties( beanInfo );

   PropertyDescriptor[] propArray = new PropertyDescriptor[ propMap.size() ];
   propMap.values().toArray( propArray );

   if( Log.isTraceEnabled() ) {
     Log.trace( "-- bean properties: " );
     for( int i=0; i<propArray.length; ++i ) {
       Log.trace( "-- " + propArray[i] );
     }
   }

   return propArray;
 }


 /**
  *
  * @param bi BeanInfo
  */
 private static java.util.Map<String,PropertyDescriptor> _getInheritProperties( BeanInfo bi ) {

   java.util.Map<String,PropertyDescriptor> result = new java.util.LinkedHashMap<String,PropertyDescriptor>();

   // Copy Properties
   PropertyDescriptor[] p= bi.getPropertyDescriptors();
   for( int i=0; i<p.length; ++i ) {
     result.put( p[i].getName(), p[i] );
   }

   BeanInfo[] add = bi.getAdditionalBeanInfo();

   if( add!=null && add.length>0 ) {

     for (int i = 0; i < add.length; ++i) {
       java.util.Map<String,PropertyDescriptor> pMap = _getInheritProperties( add[i] );

       BeanDescriptor bd = add[i].getBeanDescriptor();
       Object entityName =null;
       if (bd != null && (entityName = bd.getValue(BeanDescriptorEntity.ENTITY_NAME)) != null) {
         _inheritAggregateProperties( result, pMap, (String)entityName );
       }
       else {
         _inheritProperties( result, pMap );
       }
       pMap = null;
     }
   }

   return result;
 }

  /**
   * inherit property from a aggregate beanInfo
   *
   * @param propTo Map
   * @param propFrom Map
   * @param entityName String
   */
  @SuppressWarnings("unchecked")
private static void _inheritAggregateProperties(    java.util.Map<String,PropertyDescriptor> propTo,
                                                    java.util.Map<String,PropertyDescriptor> propFrom,
                                                    String entityName )
  {

    PropertyDescriptor item = null;

    for( String key : propFrom.keySet() ) {

      if( propTo.containsKey(key) ) continue;

      item = propFrom.get(key);

      inheritAggregateProperty(propTo, item, entityName);

    }
  }

  /**
   * inherit properties from beaninfo
   *
   * @param propTo Map
   * @param propFrom Map
   */
  private static void _inheritProperties(  java.util.Map<String,PropertyDescriptor> propTo,
                                           java.util.Map<String,PropertyDescriptor> propFrom )
  {
    
	  for( String key: propFrom.keySet() ) {
	
		  if( propTo.containsKey(key) ) continue;
	
		  propTo.put( key, propFrom.get(key));
	  }
  }

  /**
   * 
   * @param sqlType			sql type belong to java.sql.Types
   * @param returnRawType	if true will be returned the raw java type (byte, char, int ...), otherwise not (Byte, Character, Integer, ...)
   * @param ignoreSqlType   if true the class belong to java.sql package will be ignored. (e.g. instead of Blob will be returned Byte[] or byte[] depends on previous flag) 
   * @return related java class
   */
  public static Class<?> getJavaType( int sqlType, boolean returnRawType, boolean ignoreSqlType ) {
	
	  switch( sqlType ) {
	 
	  case Types.CHAR:
	  case	Types.VARCHAR:
	  case	Types.LONGVARCHAR:
		return String.class;
	  case	Types.NUMERIC:
	  case	Types.DECIMAL:
		  return java.math.BigDecimal.class;
	  case Types.BIT:
	  	return (returnRawType) ? boolean.class : Boolean.class;
	  case Types.TINYINT:
		  return (returnRawType) ? byte.class : Byte.class;
	  case Types.SMALLINT:
		  return (returnRawType) ? short.class : Short.class;
	  case Types.INTEGER:
		  return (returnRawType) ? int.class : Integer.class;
	  case Types.BIGINT:
		  return (returnRawType) ? long.class : Long.class;
	  case Types.REAL:
		  return (returnRawType) ? float.class : Float.class;
	  case Types.FLOAT:
	  case Types.DOUBLE:
		  return (returnRawType) ? double.class : Double.class;
	  case Types.BINARY:
	  case Types.VARBINARY:
	  case Types.LONGVARBINARY:
		  return (returnRawType) ? byte[].class : Byte[].class;
	  case Types.DATE:
		  return (ignoreSqlType) ? java.util.Date.class : java.sql.Date.class;
	  case Types.TIME:
		  return (ignoreSqlType) ? java.util.Date.class : java.sql.Time.class;
	  case Types.TIMESTAMP:
		  return (ignoreSqlType) ? java.util.Date.class : java.sql.Timestamp.class;
	  case Types.CLOB:
		  if( ignoreSqlType ) {
			  return ( returnRawType ) ? char[].class : Character[].class;				  
		  }
		  else {
			  return java.sql.Clob.class;
		  }
	  case Types.BLOB:
		  if( ignoreSqlType ) {
			  return ( returnRawType ) ? byte[].class : Byte[].class;				  
		  }
		  else {
			  return java.sql.Blob.class;
		  }
	  case Types.ARRAY:
	  if( ignoreSqlType ) {
		  return Object[].class;				  
	  }
	  else {
		  return java.sql.Array.class;
	  }
	  case Types.STRUCT:
		  if( ignoreSqlType ) {
			  return Object.class;				  
		  }
		  else {
			  return java.sql.Struct.class;
		  }
	  case Types.REF:
		  if( ignoreSqlType ) {
			  return Object.class;				  
		  }
		  else {
			  return java.sql.Ref.class;
		  }
	  case Types.JAVA_OBJECT:
	  case Types.DISTINCT:
		  return Object.class;
	  }
	  
	  return Object.class;
}
  
  
 /**
  * 
  * @param clazz
  * @return
  */
 public static int getSQLType( Class<?> clazz ) {
    
     if( String.class.equals(clazz)) {
         return Types.VARCHAR;
     }
     if( BigInteger.class.equals(clazz) ) {
         return Types.NUMERIC;
     }
     if( BigDecimal.class.equals(clazz) ) {
         return Types.NUMERIC;
     }
     if( Boolean.class.equals(clazz) || Boolean.TYPE.equals(clazz)) {
         return Types.BIT;
     }
     if( Integer.class.equals(clazz) || Integer.TYPE.equals(clazz)) {
         return Types.INTEGER;
     }
     if( Long.class.equals(clazz) || Long.TYPE.equals(clazz)) {
         return Types.BIGINT;
     }
     if( Float.class.equals(clazz) || Float.TYPE.equals(clazz)) {
         return Types.REAL;
     }
     if( Double.class.equals(clazz) || Double.TYPE.equals(clazz)) {
         return Types.DOUBLE;
     }
     if( clazz.isArray() ) { 
         if((Byte.class.equals(clazz.getComponentType()) || Byte.TYPE.equals(clazz.getComponentType()))) {
            return Types.VARBINARY;
         }
         if((Character.class.equals(clazz.getComponentType()) || Character.TYPE.equals(clazz.getComponentType()))) {
             return Types.VARCHAR;
          }
         return Types.ARRAY;
     }
     if( java.sql.Date.class.equals(clazz)) {
         return Types.DATE;
     }
     if( java.sql.Time.class.equals(clazz)) {
         return Types.TIME;
     }
     if( java.sql.Timestamp.class.equals(clazz)) {
         return Types.TIMESTAMP;
     }
     
     return Types.JAVA_OBJECT;
 }
  
 /**
  * Find the right adapter for the property
  *
  * @param p
  * @return
  */
 public static DataAdapter lookupAdapter( PropertyDescriptorField p ) throws SQLException {
	 DataAdapter adapter = p.getAdapter();

	 if( adapter==null ) {

		 switch( p.getSQLType() ) {

		 case Types.DATE:
		 case Types.TIME:
		 case Types.TIMESTAMP:
			 adapter = DataAdapter.DATETIME;
			 break;

		 case Types.CHAR:
			 adapter = DataAdapter.CHAR;
			 break;

		 case Types.JAVA_OBJECT:
			 adapter = DataAdapter.JAVA_OBJECT;
			 break;

		 case Types.INTEGER:
		 case Types.DOUBLE:
		 case Types.FLOAT:
		 case Types.REAL:
		 case Types.NUMERIC:
		 case Types.BIGINT:
		 case Types.DECIMAL:
		 case Types.SMALLINT:
		 case Types.TINYINT:
			 adapter = DataAdapter.NUMBER;
			 break;

		 case Types.VARCHAR:
		 case Types.LONGVARCHAR:
			 adapter = DataAdapter.VARCHAR;
			 break;
		 case Types.CLOB:
			 adapter = DataAdapter.CLOB;
			 break;
		 case Types.BLOB:
		 case Types.LONGVARBINARY:
		 case Types.VARBINARY:
		 case Types.BINARY:
			 adapter = DataAdapter.BLOB;
			 break;

		 default:
			 adapter = DataAdapter.GENERIC;
			 break;
		 }

		 p.setAdapter(adapter);
	 }

	 return adapter;
 }

/////////////////////// MESSAGES SECTION ///////////////////////

 /**
  * get resource messages with param
  *
  * @param key message key
  * @param params message parameters ( like java.textMessageFormat )
  * @return formatted resource message
  * @see java.text.MessageFormat
  */
 public static String getMessage(String key, Object... params) {

   String result = getMessage(key);

   return java.text.MessageFormat.format(result, (Object[])params);
 }

 /**
  * get resource message
  *
  * @param key message key
  * @return resource message
  */
 public static String getMessage(String key) {
   if (null == key)
     return "";
   if (null == messages)
     return key;

   String result = messages.bundle.getString(key);

   return (null == result) ? key : result;
 }

 /**
  * 
  * @param pd
  * @return
  */
 public static Object invokeReadMethod( PropertyDescriptor pd, Object beanInstance ) throws Exception {
    
     if( pd instanceof DynaPropertyDescriptor ) {
        
        Method m = ((DynaPropertyDescriptor)pd).getMappedReadMethod();
        
        if( null==m ) return null;
        
        return  m.invoke(beanInstance, pd.getName());
     }
     
     Method m = pd.getReadMethod();

     if( null==m ) return null;
     
     return m.invoke(beanInstance);
 }

 /**
  * 
  * @param pd
  * @return
  */
 public static Object invokeWriteMethod( PropertyDescriptor pd, Object beanInstance, Object value ) throws Exception {

     if( pd instanceof DynaPropertyDescriptor ) {
        
        Method m = ((DynaPropertyDescriptor)pd).getMappedWriteMethod();
        
        if( null==m ) return null;
        
        return  m.invoke(beanInstance, pd.getName(), value);
     }
     
     Method m = pd.getWriteMethod();

     if( null==m ) return null;
     
     return m.invoke(beanInstance,value);
    
 }
 
 /**
  * create a new bean as described within given beanInfo
  * 
  * If there are dynamic property descriptors will be invoked the setter method for each property with null value
  * 
  * @param beanInfo
  * @return bean instance
  * @throws  java.lang.InstantiationException
  */
 public static Object instantiateBean( BeanInfo beanInfo ) throws InstantiationException {
        Object bean = null;
        
        Class<?> beanClass = beanInfo.getBeanDescriptor().getBeanClass();
        
        try {
          bean = Beans.instantiate(
              beanClass.getClassLoader(),
              beanClass.getName());
             
          PropertyDescriptor[] props = beanInfo.getPropertyDescriptors();
          
          for( PropertyDescriptor pd: props ) {
              
              if( pd instanceof DynaPropertyDescriptor ) {
                  
                  Object defaultValue = pd.getValue(DEFAULT_VALUE);
                  
                  invokeWriteMethod(pd, bean, defaultValue);
              }
          }

        }
        catch (Exception ex) {
          throw new InstantiationException(ex.getMessage());
        }
 
        return bean;
 }

 /**
  * Create a new property array as result of joining the properties arrays given in input 
  * 
  * @param a
  * @param b
  * @return new array ( a + b )
  */
 public static PropertyDescriptor[] joinProperties( PropertyDescriptor[] a, PropertyDescriptor... b) 
 {
     
     PropertyDescriptor result[] = new PropertyDescriptor[ a.length + b.length ];
     
     System.arraycopy(a, 0, result, 0, a.length);
     System.arraycopy(b, 0, result, a.length, b.length);
     
     return result;
     
 }

    /**
    * inherit property from a aggregate beanInfo
    *
    * @param propTo Map
    * @param propFrom Map
    * @param entityName String
    */
    private static void inheritAggregateProperty( java.util.Map<String,PropertyDescriptor> propTo, PropertyDescriptor item, String entityName )  {

      if( item instanceof PropertyDescriptorField ) {

        if( !(item instanceof PropertyDescriptorJoin) ) {

            try {
              PropertyDescriptorField field = (PropertyDescriptorField)item;
              PropertyDescriptorJoin joinP = null;
              
              if( field instanceof DynaPropertyDescriptor ) {

            	  joinP = new DynaPropertyDescriptorJoin((DynaPropertyDescriptor)field);
            	  
              }
              else {
            	  joinP = new PropertyDescriptorJoin(field.getName(),
                                                 field.getReadMethod(),
                                                 field.getWriteMethod());
              }
              Enumeration<String> names = field.attributeNames();
              while( names.hasMoreElements() ) {
                  final String n = names.nextElement();
                  joinP.setValue(n, field.getValue(n));
              }
              joinP.setJoinTable(entityName);

              item = joinP;
              joinP = null;

            }
            catch (Exception ex) {
              Log.warn( "joinProperties "+ item.getName() + " exception ", ex );
            }

          }

        }
        propTo.put(item.getName(), item);
  }

 private static void aggregateProperties( java.util.Map<String,PropertyDescriptor> propertyMap, BeanInfo bi ) {
    BeanDescriptor bd = bi.getBeanDescriptor();
    Object entityName =null;
    boolean aggregate = (bd != null && (entityName = bd.getValue(BeanDescriptorEntity.ENTITY_NAME)) != null);

    for( PropertyDescriptor p: bi.getPropertyDescriptors() ) {
        if( propertyMap.containsKey(p.getName())) continue;

        if( aggregate )
            inheritAggregateProperty(propertyMap, p, (String)entityName);
        else
            propertyMap.put( p.getName(), p );
    }


 }

 /**
  *
  * This method is useful to join properties between bean infos.
  *
  * The field properties belonging to aggregates are transformed in PropertyJoin automatically
  *
  * It is very useful for create aggregate Bean Info
  *
  * <pre>
    final BeanInfo aggregate = new AggregateBeanInfo();

    @Override
    public BeanInfo[] getAdditionalBeanInfo() {
        return new BeanInfo[] { aggregate };
    }

    @Override
    public PropertyDescriptor[] getPropertyDescriptors() {

        return BeanManagerUtils.aggregateProperties( getBeanClass(), super.getPropertyDescriptors(), aggregate );

   }
  * </pre>
  *
  *
  * @param beanClass class of the Owner Bean
  * @param properties properties of the Owner BeanInfo
  * @param aggregate BeanInfo array
  * @return
  */
 public static PropertyDescriptor[] aggregateProperties( Class<?> beanClass, PropertyDescriptor[] properties,  BeanInfo...aggregate )
 {

    java.util.Map<String,PropertyDescriptor> result = new java.util.LinkedHashMap<String,PropertyDescriptor>();

    for( PropertyDescriptor p: properties ) {
     result.put( p.getName(), p );
    }

    BeanInfo[] add = aggregate;

    if( add!=null && add.length>0 ) {

     for ( BeanInfo bi: add ) {

         if( !(bi instanceof ManagedBeanInfo) ) throw new IllegalArgumentException( "aggregateProperties method is suitable only for ManagedBeanInfo!");

        ((ManagedBeanInfo)bi).setBeanClass( beanClass );
        
        aggregateProperties(result, bi);
     }
    }

    PropertyDescriptor[] propArray = new PropertyDescriptor[ result.size() ];
    result.values().toArray( propArray );

    return propArray;
 }

 /**
  * Utility function that generate IN clause parameters - i.e.  (?,?,?, .... )
  *
  * Usage.:
  *
  *  Collection<String> params = Arrays.AsList( {"P1", "P2", "P3" } );
  *
  *  manager.find( conn, result, String.format( "${field} IN %s ", IN(params) ), params );
  *
  * @param parameters parameters collection
  * @return IN parameters
  */
 public static String IN( Collection<?> parameters ) {
     if( parameters==null ) throw new IllegalArgumentException("argument parameters is null!");
     if( parameters.isEmpty() ) throw new IllegalArgumentException("argument parameters is empty!");
     StringBuilder sb = new StringBuilder(100);
     sb.append('(');
     sb.append( '?');
     for( int i=1 ; i<parameters.size(); ++i ) {
        sb.append(',').append('?');
     }
     sb.append(')');
     return sb.toString();
 }
}
