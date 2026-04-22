package Alura.Hackaton.SentimentAPI.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public class RegisterRequest {
    @Email
    @NotBlank
    private String email;

    @NotBlank
    private String senha;

    private String tipoUsuario;
    private String nomeEmpresa;
    private String cnpj;

    public String getEmail() { return email; }
    public String getSenha() { return senha; }
    public String getTipoUsuario() { return tipoUsuario; }
    public String getNomeEmpresa() { return nomeEmpresa; }
    public String getCnpj() { return cnpj; }

    public void setEmail(String email) { this.email = email; }
    public void setSenha(String senha) { this.senha = senha; }
    public void setTipoUsuario(String tipoUsuario) { this.tipoUsuario = tipoUsuario; }
    public void setNomeEmpresa(String nomeEmpresa) { this.nomeEmpresa = nomeEmpresa; }
    public void setCnpj(String cnpj) { this.cnpj = cnpj; }
}
