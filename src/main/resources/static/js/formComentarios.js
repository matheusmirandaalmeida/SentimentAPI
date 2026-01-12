const token = localStorage.getItem("token");
if (!token) window.location.href = "/login.html";

const formComentarios = document.querySelector("#formAvaliacao");

formComentarios?.addEventListener("submit", async (e) => {
  e.preventDefault();

  const empresa = document.querySelector("#empresa").value.trim();
  const vinculo = document.querySelector('input[name="vinculo"]:checked')?.value;
  const situacao = document.querySelector("#situacao").value;
  const cargo = document.querySelector("#cargo").value.trim();
  const titulo = document.querySelector("#titulo").value.trim();
  const texto = document.querySelector("#avaliacao").value.trim();
  const termos = document.querySelector('input[name="termos"]').checked;

  if (!empresa || !vinculo || !situacao || !titulo || !texto || !termos) {
    alert("Preencha todos os campos obrigatórios e aceite os termos.");
    return;
  }

  const payload = { empresa, vinculo, situacao, cargo, titulo, texto };

  try {
    const resp = await fetch("/api/avaliacoes", {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
        "Authorization": `Bearer ${token}`
      },
      body: JSON.stringify(payload),
    });

    if (!resp.ok) {
      const err = await resp.text();
      throw new Error(err || `HTTP ${resp.status}`);
    }

    // se o backend não retornar JSON, isso aqui QUEBRA/ não mexer
    await resp.json().catch(() => null);

    window.location.href = "/avaliacoes.html";
  } catch (err) {
    console.error(err);
    alert("Erro ao enviar avaliação: " + err.message);
  }
});
