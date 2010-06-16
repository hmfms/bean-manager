/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.bsc.bean.jpa;

import java.awt.Image;
import java.beans.BeanDescriptor;
import java.beans.BeanInfo;
import java.beans.EventSetDescriptor;
import java.beans.IntrospectionException;
import java.beans.MethodDescriptor;
import java.beans.PropertyDescriptor;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.LinkedList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.MappedSuperclass;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.bsc.bean.BeanDescriptorEntity;
import org.bsc.bean.BeanManagerUtils;
import org.bsc.bean.ManagedBeanInfo;
import org.bsc.bean.PropertyDescriptorField;
import org.bsc.bean.PropertyDescriptorJoin;
import org.bsc.bean.PropertyDescriptorPK;
import org.bsc.bean.generators.UUIDValueGenerator;
import org.bsc.util.Log;

/**
 *
 * @author bsorrentino
 */
public class JPAManagedBeanInfo<T> implements  ManagedBeanInfo<T> {

    public static class RelationBeanFactory {

        final Class<?> beanClass;

        final Method setter;

        RelationBeanFactory( Class<?> beanClass, Method setter ) {
            if( null==beanClass ) throw new IllegalArgumentException("beanClass param is null!");
            if( null==setter ) throw new IllegalArgumentException("setter param is null!");
            this.beanClass = beanClass;
            this.setter = setter;
        }

        public void createAndSet( Object bean ) throws Exception {

            Log.debug( "TRY TO CREATE BEANCLASS [{0}]", beanClass.getName());

            Object o = beanClass.newInstance();

            Log.debug( "INVOKING SETTER [{0}]", setter.toString());

            setter.invoke(bean, o);
        }

    }

    protected final static String getTableName( Class<?> beanClass, String defValue ) {
    	if( beanClass == null && defValue == null ) throw new IllegalArgumentException( "both parameters 'beanClass' and  parameter 'defValue' are null!");
    	
    	Table annotation = beanClass.getAnnotation( Table.class );
    	
    	String tableName = ( annotation != null ) ? annotation.name() : defValue;
    	
    	if( tableName==null || tableName.isEmpty() ) throw new IllegalStateException( "tableName is not defined");
    	
    	String schema = ( annotation != null ) ? annotation.schema() : null ;
    	
    	return ( schema!=null && !schema.isEmpty() ) ?
    		String.format( "%s.%s", schema, tableName ) :
    		tableName;	
    }
    
    protected static PropertyDescriptor processJoin(JPAManagedBeanInfo<?> result, AnnotatedElement f, OneToOne relation, PropertyDescriptor pd) throws Exception {

        if( relation==null || pd==null ) return null;

        JoinColumn jc = f.getAnnotation( JoinColumn.class );
        if( jc==null ) throw new IllegalStateException( "JoinColumn Info is not set!");

        String jtable = null;
        
        Class<?> type = pd.getPropertyType();
        
        if( result.relationBeanList==null) result.relationBeanList = new java.util.ArrayList<RelationBeanFactory>(5);
        result.relationBeanList.add( new RelationBeanFactory( type, pd.getWriteMethod() ));


        String fieldA = jc.name();
        String fieldB = jc.referencedColumnName();
        
        // GET JOIN TBLE NAME
/*
        Table joinTable = type.getAnnotation(Table.class);
        if( joinTable!=null) {
        	jtable = joinTable.name();
        }
        else {	
        	jtable = (jc.table()==null || jc.table().isEmpty()) ? type.getSimpleName() : jc.table();
        }
*/        
        jtable = getTableName( type, (jc.table()==null || jc.table().isEmpty()) ? type.getSimpleName() : jc.table() );
        
        PropertyDescriptorField pf = new PropertyDescriptorField( pd );
        pf.setFieldName(fieldA);
        pf.setValue("relation.attribute", fieldB.toLowerCase());

        BeanDescriptorEntity bde = (BeanDescriptorEntity) result.getBeanDescriptor();

        bde.createJoinRelation(jtable, new org.bsc.bean.JoinCondition(fieldA, fieldB));

        JPAManagedBeanInfo<?> beanInfo = JPAManagedBeanInfo.create(type);
        
// TODO add aggretation feature
//        result.getAdditionalList().add( beanInfo );
        
        PropertyDescriptor[] pp = beanInfo.getPropertyDescriptors();

        for( PropertyDescriptor ppd : pp ) {

            if( ppd instanceof PropertyDescriptorJoin ) continue;

            if( ppd instanceof PropertyDescriptorField ) {

                PropertyDescriptorJoin pdj = new PropertyDescriptorJoin( ppd.getName(), ppd.getReadMethod(), ppd.getWriteMethod());
                pdj.setJoinTable(jtable);

                pdj.setValue( "relation.name", pd.getName());
                pdj.setSQLType( BeanManagerUtils.getSQLType(ppd.getPropertyType()));

                result.properties.put( ppd.getName() , pdj );
            }
        }


        return pf;
    }

	protected static void processJoinFields( JPAManagedBeanInfo<?> result, String joinTableName, Class<?> superClass, PropertyDescriptor[] properties, List<String> pkList ) {
        for( PropertyDescriptor pd : properties ) {
            try {
            	if( "class".equals(pd.getName()) ) continue;
            	
            	Field f = null;
            	try {
            		f = superClass.getDeclaredField(pd.getName());
            	}
            	catch (NoSuchFieldException ex) {
                    Log.warn( "the field [{0}] doesn't exist!", pd.getName() );
            	}
            	
                Method m = pd.getReadMethod();
                
            	//
            	// TRANSIENT
            	// 
            	if( f!=null ) {
                    Transient t = f.getAnnotation( Transient.class );
                    if( t!=null ) {
                        Log.debug( "the field [{0}] is transient",  pd.getName() );

                        result.properties.put( pd.getName(), pd);
                        continue;

                    }            		
            	}
            	else {
                    Transient t = m.getAnnotation( Transient.class );
                    if( t!=null ) {
                        Log.debug( "the field [{0}] is transient",  pd.getName() );

                        result.properties.put( pd.getName(), pd);
                        continue;

                    }            		
            		
            	}
  
                PropertyDescriptorJoin pdj = new PropertyDescriptorJoin(pd);
                pdj.setJoinTable(joinTableName);
                pdj.setSQLType( BeanManagerUtils.getSQLType(pd.getPropertyType()));
                
            	Id idf = null;
            	if( f!=null ) idf = f.getAnnotation( Id.class );
            	
            	Id idm = m.getAnnotation(Id.class );
            	
                processColumn( result, f, pdj );
                processColumn( result, m, pdj );

                if( idf!=null || idm!=null  ) {
            		pkList.add( pdj.getFieldName() );
            	}


                if( !result.properties.containsKey( pd.getName() ) ) {
                	result.properties.put( pd.getName(), pdj);
                }
                else {
                	Log.debug( "property {0} is duplicated!", pd.getName());
                }


            } catch (SecurityException ex) {
                Log.error( "security issue on the field [{0}]", ex, pd.getName() );
            } catch (Exception ex) {
                Log.error( "reflection issue on the field [{0}]", ex, pd.getName() );
            }
        }
        
		
	}
    
    protected static PropertyDescriptorPK processId(JPAManagedBeanInfo<?> result, AnnotatedElement f, PropertyDescriptor pd) throws Exception {
        if(pd==null || f==null ) return null;
        
        Id id = f.getAnnotation(Id.class);
        
        if( id==null ) return null;
        
        PropertyDescriptorPK pk = null;

        if( pd instanceof PropertyDescriptorPK ) {
            pk = (PropertyDescriptorPK) pd;
        }

        if( pk==null ) pk = new PropertyDescriptorPK( pd );

        GeneratedValue gv = f.getAnnotation(GeneratedValue.class);

        if( gv != null ) {
            if( !pd.getPropertyType().equals(String.class) )
                throw new UnsupportedOperationException("the property annotated as Id must be a String type!");

            pk.setValueGenerator( new UUIDValueGenerator() );
        }
        
        return pk;

    }

    /**
     * 
     * @param result
     * @param f
     * @param annotation
     * @param pd
     * @throws Exception
     */
    protected static void processColumn( JPAManagedBeanInfo<?> result, AnnotatedElement f, PropertyDescriptorField pd) throws Exception {

        if( pd==null || f==null ) return;

        Column annotation = f.getAnnotation(Column.class);
        
        if( annotation==null ) return;
        //boolean unique = annotation.unique();

        if( !annotation.insertable() && !annotation.updatable() ) pd.setReadOnly(true);

        int len = annotation.length();
        if( len > 0 ) pd.setSize(len);

        String name = annotation.name();
        if( name!=null ) pd.setFieldName(name);

        boolean nullable = annotation.nullable();
        if( !nullable ) pd.setRequired(true);

        //String table = annotation.table();

        //int precision = annotation.precision();
        //int scale = annotation.scale();
        

    }

    /**
     * 
     * @param <T>
     * @param result
     * @param beanClass
     * @param properties
     * @throws IntrospectionException
     */
    private static <T> void processFields( JPAManagedBeanInfo<T> result, Class<T> beanClass,  PropertyDescriptor[] properties, List<PropertyDescriptorPK> pk )  {

        for( PropertyDescriptor pd : properties ) {
            try {
            	
            	if( "class".equals(pd.getName()) ) continue;
            	
            	Field f = null;
            	try {
            		f = beanClass.getDeclaredField(pd.getName());
            	}
            	catch (NoSuchFieldException ex) {
                    Log.warn( "the field [{0}] doesn't exist!", pd.getName() );
            	}
                
            	Method m = pd.getReadMethod();
                
            	//
            	// TRANSIENT
            	// 
            	if( f!=null ) {
                    Transient t = f.getAnnotation( Transient.class );
                    if( t!=null ) {
                        Log.debug( "the field [{0}] is transient",  pd.getName() );

                        result.properties.put( pd.getName(), pd);
                        continue;

                    }            		
            	}
            	else {
                    Transient t = m.getAnnotation( Transient.class );
                    if( t!=null ) {
                        Log.debug( "the field [{0}] is transient",  pd.getName() );

                        result.properties.put( pd.getName(), pd);
                        continue;

                    }            		
            		
            	}
            	
            	//
            	//	JOIN
            	//

            	if( f!=null ) {
                    PropertyDescriptor ppd = processJoin( result, f, f.getAnnotation(OneToOne.class), pd);
                    if( ppd!=null ) {

                        result.properties.put( pd.getName(), ppd);
                        continue;
                    }
                }

            	//
            	// ID
            	//
                { 
                    PropertyDescriptorPK pf = processId(result, f , pd );                    	
                    
                    if( pf==null ) pf = processId(result, m, pd );
                   
                    if( pf!= null ) {

                        processColumn( result, f, pf );
                        processColumn( result, m, pf );

                        pf.setSQLType( BeanManagerUtils.getSQLType(pd.getPropertyType()));

                        result.properties.put( pd.getName(), pf);

                        pk.add(pf);
                        
                        continue;

                    }
                }

                //
                // OTHER
                //
                { 
                    PropertyDescriptorField pf = null;

                    if( pd instanceof PropertyDescriptorField ) {
                        pf = (PropertyDescriptorField) pd;
                    }

                    if( pf==null ) pf = new PropertyDescriptorField( pd );

                    processColumn( result, f, pf );
                    processColumn( result, m, pf );

                    pf.setSQLType( BeanManagerUtils.getSQLType(pd.getPropertyType()));

                    result.properties.put( pd.getName(), pf);
                }

            } catch (SecurityException ex) {
                Log.error( "security issue on the field [{0}]", ex, pd.getName() );
            } catch (Exception ex) {
                Log.error( "reflection issue on the field [{0}]", ex, pd.getName() );
            }
        }
        
    }

    public static <T> JPAManagedBeanInfo<T>  create( Class<T> beanClass ) throws IntrospectionException{

    	{
        Entity entity = beanClass.getAnnotation( Entity.class );
        if( entity == null ) throw new IllegalArgumentException( String.format("class [%s] is not an Entity!", beanClass.getName()));
    	}

    	/*
        String tableName = beanClass.getSimpleName();
        {
        Table table = beanClass.getAnnotation( Table.class );
        if( table != null ) tableName = table.name();
        }
        */
    	
    	String tableName = getTableName( beanClass, beanClass.getSimpleName() );
    	
        //
        // CHECK FOR JOINED INHERITANCE
        //
        String joinTableName = null;
        
        boolean noEntityInheritance = false;
        boolean singleTableInheritance = false;
        boolean joinedInheritance = false;
        boolean mappedSuperclass = false;
        
    	Class<?> superClass = beanClass.getSuperclass();
    	
    	if( superClass != null ) {
    	
    		Entity entity = superClass.getAnnotation( Entity.class );
    		if( entity != null ) {
    	    	
    			Inheritance inheritance = superClass.getAnnotation(Inheritance.class);
    			if( inheritance==null )  {
    				singleTableInheritance = true;
    			}
    			else {
    				if( inheritance.strategy()==InheritanceType.TABLE_PER_CLASS) throw new IllegalStateException( String.format("TABLE_PER_CLASS is not supported yet!") );

    				if( inheritance.strategy()==InheritanceType.JOINED ) {
    					joinedInheritance = true;
    					
    					/*
    					joinTableName = superClass.getSimpleName();
    					Table table = superClass.getAnnotation( Table.class );
    					if( table != null ) joinTableName = table.name();
    					*/
    					joinTableName = getTableName( superClass, superClass.getSimpleName());
    				}
    				else {
    					singleTableInheritance = true;
    				}
    			}
    		}
    		else {
    			MappedSuperclass msc = superClass.getAnnotation(MappedSuperclass.class);
    			mappedSuperclass = (msc!=null);			
    			noEntityInheritance = (msc==null);
    		}
    	}
    	
        Log.debug( "table name [{0}]", tableName);

        JPAManagedBeanInfo<T> result = new JPAManagedBeanInfo<T>(beanClass);

        BeanInfo beanInfo = (joinedInheritance || noEntityInheritance ) ? 
        						java.beans.Introspector.getBeanInfo(beanClass,superClass) :
        						java.beans.Introspector.getBeanInfo(beanClass);        
                
        if( null==beanInfo ) throw new IllegalStateException( "beanInfo not found!");

        result.beanDescriptor = new BeanDescriptorEntity( beanClass, tableName );

        PropertyDescriptor pd[] = beanInfo.getPropertyDescriptors();

        result.properties = new java.util.LinkedHashMap<String, PropertyDescriptor>( pd.length );

        List<PropertyDescriptorPK> pkList = new LinkedList<PropertyDescriptorPK>();
        
        processFields( result, beanClass, pd, pkList );
        

        if( joinedInheritance ) {
        	
        	BeanInfo joinedBeanInfo = java.beans.Introspector.getBeanInfo(superClass);

        	List<String> jpkList = new LinkedList<String>();
        	
        	processJoinFields( result, joinTableName, superClass, joinedBeanInfo.getPropertyDescriptors(), jpkList );
        	
        	if( pkList.size() != jpkList.size() ) 
        		throw new IllegalStateException( String.format(" Primary key of table [%s] doesn't match with Pimary Key of joined table [%s]!", tableName, joinTableName));

        	for( int i=0; i < pkList.size() ; ++i ) {
            	result.beanDescriptor.createJoinRelation(joinTableName, new org.bsc.bean.JoinCondition(pkList.get(i).getFieldName(), jpkList.get(i)));        		
        	}
        	
        	result.getAdditionalList().add( JPAManagedBeanInfo.create(superClass));

        }
        
    	return result;
    }
    
    

    protected Class<T> beanClass;
    protected java.util.List<RelationBeanFactory> relationBeanList;
    private java.util.List<JPAManagedBeanInfo<?>> additionalList;

    
    protected java.util.Map<String,PropertyDescriptor> properties = null;
    protected BeanDescriptorEntity beanDescriptor = null;

    protected JPAManagedBeanInfo( Class<T> beanClass ) {
        setBeanClass(beanClass);
    }

    protected java.util.List<JPAManagedBeanInfo<?>> getAdditionalList() {
        if( additionalList==null) additionalList = new java.util.ArrayList<JPAManagedBeanInfo<?>>(5);
    	
        return additionalList;
    }
    
    protected <F extends PropertyDescriptorField> F getPropertyField( String name ) {
        return (F)properties.get(name);
    }

    public Class<T> getBeanClass() {
       return beanClass;
    }

    public void setBeanClass(Class<? extends T> type) {
        beanClass = (Class<T>) type;
    }

    public BeanDescriptor getBeanDescriptor() {
        return beanDescriptor;
    }

    public PropertyDescriptor[] getPropertyDescriptors() {
        return properties.values().toArray( new PropertyDescriptor[ properties.size() ]);
    }

    public EventSetDescriptor[] getEventSetDescriptors() {
        return new EventSetDescriptor[0];
    }

    public int getDefaultEventIndex() {
        return -1;
    }

    public int getDefaultPropertyIndex() {
        return -1;
    }

    public MethodDescriptor[] getMethodDescriptors() {
        return new MethodDescriptor[0];
    }

    public BeanInfo[] getAdditionalBeanInfo() {
        if( additionalList==null ) return null;
        return additionalList.toArray( new BeanInfo[ additionalList.size() ]);
    }

    public Image getIcon(int i) {
        return null;
    }


}
