package org.bsc.bean.ddl;

import java.beans.BeanDescriptor;
import java.beans.BeanInfo;
import java.beans.PropertyDescriptor;

import org.apache.ddlutils.Platform;
import org.apache.ddlutils.PlatformFactory;
import org.apache.ddlutils.model.Column;
import org.apache.ddlutils.model.Database;
import org.apache.ddlutils.model.Index;
import org.apache.ddlutils.model.IndexColumn;
import org.apache.ddlutils.model.Table;
import org.apache.ddlutils.model.UniqueIndex;
import org.bsc.bean.BeanDescriptorUtils;
import org.bsc.bean.PropertyDescriptorField;
import org.bsc.bean.PropertyDescriptorJoin;
import org.bsc.bean.PropertyDescriptorPK;


public class DDLUtil {
	/**
	 * 
	 * @param pd
	 * @return
	 */
	public static Column fromPropertyDescriptorToColumn(PropertyDescriptor pd) {

		Column result = null;

		if (pd instanceof PropertyDescriptorField) {

			if (pd instanceof PropertyDescriptorJoin)
				return result;

			PropertyDescriptorField f = (PropertyDescriptorField) pd;

			result = new Column();

			result.setName(f.getFieldName());
			if (f.getSize() > 0)
				result.setSizeAndScale(f.getSize(), 0);
			result.setTypeCode(f.getSQLType());
			result.setRequired(f.isRequired());
			result.setPrimaryKey(f instanceof PropertyDescriptorPK);

		}

		return result;
	}

	/**
	 * Create a Table definition from bean info
	 * 
	 * @param info
	 * @return Table definition
	 */
	public static Table fromBeanInfoToTable(BeanInfo info) {

		Table t = new Table();

		Index primaryIndex = new UniqueIndex();

		BeanDescriptor bd = info.getBeanDescriptor();

		String name = BeanDescriptorUtils.getEntityName(bd);

		if (null == name)
			throw new IllegalArgumentException(
					"info is not a vaid beanManager beanInfo!");

		t.setName(name);
		primaryIndex.setName(name.concat("_pidx"));

		for (PropertyDescriptor pd : info.getPropertyDescriptors()) {

			Column c = fromPropertyDescriptorToColumn(pd);

			if (null != c) {
				if (c.isPrimaryKey()) {
					primaryIndex.addColumn(new IndexColumn(c));
				}
				t.addColumn(c);
			}
		}

		t.addIndex(primaryIndex);

		return t;
	}

	/**
	 *
	 *
	 */
	public static void createTablesModel(Database db, BeanInfo... infos) {

		for (BeanInfo info : infos) {

			db.addTable(fromBeanInfoToTable(info));

		}
	}

	/**
	 * 
	 * @param db
	 * @param Driver
	 * @param connectionUrl
	 * @return
	 */
	public static String generateSQL(Database db, String Driver,
			String connectionUrl) {

		Platform platform = PlatformFactory.createNewPlatformInstance(
				Driver.trim(), connectionUrl);

		return platform.getCreateTablesSql(db, true /* drop table first */,
				true /* continue on error */);

		//
		// ADD INITIALIZATION DATA TO SQL
		//
		/*
		 * SqlDynaBean bean = new SqlDynaBean( SqlDynaClass.newInstance(
		 * db.findTable("AUDIT_FILTERS")));
		 * 
		 * for( ConfigObject o : getInitData() ) {
		 * 
		 * BeanUtils.copyProperties(bean, o);
		 * 
		 * bean.set( "selected", (o.isSelected()) ? 'Y' : 'N' ); bean.set(
		 * "processed", (o.isProcessed()) ? 'Y' : 'N' ); System.out.println(
		 * platform.getInsertSql(db, bean) );
		 * 
		 * } }
		 */
	}

}
