package br.com.sistema.model.entities;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("ADMINISTRADOR")
public class UsuarioAdministrador extends Usuario{
    public UsuarioAdministrador() {
    }

    public UsuarioAdministrador(Long id, String nome, String login, String senha) {
        super(id, nome, login, senha);
    }

    @Override
    public boolean autenticar(String login, String senha) {
        if (this.getLogin().equals(login) && this.getSenha().equals(senha)) return true;
        return false;
    }

    @Override
    public String obterPerfil() {
        return "ADMINISTRADOR";
    }
}
