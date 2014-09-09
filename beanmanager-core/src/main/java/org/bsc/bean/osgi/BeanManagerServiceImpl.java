package org.bsc.bean.osgi;

import java.beans.BeanInfo;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.text.MessageFormat;
import java.util.Collection;
import java.util.Dictionary;
import java.util.Hashtable;

import org.bsc.bean.BeanManager;
import org.bsc.bean.BeanManagerFactory;
import org.bsc.bean.metadata.ColumnBean;
import org.bsc.bean.metadata.ColumnBeanInfo;
import org.bsc.bean.metadata.TableBean;
import org.bsc.bean.metadata.TableBeanInfo;
import org.bsc.util.Log;
import org.osgi.framework.BundleContext;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;

/**
 * 
 * @author Sorrentino
 *
 */
public class BeanManagerServiceImpl implements BeanManagerService {

    BeanManagerFactory factory = BeanManagerFactory.getFactory();
    
    BeanManager<TableBean> tableBeanManager = null;
    BeanManager<ColumnBean> colBeanManager = null;
    
    java.lang.ref.Reference<BundleContext> contextRef;
    
    /**
     * 
     */
	public BeanManagerServiceImpl( BundleContext context ) {
		contextRef = new java.lang.ref.WeakReference<BundleContext>(context);
	}

	/**
	 * 
	 */
	public BeanManager<?> registerBeanManager(Class<?> beanClass, BeanInfo beanInfo) {
            
            BeanManager<?> beanManager = factory.createBeanManager(beanClass, beanInfo);
		
		BundleContext context = contextRef.get();
		
		if( null!=context ) {
			Dictionary<String,String> props = new Hashtable<String,String>(1);
			props.put("entity", beanManager.getBeanDescriptor().getEntityName());
			context.registerService(BeanManager.class.getName(), beanManager, props);
		}
		return beanManager;
	}
        
        /**
         * 
         * @param beanClass
         * @param beanInfo
         * @param key
         * @return
         */
	public BeanManager<?> registerBeanManagerEx( Class<?> beanClass, BeanInfo beanInfo, String key) {
		if( null==key ) throw new IllegalArgumentException( "key is null!");
                
                BeanManager<?> beanManager = factory.createBeanManager(beanClass, beanInfo);
		
		BundleContext context = contextRef.get();
		
		if( null!=context ) {
			Dictionary<String,String> props = new Hashtable<String,String>(1);
			props.put("entity", key);
			context.registerService(BeanManager.class.getName(), beanManager, props);
		}
		return beanManager;
	}

        /**
         * 
         * @param beanClass
         * @param key
         * @return
         */
        public BeanManager<?> lookupBeanManager(Class<?> beanClass, String key)  {
		
                BeanManager<?> result = null;
                
                BundleContext context = contextRef.get();
                
                if( null!=context ) {

                    try {
    
                        String filter = MessageFormat.format("(entity={0})", key);

                        ServiceReference[] entityRefs = context.getServiceReferences(BeanManager.class.getName(), filter);

                        if( null!=entityRefs && entityRefs.length>0 ) {
                            result = (BeanManager<?>) context.getService(entityRefs[0]);
                        }
                    
                    } catch (InvalidSyntaxException ex) {
                        Log.warn( "lookupBeanManager error ", ex );
                    }
			                    
                }
                    
                return result;
        }

        
	/**
	 * 
	 */
	@SuppressWarnings("unchecked")
	public Collection<TableBean> getTables(	DatabaseMetaData dbmd, 
										String catalog, 
										String schemaPattern, 
										String tableNamePattern, 
										String[] types,
										Collection<TableBean> result,
										Class<? extends TableBean> clazz) throws Exception {
       
       if( null==result ) throw new IllegalArgumentException( "result list is null!");

       ResultSet rs = dbmd.getTables(  catalog, schemaPattern, tableNamePattern, types );
      
       if( null==tableBeanManager ) {
    	   tableBeanManager = (BeanManager<TableBean>) factory.createBeanManager(clazz, new TableBeanInfo());
       }
       
       while( rs.next() ) {

           TableBean bean = tableBeanManager.instantiateBean();

           tableBeanManager.setBeanProperties(bean, rs);

           result.add(bean);
       }

       return result;
	}

	@SuppressWarnings("unchecked")
	public Collection<ColumnBean> getColumns(	DatabaseMetaData dbmd, 
												TableBean table, 
												Collection<ColumnBean> result, 
												Class<? extends ColumnBean> clazz) throws Exception {
	       if( null==result ) throw new IllegalArgumentException( "result list is null!");

	       String catalog = null;
	       
	       ResultSet rs = dbmd.getColumns(  catalog, table.getSchema(), table.getName(), "%" );
	      
	       if( null==colBeanManager ) {
	    	   colBeanManager = (BeanManager<ColumnBean>) factory.createBeanManager(ColumnBean.class, new ColumnBeanInfo());
	       }
	       
	       while( rs.next() ) {

	           ColumnBean bean = colBeanManager.instantiateBean();

	           colBeanManager.setBeanProperties(bean, rs);

	           result.add( bean );
	       }

	       return result;
	}


}
