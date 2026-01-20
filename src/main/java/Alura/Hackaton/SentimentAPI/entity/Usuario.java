package Alura.Hackaton.SentimentAPI.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

@Entity
@Table(name = "usuarios")
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Email
    @NotBlank
    @Column(unique = true, nullable = false)
    private String email;

    @NotBlank
    @Column(nullable = false)
    private String senha; // hash BCrypt

    @Column(nullable = false)
    private String role; // Aqui é para identificar se vai ser usuario comum ou adm

    @Column
    private String nomeEmpresa;

    private String cnpj;

//    private String telefone;

    @Column(nullable = false)
    private String tipoUsuario;

    public Usuario() {}

    public Usuario(String email, String senha, String role, String tipoUsuario) {
        this.email = email;
        this.senha = senha;
        this.role = role;
        this.tipoUsuario = tipoUsuario;
    }

    public Usuario(String email, String senha, String role, String tipoUsuario,
                   String nomeEmpresa, String cnpj) {
        this.email = email;
        this.senha = senha;
        this.role = role;
        this.tipoUsuario = tipoUsuario;
        this.nomeEmpresa = nomeEmpresa;
        this.cnpj = cnpj;
    }

    public Long getId() { return id; }
    public String getEmail() { return email; }
    public String getSenha() { return senha; }
    public String getRole() { return role; }
    public String getTipoUsuario() { return tipoUsuario; }
    public String getNomeEmpresa() { return nomeEmpresa; }

    public String getCnpj() {
        return cnpj;
    }

    public void setId(Long id) { this.id = id; }
    public void setEmail(String email) { this.email = email; }
    public void setSenha(String senha) { this.senha = senha; }
    public void setRole(String role) { this.role = role; }
    public void setNomeEmpresa(String nomeEmpresa) { this.nomeEmpresa = nomeEmpresa; }
    public void setCnpj(String cnpj) { this.cnpj = cnpj; }
    public void setTipoUsuario(String tipoUsuario) { this.tipoUsuario = tipoUsuario; }


}
