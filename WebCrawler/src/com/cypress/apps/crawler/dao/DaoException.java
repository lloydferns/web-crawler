/**
 * 
 */
package com.cypress.apps.crawler.dao;

/**
 * This class represents a generic DaoException exception. It should wrap any exception of the underlying
 * code, such as SQLExceptions.
 *
 * @author BalusC
 * @link http://balusc.blogspot.com/2008/07/dao-tutorial-data-layer.html
 */
public class DaoException extends RuntimeException {

  private static final long serialVersionUID = 1L;

  /**
   * Constructs a DaoException with the given detail message.
   * @param message The detail message of the DaoException.
   */
  public DaoException(String message) {
    super(message);
  }

  /**
   * Constructs a DaoException with the given root cause.
   * @param cause The root cause of the DaoException.
   */
  public DaoException(Throwable cause) {
    super(cause);
  }

  /**
   * Constructs a DaoException with the given detail message and root cause.
   * @param message The detail message of the DaoException.
   * @param cause The root cause of the DaoException.
   */
  public DaoException(String message, Throwable cause) {
    super(message, cause);
  }

}