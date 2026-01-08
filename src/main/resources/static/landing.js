async function carregarAvaliacoes() {
    const container = document.getElementById("reviews");
    const status = document.getElementById("reviewsStatus");

    try {
        const resp = await fetch("/api/avaliacoes?limit=10");
        if (!resp.ok) throw new Error("HTTP " + resp.status);

        const data = await resp.json(); // espera: [{nomeUsuario, empresa, texto, ...}, ...]

        container.innerHTML = "";

        if (!data || data.length === 0) {
            status.textContent = "Ainda não há avaliações publicadas.";
            return;
        }

        status.textContent = "";
        for (const item of data) {
            const nome = item.nomeUsuario ?? "Usuário";
            const empresa = item.empresa ?? "Empresa";
            const texto = item.texto ?? "(sem texto)";

            const el = document.createElement("article");
            el.className = "review";
            el.innerHTML = `
        <p class="review__meta">Nome do usuário</p>
        <p class="review__meta">${escapeHtml(nome)}</p>

        <p class="review__meta" style="margin-top:10px;">Nome da empresa que ele trabalha</p>
        <p class="review__meta">${escapeHtml(empresa)}</p>

        <div class="review__box">
          <p class="review__text">${escapeHtml(texto)}</p>
          <small class="review__small">Avaliação do usuários</small>
        </div>
      `;
            container.appendChild(el);
        }
    } catch (e) {
        status.textContent = "Não foi possível carregar as avaliações agora.";
        console.error(e);
    }
}

function escapeHtml(str) {
    return String(str)
        .replaceAll("&", "&amp;")
        .replaceAll("<", "&lt;")
        .replaceAll(">", "&gt;")
        .replaceAll('"', "&quot;")
        .replaceAll("'", "&#039;");
}

carregarAvaliacoes();
