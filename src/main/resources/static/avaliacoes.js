const lista = document.querySelector("#listaAvaliacoes");
const emptyState = document.querySelector("#emptyState");

function getAvaliacoes() {
  return JSON.parse(localStorage.getItem("avaliacoes") || "[]");
}

function formatDate(iso) {
  const d = new Date(iso);
  return d.toLocaleString("pt-BR");
}

function cardHTML(a) {
  return `
    <article class="review">
      <div class="name">${a.nome || "Usuário"}</div>
      <div class="company">${a.empresa} • ${formatDate(a.createdAt)}</div>
      <div class="box">
        <p><strong>${a.titulo}</strong><br/>${a.texto}</p>
      </div>
    </article>
  `;
}

function render() {
  const avaliacoes = getAvaliacoes();

  if (!avaliacoes.length) {
    emptyState.style.display = "block";
    lista.innerHTML = "";
    return;
  }

  emptyState.style.display = "none";
  lista.innerHTML = avaliacoes.map(cardHTML).join("");
}

render();
