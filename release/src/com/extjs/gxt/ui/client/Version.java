/*
 * Ext GWT 2.2.0 - Ext for GWT
 * Copyright(c) 2007-2010, Ext JS, LLC.
 * licensing@extjs.com
 * 
 * http://extjs.com/license
 */
package com.extjs.gxt.ui.client;

import java.util.Date;

import com.google.gwt.i18n.client.DateTimeFormat;

/**
 * Contains the current codes release information. Use {@link GXT#getVersion()} to get
 * an instance of this class.
 */
public final class Version {

  private static final String release = "2.2.0";
  private static final int major = 2;
  private static final int minor = 2;
  private static final int revision = 0;
  private static final String buildTime = "08/31/2010 12:14";

  Version() {

  }

  /**
   * Returns the release name.
   * 
   * @return the release name
   */
  public String getRelease() {
    return release;
  }

  /**
   * Returns the major number.
   * 
   * @return the major number
   */
  public int getMajor() {
    return major;
  }

  /**
   * Returns the minor number.
   * 
   * @return the minor number
   */
  public int getMinor() {
    return minor;
  }

  /**
   * Returns the revision number.
   * 
   * @return the revision number
   */
  public int getRevision() {
    return revision;
  }
  
  /**
   * Returns the build time.
   * 
   * @return the build time
   */
  public Date getBuildTime() {
    return DateTimeFormat.getFormat("MM/dd/yyyy hh:mm").parse(buildTime);
  }

}
