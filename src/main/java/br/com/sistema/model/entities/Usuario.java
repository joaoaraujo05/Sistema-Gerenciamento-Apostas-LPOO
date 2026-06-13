package br.com.sistema.model.entities;

import jakarta.persistence.*;

import java.io.Serializable;

@Entity
@Table(name = "usuarios")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "perfil", discriminatorType = DiscriminatorType.STRING)
public abstract class Usuario implements Serializable {

    private static final long serialVersion = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String nome;

    @Column(nullable = false, unique = true, length = 50)
    private String login;

    @Column(nullable = false)
    private String senha;

    public Usuario(){}

    public Usuario(Long id, String nome, String login, String senha) {
        this.id = id;
        this.nome = nome;
        this.login = login;
        this.senha = senha;
    }

    public abstract boolean autenticar(String login, String senha);
    public abstract String obterPerfil();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getSenha() {
        return senha;
    }

    public void setSenha(String novaSenha) {
        if (novaSenha == null || novaSenha.length() < 4) throw new IllegalArgumentException("A senha tem que possuir 4 caracteres");
        this.senha = novaSenha;
    }
}
