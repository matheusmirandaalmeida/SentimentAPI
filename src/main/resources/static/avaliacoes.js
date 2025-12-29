const lista = document.querySelector("#listaAvaliacoes");
const emptyState = document.querySelector("#emptyState");

function formatDate(iso) {
  const d = new Date(iso);
  return d.toLocaleString("pt-BR");
}

function cardHTML(a) {
  const sentimento = a.sentimento ? ` • <strong>${a.sentimento}</strong>` : "";
  return `
    <article class="review">
      <div class="name">${a.cargo || "Usuário"}</div>
      <div class="company">${a.empresa} • ${formatDate(a.createdAt)}${sentimento}</div>
      <div class="box">
        <p><strong>${a.titulo}</strong><br/>${a.texto}</p>
      </div>
    </article>
  `;
}

async function render() {
  try {
    const resp = await fetch("/api/avaliacoes");

    if (!resp.ok) {
      const err = await resp.text();
      throw new Error(`HTTP ${resp.status} - ${err}`);
    }

    const avaliacoes = await resp.json();

    if (!avaliacoes.length) {
      emptyState.style.display = "block";
      lista.innerHTML = "";
      return;
    }

    emptyState.style.display = "none";
    lista.innerHTML = avaliacoes.map(cardHTML).join("");
  } catch (err) {
    console.error(err);
    emptyState.style.display = "block";
    emptyState.textContent = "Erro ao carregar avaliações.";
    lista.innerHTML = "";
  }
}

render();
