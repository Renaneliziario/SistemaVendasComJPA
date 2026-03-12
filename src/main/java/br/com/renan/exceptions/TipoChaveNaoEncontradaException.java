package br.com.renan.exceptions;

/**
@author renan.eliziario
 */
public final class TipoChaveNaoEncontradaException extends VendasException {

	private static final long serialVersionUID = -1389494676398525746L;

	public TipoChaveNaoEncontradaException(String msg) {
        this(msg, null);
    }

    public TipoChaveNaoEncontradaException(String msg, Throwable e) {
        super(msg, e);
    }
}
