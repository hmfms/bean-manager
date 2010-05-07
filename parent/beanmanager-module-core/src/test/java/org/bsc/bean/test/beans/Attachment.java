package org.bsc.bean.test.beans;

import java.util.UUID;

public class Attachment {

	private String id;
	private char[] content;
	
	
	public Attachment() {
		setId( UUID.randomUUID().toString() );
	}
	/**
	 * @return the id
	 */
	public final String getId() {
		return id;
	}
	/**
	 * @param id the id to set
	 */
	public final void setId(String id) {
		this.id = id;
	}
	/**
	 * @return the content
	 */
	public final char[] getContent() {
		return content;
	}
	/**
	 * @param content the content to set
	 */
	public final void setContent(char[] content) {
		this.content = content;
	}
	
	
}
