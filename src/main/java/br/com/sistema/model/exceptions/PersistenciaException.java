package br.com.sistema.model.exceptions;

public class PersistenciaException extends RuntimeException {
    public PersistenciaException(String msg) {
        super(msg);
    }
}
