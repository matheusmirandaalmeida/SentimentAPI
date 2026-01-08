const form = document.getElementById("formCadastro");
const email = document.getElementById("emailCadastro");
const senha = document.getElementById("senhaCadastro");

form.addEventListener("submit", async (e) => {
    e.preventDefault();

    const resp = await fetch("/auth/register", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({
            email: email.value.trim(),
            senha: senha.value
        })
    });

    const msg = await resp.text();
    console.log("STATUS:", resp.status, msg);

    if (!resp.ok) {
        alert(msg || "Falha no cadastro");
        return;
    }

    alert(msg || "Cadastro realizado!");
    window.location.href = "/login.html";
});
