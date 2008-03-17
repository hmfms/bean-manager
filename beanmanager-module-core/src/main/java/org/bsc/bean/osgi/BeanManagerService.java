package org.bsc.bean.osgi;

import java.beans.BeanInfo;
import java.sql.DatabaseMetaData;
import java.util.Collection;

import org.bsc.bean.BeanManager;
import org.bsc.bean.metadata.ColumnBean;
import org.bsc.bean.metadata.TableBean;

/**
 * 
 * @author Sorrentino
 *
 */
public interface BeanManagerService {

	/**
	 * 
	 * @param beanClass
	 * @param beanInfo
	 * @return
	 */
	BeanManager<?> registerBeanManager( Class<?> beanClass, BeanInfo beanInfo );
	

	/**
	 * 
	 * @param md
	 * @param catalog
	 * @param schemaPattern
	 * @param tableNamePattern
	 * @param types
	 * @param result
	 * @return
	 * @throws Exception
	 */
	Collection<TableBean> getTables(DatabaseMetaData md, 
									String catalog, 
									String schemaPattern, 
									String tableNamePattern, 
									String[] types, 
									Collection<TableBean> result, 
									Class<? extends TableBean> clazz ) throws Exception;

	/**
	 * 
	 * @param md
	 * @param catalog
	 * @param schemaPattern
	 * @param tableNamePattern
	 * @param result
	 * @return
	 */
	Collection<ColumnBean> getColumns(	DatabaseMetaData md, 
										TableBean table, 
										Collection<ColumnBean> result,
										Class<? extends ColumnBean> clazz ) throws Exception;

}
