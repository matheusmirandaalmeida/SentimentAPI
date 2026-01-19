package Alura.Hackaton.SentimentAPI.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public class RegisterRequest {

    @Email
    @NotBlank
    private String email;

    @NotBlank
    private String senha;

    @NotBlank
    @Pattern(regexp = "^(INDIVIDUAL|EMPRESA)$", message = "Tipo de usuário deve ser INDIVIDUAL ou EMPRESA")
    private String tipoUsuario;

    private String nomeEmpresa;

    @Pattern(regexp = "\\d{14}|\\d{18}", message = "CNPJ deve ter 14 ou 18 dígitos")
    private String cnpj;

    // Getters e Setters
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getSenha() { return senha; }
    public void setSenha(String senha) { this.senha = senha; }

    public String getTipoUsuario() { return tipoUsuario; }
    public void setTipoUsuario(String tipoUsuario) { this.tipoUsuario = tipoUsuario; }

    public String getNomeEmpresa() { return nomeEmpresa; }
    public void setNomeEmpresa(String nomeEmpresa) { this.nomeEmpresa = nomeEmpresa; }

    public String getCnpj() { return cnpj; }
    public void setCnpj(String cnpj) { this.cnpj = cnpj; }

}