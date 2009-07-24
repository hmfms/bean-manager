package org.bsc.beanmanager;

import org.apache.ddlutils.model.Table;

public class GenerateBean {

	private boolean selected;
	private String tableName;
	private String beanName;
	public final Table table;
	
	public GenerateBean(Table table) {
		super();
		this.table = table;
		this.tableName = table.getName();
		this.beanName = table.getName();
		this.selected = true;
	}
	
	public final boolean isSelected() {
		return selected;
	}
	public final void setSelected(boolean selected) {
		this.selected = selected;
	}
	public final String getTableName() {
		return tableName;
	}
	public final void setTableName(String tableName) {
		this.tableName = tableName;
	}
	public final String getBeanName() {
		return beanName;
	}
	public final void setBeanName(String beanName) {
		this.beanName = beanName;
	}
	
	
}
