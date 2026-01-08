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

    // se backend devolver JSON com token
    const data = await resp.json().catch(() => null);

    if (data?.token) {
        localStorage.setItem("token", data.token);
        window.location.href = "/comentarios.html";
    } else {
        alert("Login ok, mas não recebi token.");
    }
});
