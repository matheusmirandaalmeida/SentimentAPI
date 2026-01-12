async function carregarAvaliacoes() {
    const container = document.getElementById("reviews");
    const status = document.getElementById("reviewsStatus");
    const errorDiv = document.getElementById("reviewsError");

    try {
        status.style.display = "flex";
        errorDiv.style.display = "none";
        container.innerHTML = "";

        const resp = await fetch('/api/avaliacoes?limit=3');

        if (!resp.ok) {
            throw new Error(`HTTP ${resp.status}`);
        }

        const data = await resp.json();
        status.style.display = "none";

        if (!data || data.length === 0) {
            container.innerHTML = `
                <div class="status" style="text-align: center; padding: 40px 0; color: rgba(15,23,42,0.6);">
                    <p>Ainda não há avaliações publicadas.</p>
                </div>
            `;
            return;
        }

        // Dados de exemplo para demonstração (serão substituídos pela API)
        const avaliacoesExemplo = [
            {
                id: 1,
                nome: "Teresa Silva",
                cargo: "Auditor",
                empresa: "Solorivo",
                data: "11/01/2006",
                sentimento: "positivo",
                texto: "A empresa é muito boa, ambiente colaborativo e oportunidades de crescimento.",
                tags: ["Cultura Forte", "Crescimento"]
            },
            {
                id: 2,
                nome: "Carlos Mendes",
                cargo: "Desenvolvedor",
                empresa: "TechCorp",
                data: "15/02/2024",
                sentimento: "negativo",
                texto: "Gestão muito verticalizada e sem espaço para inovação. Salário abaixo do mercado.",
                tags: ["Gestão", "Remuneração"]
            }
        ];

        // Usar dados da API se disponíveis, senão usar exemplo
        const avaliacoes = data.length > 0 ? data : avaliacoesExemplo;

        for (const item of avaliacoes) {
            const nome = item.nome || item.autor || "Usuário";
            const cargo = item.cargo || item.funcao || "";
            const empresa = item.empresa || "Empresa";
            const dataFormatada = formatarData(item.data || item.createdAt);
            const sentimento = (item.sentimento || "positivo").toLowerCase();
            const texto = item.texto || item.conteudo || "(sem texto)";
            const tags = item.tags || ["Geral"];

            // Criar a primeira letra para o avatar
            const primeiraLetra = nome.charAt(0).toUpperCase();

            // Determinar o texto do sentimento
            const textoSentimento = sentimento === 'positivo' ? 'Positiva' :
                                  sentimento === 'negativo' ? 'Negativa' :
                                  'Neutra';

            const el = document.createElement("article");
            el.className = `review ${sentimento}`;
            el.innerHTML = `
                <div class="review__header">
                    <div class="reviewer-info">
                        <div class="reviewer-avatar">${primeiraLetra}</div>
                        <div class="reviewer-details">
                            <h3 class="reviewer-name">${escapeHtml(nome)}</h3>
                            <p class="reviewer-role">${escapeHtml(cargo)}</p>
                            <span class="review-date">${dataFormatada}</span>
                        </div>
                    </div>
                </div>

                <div class="review-content">
                    <p class="review-text">${escapeHtml(texto)}</p>
                </div>

                ${tags.length > 0 ? `
                    <div class="review-tags">
                        ${tags.map(tag => `<span class="tag">${escapeHtml(tag)}</span>`).join('')}
                    </div>
                ` : ''}

                <div class="review-footer">
                    <span class="sentiment-badge">${textoSentimento}</span>
                </div>
            `;

            container.appendChild(el);
        }

    } catch (e) {
        console.error("Erro ao carregar avaliações:", e);
        status.style.display = "none";
        errorDiv.style.display = "block";
    }
}

function formatarData(dataString) {
    if (!dataString) return "Data não disponível";
    try {
        const data = new Date(dataString);
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

// Carregar avaliações quando a página carregar
document.addEventListener('DOMContentLoaded', carregarAvaliacoes);

// Para o botão "Tentar novamente"
window.carregarAvaliacoes = carregarAvaliacoes;