document.addEventListener('DOMContentLoaded', function() {
    const form = document.getElementById("formLogin");
    const emailInput = document.getElementById("emailLogin");
    const senhaInput = document.getElementById("senhaLogin");
    const messageDiv = document.getElementById("message");

    form.addEventListener("submit", async (e) => {
        e.preventDefault();

        // Limpar mensagem anterior
        messageDiv.style.display = 'none';

        // Validação básica
        if (!emailInput.value.trim() || !senhaInput.value) {
            showMessage('Por favor, preencha todos os campos', 'error');
            return;
        }

        try {
            const resp = await fetch("/auth/login", {
                method: "POST",
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify({
                    email: emailInput.value.trim(),
                    senha: senhaInput.value
                })
            });

            if (!resp.ok) {
                let errorMsg = "Falha no login";
                try {
                    const errorData = await resp.json();
                    if (errorData.erro) errorMsg = errorData.erro;
                } catch {}
                showMessage(errorMsg, 'error');
                return;
            }

            const data = await resp.json();

            if (data?.token) {
                // Salvar dados no localStorage
                localStorage.setItem("token", data.token);
                localStorage.setItem("userRole", data.role || "USER");
                localStorage.setItem("tipoUsuario", data.tipoUsuario || "INDIVIDUAL");

                // Salvar nome da empresa se existir
                if (data.nomeEmpresa) {
                    localStorage.setItem("nomeEmpresa", data.nomeEmpresa);
                }

                // Salvar email do usuário
                localStorage.setItem("userEmail", emailInput.value.trim());

                // Redirecionar baseado no tipo de usuário
                if (data.role === "ADMIN") {
                    window.location.href = "/dashboard.html"; // Dashboard admin
                } else if (data.tipoUsuario === "EMPRESA") {
                    window.location.href = "/dashboard-empresa.html"; // Dashboard empresa
                } else {
                    window.location.href = "/comentarios.html"; // Usuário comum
                }
            } else {
                showMessage("Login ok, mas não recebi token.", 'error');
            }
        } catch (error) {
            console.error("Erro no login:", error);
            showMessage("Erro de conexão com o servidor", 'error');
        }
    });

    function showMessage(text, type = 'error') {
        messageDiv.textContent = text;
        messageDiv.className = `message ${type}`;
        messageDiv.style.display = 'block';
    }
});