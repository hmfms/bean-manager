package org.bsc.bean.jpa;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
public class MyBean extends java.util.HashMap<String,Object> {


	public String getContact() {
		return (String) get("contact");
	}

	public void setContact(String contact) {
		put("contact", contact);
	}

	@Id
	public String getId() {
		return (String) get("id");
	}

	public void setId(String id) {
		put("id", id);
	}

	public String getNumber() {
		return (String) get("number");
	}

	public void setNumber(String number) {
		put("number", number);
	}

	@Temporal(TemporalType.DATE)
	public java.util.Date getBirthDate() {
		return (Date) get("bd");
	}

	public void setBirthDate(java.util.Date birthDate) {
		put( "bd", birthDate );
	}
	
	

}
