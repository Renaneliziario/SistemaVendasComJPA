package br.com.renan.exceptions;

/**
 * Java 21 — Sealed Class:
 * Define um conjunto FECHADO e conhecido de subclasses de exceção.
 * O compilador garante que qualquer switch/catch sobre VendasException
 * pode ser exaustivo, sem precisar de um bloco "default" extra.
 *
 * Exemplo de uso com pattern matching:
 *   catch (VendasException ex) {
 *       String msg = switch (ex) {
 *           case DAOException e                      -> "Erro no banco: "    + e.getMessage();
 *           case TableException e                    -> "Tabela inválida: "  + e.getMessage();
 *           case TipoChaveNaoEncontradaException e   -> "Chave não existe: " + e.getMessage();
 *           case MaisDeUmRegistroException e         -> "Dado duplicado: "   + e.getMessage();
 *           case TipoElementoNaoConhecidoException e -> "Tipo inválido: "    + e.getMessage();
 *       };
 *   }
 */
public abstract sealed class VendasException extends Exception
        permits DAOException,
                TableException,
                TipoChaveNaoEncontradaException,
                MaisDeUmRegistroException,
                TipoElementoNaoConhecidoException {

    protected VendasException(String msg) {
        super(msg);
    }

    protected VendasException(String msg, Throwable cause) {
        super(msg, cause);
    }
}
