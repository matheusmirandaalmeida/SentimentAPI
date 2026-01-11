async function carregarAvaliacoes() {
    const container = document.getElementById("reviews");
    const status = document.getElementById("reviewsStatus");

    try {
        // Usa a URL correta da API Spring
        const resp = await fetch("http://localhost:8080/api/avaliacoes?limit=3");
        if (!resp.ok) throw new Error("HTTP " + resp.status);

        const data = await resp.json();

        container.innerHTML = "";

        if (!data || data.length === 0) {
            status.textContent = "Ainda não há avaliações publicadas.";
            return;
        }

        status.textContent = "";
        for (const item of data) {
            // Usa os campos corretos da API Spring
            const empresa = item.empresa ?? "Empresa";
            const cargo = item.cargo ? ` • ${item.cargo}` : "";
            const titulo = item.titulo ?? "Sem título";
            const sentimento = item.sentimento ?? "";
            const vinculo = item.vinculo ?? "";
            const situacao = item.situacao ?? "";
            const texto = item.texto ?? "(sem texto)";

            const el = document.createElement("article");
            el.className = "review";
            el.innerHTML = `
                <div class="review__header">
                    <p class="review__meta">
                        <strong>${escapeHtml(empresa)}</strong>${escapeHtml(cargo)}
                        <span class="sentiment-badge sentiment-${sentimento.toLowerCase()}">
                            ${escapeHtml(sentimento)}
                        </span>
                    </p>
                    <p class="review__title">${escapeHtml(titulo)}</p>
                </div>

                <div class="review__box">
                    <p class="review__text">${escapeHtml(texto)}</p>
                    <small class="review__small">
                        ${escapeHtml(vinculo)} • ${escapeHtml(situacao)} •
                        ${formatarData(item.createdAt)}
                    </small>
                </div>
            `;
            container.appendChild(el);
        }
    } catch (e) {
        status.textContent = "Não foi possível carregar as avaliações agora.";
        console.error("Erro ao carregar avaliações:", e);
    }
}

function formatarData(isoString) {
    if (!isoString) return "Data não disponível";
    try {
        const data = new Date(isoString);
        return data.toLocaleDateString('pt-BR', {
            day: '2-digit',
            month: '2-digit',
            year: 'numeric'
        });
    } catch {
        return "Data inválida";
    }
}

function escapeHtml(str) {
    if (str === null || str === undefined) return "";
    return String(str)
        .replaceAll("&", "&amp;")
        .replaceAll("<", "&lt;")
        .replaceAll(">", "&gt;")
        .replaceAll('"', "&quot;")
        .replaceAll("'", "&#039;");
}

// Carrega as avaliações quando a página carrega
document.addEventListener('DOMContentLoaded', carregarAvaliacoes);