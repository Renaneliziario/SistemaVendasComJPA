/**
 * 
 */
package br.com.renan.exceptions;

/**
@author renan.eliziario
 *
 */
public final class TipoElementoNaoConhecidoException extends VendasException {

	private static final long serialVersionUID = -2268140970978666251L;

	public TipoElementoNaoConhecidoException(String msg) {
        this(msg, null);
    }

    public TipoElementoNaoConhecidoException(String msg, Throwable e) {
        super(msg, e);
    }
}
