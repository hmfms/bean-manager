/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.bsc.bean.metadata;

/**
 *
 * @author Sorrentino
 */
public class TableBean {
    private String name;
    private String type;
    private String remarks;
    private String schema;

    /**
     * 
     */
    public TableBean() {
    }

    
    /**
     * 
     * @param schema
     * @param name
     */
    public TableBean(String schema, String name) {
        this.name = name;
        this.schema = schema;
    }

    
    public String getName() {
        return name;
    }

    public String getRemarks() {
        return remarks;
    }

    public String getType() {
        return type;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getSchema() {
        return schema;
    }

    public void setSchema(String schema) {
        this.schema = schema;
    }
    
    
}
