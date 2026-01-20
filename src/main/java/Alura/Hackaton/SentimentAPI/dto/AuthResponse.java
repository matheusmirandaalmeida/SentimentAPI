package Alura.Hackaton.SentimentAPI.dto;

public class AuthResponse {
    private String token;
    private String role;
    private String tipoUsuario;
    private String nomeEmpresa;

    public AuthResponse(String token, String role, String tipoUsuario, String nomeEmpresa) {
        this.token = token;
        this.role = role;
        this.tipoUsuario = tipoUsuario;
        this.nomeEmpresa = nomeEmpresa;
    }

    public String getTipoUsuario() {
        return tipoUsuario;
    }

    public void setTipoUsuario(String tipoUsuario) {
        this.tipoUsuario = tipoUsuario;
    }

    public String getNomeEmpresa() {
        return nomeEmpresa;
    }

    public void setNomeEmpresa(String nomeEmpresa) {
        this.nomeEmpresa = nomeEmpresa;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}
