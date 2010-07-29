package org.bsc.bean.jpa;

import java.io.Serializable;


import com.extjs.gxt.ui.client.data.BaseModelData;

public class ItemMasterBaseBean implements Serializable {

	protected BaseModelData m ;


	public ItemMasterBaseBean() {
		m = new BaseModelData();
	}
	
	public ItemMasterBaseBean( BaseModelData model ) {
		setM( model );
	}

	public final BaseModelData getM() {
		return m;
	}

	public final void setM(BaseModelData model) {
		if( model == null ) throw new IllegalArgumentException( "model is null!");
		this.m = model;
	}
	
	public static java.util.List<BaseModelData> toModel( java.util.Collection< ? extends ItemMasterBaseBean> source ) {
		
		java.util.List<BaseModelData> result = new java.util.ArrayList<BaseModelData>( source.size() );
		
		for( ItemMasterBaseBean b : source ) {
			
			result.add( b.getM() );
		}
		
		return result;
	}
	


}
