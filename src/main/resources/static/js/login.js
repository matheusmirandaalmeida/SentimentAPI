const form = document.getElementById("formLogin");
const emailInput = document.getElementById("emailLogin");
const senhaInput = document.getElementById("senhaLogin");

form.addEventListener("submit", async (e) => {
    e.preventDefault();

    const resp = await fetch("/auth/login", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({
            email: emailInput.value.trim(),
            senha: senhaInput.value
        })
    });

    if (!resp.ok) {
        const msg = await resp.text();
        alert(msg || "Falha no login");
        return;
    }

    const data = await resp.json();

    if (data?.token) {
        localStorage.setItem("token", data.token);
        localStorage.setItem("userRole", data.role || "USER");

        // Redireciona baseado no role
        if (data.role === "ADMIN") {
            window.location.href = "/dashboard.html"; // Página do admin
        } else {
            window.location.href = "/comentarios.html"; // Página do usuário comum
        }
    } else {
        alert("Login ok, mas não recebi token.");
    }
});
