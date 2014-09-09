package org.bsc.bean.jpa;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;


@Entity
@Table( name="F00091", schema="PYDTA")
public class ItemCifF00091 extends ItemCifBase {

	@Id
	@Column(name="T2SDB")
	public String getT2SDB() { return m.get("T2SDB"); }
	public void setT2SDB(String value) { m.set("T2SDB", value); }
	
	@Column(name="T2RT")
	public String getKyUdc2() { return m.get("T2RT"); }
	public void setKyUdc2(String value) { m.set("T2RT", value); }
	
	@Column(name="T2SY1")
	public String getRmkUdc1() { return m.get("T2SY1"); }
	public void setRmkUdc1(String value) { m.set("T2SY1", value); }
	@Column(name="T2RT1")
	public String getRmkUdc2() { return m.get("T2RT1"); }
	public void setRmkUdc2(String value) { m.set("T2RT1", value); }
	
	@Column(name="T2SY2")
	public String getRmk2Udc1() { return m.get("T2SY2"); }
	public void setRmk2Udc1(String value) { m.set("T2SY2", value); }
	
	@Column(name="T2RT2")
	public String getRmk2Udc2() { return m.get("T2RT2"); }
	public void setRmk2Udc2(String value) { m.set("T2RT2", value); }
	
	@Column(name="CUEV02")
	public String getCUEV02() { return m.get("CUEV02", ""); }
	public void setCUEV02(String value) { m.set("CUEV02", value); }
	
	@Column(name="CUEV01")
	public String getCUEV01() { return m.get("CUEV01", ""); }
	public void setCUEV01(String value) { m.set("CUEV01", value); }		
	
	@Transient
	public boolean isActive(){
		return getCUEV02().equalsIgnoreCase("Y");
	}
	
	@Transient
	public boolean isHistory(){
		return getCUEV01().equalsIgnoreCase("Y");
	}
}
