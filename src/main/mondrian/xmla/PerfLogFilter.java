/*
// This software is subject to the terms of the Eclipse Public License v1.0
// Agreement, available at the following URL:
// http://www.eclipse.org/legal/epl-v10.html.
// You must accept the terms of that agreement to use this software.
//
// Copyright (c) 2002-2013 Pentaho Corporation..  All rights reserved.
*/
package mondrian.xmla;

import org.apache.log4j.Logger;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

public class PerfLogFilter implements Filter {

  protected static final Logger LOGGER = Logger.getLogger(PerfLogFilter.class);
  String logMsg = "<none>";
  static long time = System.currentTimeMillis();

  public void init(FilterConfig filterConfig) throws ServletException {
    logMsg = filterConfig.getInitParameter("logMsg"); /*//$NON-NLS-1$*/
  }

  public String getLogMsg() {
    return logMsg;
  }

  public void setLogMsg(String logMsg) {
    this.logMsg = logMsg;
  }

  public void destroy() {}

  public void doFilter(
      ServletRequest request, ServletResponse response,
    FilterChain chain) throws IOException,
    ServletException
  {
    if (LOGGER.isDebugEnabled()) {
      long currTime = System.currentTimeMillis();
      LOGGER.debug(
          "PerfLogFilter: "
          + logMsg + " PRE time diff= " + (currTime - time));
      time = currTime;
      chain.doFilter(request, response);
      currTime = System.currentTimeMillis();
      LOGGER.debug(
          "PerfLogFilter: "
          + logMsg + " POST time diff= " + (currTime - time));
      time = currTime;
    } else {
      chain.doFilter(request, response);
    }
  }
}
// End PerfLogFilter.java