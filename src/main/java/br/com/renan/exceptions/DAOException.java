/**
 * 
 */
package br.com.renan.exceptions;

/**
@author renan.eliziario
 *
 */
public final class DAOException extends VendasException {

	private static final long serialVersionUID = 7054379063290825137L;

	public DAOException(String msg, Exception ex) {
		super(msg, ex);
    }
}
