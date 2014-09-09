/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.bsc.bean.jpa;

import javax.persistence.Column;
import javax.persistence.Entity;

/**
 *
 * @author softphone
 */
@Entity
public class ItemCifBase extends ItemMasterBaseBean {

    	@Column(name="T2TYDT")
	public String getCifType() { return m.get("T2TYDT"); }
	public void setCifType(String value) { m.set("T2TYDT", value); }

	@Column(name="T2SY")
	public String getKyUdc1() { return m.get("T2SY"); }
	public void setKyUdc1(String value) { m.set("T2SY", value); }

}
