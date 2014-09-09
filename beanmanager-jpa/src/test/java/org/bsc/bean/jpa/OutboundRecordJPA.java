package org.bsc.bean.jpa;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
public class OutboundRecordJPA {
	
	private int record_id;
	private int chain_id;
	private int chain_n = 0;
	private java.util.Date date_ins;
	//private Date date_inoltro;
	private String contact_info;
	private String sms_flag;	
	
	@Id
	public int getRecord_id() {
		return record_id;
	}
	public void setRecord_id(int recordId) {
		record_id = recordId;
	}
	public int getChain_id() {
		return chain_id;
	}
	public void setChain_id(int chainId) {
		chain_id = chainId;
	}
	public int getChain_n() {
		return chain_n;
	}
	public void setChain_n(int chainN) {
		chain_n = chainN;
	}
        @Temporal(TemporalType.TIMESTAMP)
	public java.util.Date getDate_ins() {
		return date_ins;
	}
	public void setDate_ins(java.util.Date dateIns) {
		date_ins = dateIns;
	}
	/*
	public Date getDate_inoltro() {
		return date_inoltro;
	}
	public void setDate_inoltro(Date dateInoltro) {
		date_inoltro = dateInoltro;
	}
	*/
	public String getContact_info() {
		return contact_info;
	}
	public void setContact_info(String contactInfo) {
		contact_info = contactInfo;
	}
	public String getSms_flag() {
		return sms_flag;
	}
	public void setSms_flag(String smsFlag) {
		sms_flag = smsFlag;
	}
	
	
	
}
