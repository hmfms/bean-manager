package org.bsc.bean.adapters;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.sql.Blob;
import java.sql.SQLException;

import org.bsc.util.Log;
/**
 * Title:        Bartolomeo Sorrentino Classi
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:      ITD
 * 
 * deprecated use SerialBlob instead
 * 
 * @author Bartolomeo Sorrentino
 * @version 1.0
 */
public class PropertyBlob implements Blob {
  private byte[] content;

  /**
   *
   * @param value
   */
  public PropertyBlob( byte[] value ) {
    this.content = value;
  }

  /**
   *
   * @param is
   */
  public PropertyBlob( InputStream is ) throws java.io.IOException {
    java.io.BufferedInputStream bis = new java.io.BufferedInputStream(is, 4096);
    java.io.ByteArrayOutputStream baos = new java.io.ByteArrayOutputStream();

    byte[] buffer = new byte[4096];
    int len ;
    while( ( len = bis.read(buffer))==4096 ) {
      baos.write(buffer,0,len);
    }
    if( len>0 ){
      baos.write(buffer,0,len);
    }

    this.content = baos.toByteArray();
  }

  public long length() throws SQLException {
    return (long)content.length;
  }

  public byte[] getBytes(long pos, int length) throws SQLException {
    byte[] result = new byte[length];
    System.arraycopy(content,(int)pos,result,0,length);
    return result;
  }

  public byte[] getBytes() throws SQLException {
    return content;
  }

  public InputStream getBinaryStream() throws SQLException {
    return new ByteArrayInputStream(content);
  }

  public long position(byte[] pattern, long start) throws SQLException {
    Log.trace("PropertyBlob.position(byte[])");
    return -1;
  }

  public long position(Blob pattern, long start) throws SQLException {
    Log.trace("PropertyBlob.position(Blob)");
    return -1;
  }

  /**
   * jdk1.4 method
   */
  public void truncate( long value ) {
  }

  /**
   * jdk1.4 method
   */
  public java.io.OutputStream setBinaryStream( long value ) {
    return null;
  }

  /**
   * jdk1.4 method
   */
  public int setBytes( long p1, byte[] p2, int p3, int p4  ) {
    return -1;
  }

  /**
   * jdk1.4 method
   */
  public int setBytes( long p1, byte[] p2 ) {
    return -1;
  }

    public void free() throws SQLException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public InputStream getBinaryStream(long pos, long length) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
