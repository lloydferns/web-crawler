/**
 * Copyright (c) 2012 by Cypress Semiconductor
 *
 * DESCRIPTION: This class provides a common method for getting a connection.
 *   The DB pool is managed in Tomcat using DBCP along with some utility functions for
 *   working with JDBC
 * 
 * REVISION HISTORY:
 *  2012-06-28  CFC adapted from com.cypress.utils.core.controller.filter.DBConnectionFilter
 *              and http://balusc.blogspot.com/2008/07/dao-tutorial-data-layer.html
 */

package com.cypress.apps.crawler.dao;

import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.Timestamp;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.sql.DataSource;

import org.apache.log4j.Logger;

import com.cypress.utils.core.util.sql.DbConnection;

/**
 * @author cfc
 *
 */
public abstract class JdbcBaseDao {

  private static final Logger LOG = Logger.getLogger(JdbcBaseDao.class);


  public final Connection getConnection() throws DaoException {

    Connection conn = null;
    try {
      //conn = DbConnection.getConnection("abcd");
      String user = "hackuser";
      String password = "hackpass";
      String url = "jdbc:mysql://csj-indc101.mis.cypress.com:3306/hackdatabase";
      
      Class.forName("com.mysql.jdbc.Driver");
      conn = DriverManager.getConnection(url, user, password);

    } catch (Exception e) {
      throw new DaoException("Error getting 'abcd---------------->>>' connection defined in server.properties file.", e);
    }
    return conn;

    /*
    return getDBCPConnectionDbcp("jdbc/npdisdb");
    return getDBCPConnectionDbcp("jdbc/dwdatadb");
    */

    // If the Cypress Tomcat DBConnectionFilter is being used,
    // com.cypress.utils.core.controller.filter.DBConnectionFilter, 
    // then the dbConnection will be saved as a request attribute
    // return (Connection) FacesUtils.getRequestAttribute("dbConnection");

  }

  /**
   * Method declaration
   * 
   * Gets a database connection from the DBCP.
   * 
   * Note: the DBCP is configured to remove abandoned connections after 60
   * seconds of idle time. All connection resources including statements and
   * results will be released. If the connection resources are released by
   * timer, the resources and a stack trace will be logged to the catalina.out
   * file.
   * 
   * @param dataSourceName name of the datasource to ask Tomcat for the resource should
   *   be configured in the tomcat server's context.xml file as an 
   *   oracle.jdbc.driver.OracleDriver resource
   * @return Connection
   * 
   */
  private static final Connection getConnectionDbcp(final String dataSourceName) throws DaoException {
    Connection conn = null;

    try {
      final Context initContext = new InitialContext();
      final Context envContext = (Context) initContext.lookup("java:/comp/env");
      final DataSource dataSrc = (DataSource) envContext.lookup(dataSourceName);

      conn = dataSrc.getConnection();
      conn.setAutoCommit(true);
    } catch (Exception e) {
      LOG.error("A database connection can not be obtained", e);
      throw new DaoException(e);
    }

    return conn;
  }

  //  Statement.RETURN_GENERATED_KEYS NOT FULLY SUPPORTED BY ORACLE JDBC DRIVER
  //
  //  /**
  //   * Returns a PreparedStatement of the given connection, set with the given SQL query and the
  //   * given parameter values.
  //   * @param connection The Connection to create the PreparedStatement from.
  //   * @param sql The SQL query to construct the PreparedStatement with.
  //   * @param returnGeneratedKeys Set whether to return generated keys or not.
  //   * @param values The parameter values to be set in the created PreparedStatement.
  //   * @throws SQLException If something fails during creating the PreparedStatement.
  //   */
  //  public static final PreparedStatement prepareStatement(Connection conn, String sql, boolean returnGeneratedKeys, Object... values) throws SQLException {
  //    PreparedStatement pStmt = conn.prepareStatement(sql, returnGeneratedKeys ? Statement.RETURN_GENERATED_KEYS
  //        : Statement.NO_GENERATED_KEYS);
  //    setValues(pStmt, values);
  //    return pStmt;
  //  }

  /** 
   * Returns a PreparedStatement of the given connection, set with the given SQL query and the
   *   given parameter values.
   * @param connection The Connection to create the PreparedStatement from.
   * @param sql The SQL query to construct the PreparedStatement with.
   * @param values The parameter values to be set in the created PreparedStatement.
   * @throws SQLException If something fails during creating the PreparedStatement.
   */
  protected static final PreparedStatement prepareStatement(final Connection conn, final String sql, final Object... values) throws SQLException {
    final PreparedStatement pStmt = conn.prepareStatement(sql);
    setValues(pStmt, values);
    return pStmt;
  }

  /**
   * Set the given parameter values in the given PreparedStatement.
   * @param connection The PreparedStatement to set the given parameter values in.
   * @param values The parameter values to be set in the created PreparedStatement.
   * @throws SQLException If something fails during setting the PreparedStatement values.
   */
  protected static final void setValues(final PreparedStatement pStmt, final Object... values) throws SQLException {
    for (int i = 0; i < values.length; i++) {
      pStmt.setObject(i + 1, values[i]);
    }
  }

  /**
   * Converts the given java.util.Date to java.sql.Date.
   * @param date The java.util.Date to be converted to java.sql.Date.
   * @return The converted java.sql.Date.
   */
  protected static final Date toSqlDate(final java.util.Date date) {
    return (date != null) ? new Date(date.getTime()) : null;
  }

  /**
   * Converts the given java.util.Date to java.sql.Timestamp.
   * @param date The java.util.Date to be converted to java.sql.Timestamp.
   * @return The converted java.sql.Date.
   */
  protected static final Timestamp toSqlTimestamp(final java.util.Date date) {
    return (date != null) ? new Timestamp(date.getTime()) : null;
  }

  /**
   * Quietly close the Connection.
   * @param conn The Connection to be closed quietly.
   */
  protected static final void close(final Connection conn) {
    if (conn != null) {
      try {
        conn.close();
      } catch (SQLException e) {
        LOG.warn("Closing connection failed:", e);
      }
    }
  }

  /**
   * Quietly close the Statement.
   * @param statement The Statement to be closed quietly.
   */
  protected static final void close(final Statement stmt) {
    if (stmt != null) {
      try {
        stmt.close();
      } catch (SQLException e) {
        LOG.warn("Closing statement failed:", e);
      }
    }
  }

  /**
   * Quietly close the ResultSet.
   * @param rSet The ResultSet to be closed quietly.
   */
  protected static final void close(final ResultSet rSet) {
    if (rSet != null) {
      try {
        rSet.close();
      } catch (SQLException e) {
        LOG.warn("Closing resultset failed:", e);
      }
    }
  }

  /**
   * Quietly close the Connection and Statement. 
   * @param conn The Connection to be closed quietly.
   * @param stmt The Statement to be closed quietly.
   */
  protected static final void close(final Connection conn, final Statement stmt) {
    close(stmt);
    close(conn);
  }

  /**
   * Quietly close the Connection, Statement and ResultSet. 
   * @param conn The Connection to be closed quietly.
   * @param stmt The Statement to be closed quietly.
   * @param rSet The ResultSet to be closed quietly.
   */
  protected static final void close(final Connection conn, final Statement stmt, final ResultSet rSet) {
    close(rSet);
    close(stmt);
    close(conn);
  }

  /**
   * 
   * @param conn  Note that it is the responsibility of the calling method to close this connection.
   * @param oracleSequence 
   * @return the next value for the oracle sequence 
   * @throws SQLException
   */
  protected static final Integer getNextKey(final Connection conn, final String oracleSequence) throws SQLException {
    PreparedStatement pStmt = null;
    ResultSet rSet = null;
    Integer key = null;

    try {
      final String sql = "SELECT " + oracleSequence + ".nextval FROM dual";
      pStmt = prepareStatement(conn, sql);

      rSet = pStmt.executeQuery();
      if (rSet.next()) {
        key = Integer.valueOf(rSet.getInt(1));
      }
    } finally {
      // intentionally leaving connection open, responsibility to close left to calling method
      close(pStmt);
      close(rSet);
    }

    return key;
  }
  
  /**
   * 
   * @param conn  Note that it is the responsibility of the calling method to close this connection.
   * @param table 
   * @return the # of rows in the given table
   * @throws SQLException
   */
  protected static final Integer getRowCount(final Connection conn, final String table) throws SQLException {
    PreparedStatement pStmt = null;
    ResultSet rSet = null;
    Integer numRows = null;

    try {
      final String sql = "SELECT COUNT(*) FROM " + table;
      pStmt = prepareStatement(conn, sql);

      rSet = pStmt.executeQuery();
      if (rSet.next()) {
        numRows = Integer.valueOf(rSet.getInt(1));
      }
    } finally {
      // intentionally leaving connection open, responsibility to close left to calling method
      close(pStmt);
      close(rSet);
    }
    
    LOG.debug("JdbcBaseDao.getRowCount returned " + numRows + " for " + table + ".");

    return numRows;
  }

  /**
   * @param numQs
   * @return a string of numQ question marks separated by commas for use in generating prepared
   *         statements containing "IN"clauses
   */
  protected static final String generateQsForIn(int numQs) {
    String items = "";
    for (int i = 0; i < numQs; i++) {
      if (i != 0)
        items += ", ";
      items += "?";
    }
    return items;
  }

}
