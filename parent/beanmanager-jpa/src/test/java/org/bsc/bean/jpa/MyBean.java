package org.bsc.bean.jpa;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class MyBean extends java.util.HashMap<String,String> {

	public String getContact() {
		return get("contact");
	}

	public void setContact(String contact) {
		put("contact", contact);
	}

	@Id
	public String getId() {
		return get("id");
	}

	public void setId(String id) {
		put("id", id);
	}

	public String getNumber() {
		return get("number");
	}

	public void setNumber(String number) {
		put("number", number);
	}
	

}
