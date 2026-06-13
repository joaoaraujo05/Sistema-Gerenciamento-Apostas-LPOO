package br.com.sistema.model.exceptions;

public class LimiteCadastroException extends RuntimeException {
    public LimiteCadastroException(String message) {
        super(message);
    }
}
