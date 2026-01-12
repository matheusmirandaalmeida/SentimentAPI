document.addEventListener('DOMContentLoaded', function() {
    const listaAvaliacoes = document.getElementById('listaAvaliacoes');
    const emptyState = document.getElementById('emptyState');
    const filterBtns = document.querySelectorAll('.filter-btn');
    const loadMoreBtn = document.getElementById('loadMoreBtn');

    const API_BASE_URL = window.AppConfig?.getApiBaseUrl() || '';
    let todasAvaliacoes = [];
    let currentFilter = 'all';
    let visibleCount = 5;

    // Função para carregar avaliações da API
    async function carregarAvaliacoes() {
        try {
            // Mostrar loading
            listaAvaliacoes.innerHTML = `
                <div class="status loading">
                    <div class="spinner"></div>
                    <p>Carregando avaliações...</p>
                </div>
            `;

            const response = await fetch('/api/avaliacoes');

            if (!response.ok) {
                throw new Error(`HTTP ${response.status}`);
            }

            const avaliacoes = await response.json();
            todasAvaliacoes = avaliacoes;

            if (avaliacoes.length === 0) {
                mostrarEmptyState();
            } else {
                renderizarAvaliacoes();
            }

        } catch (error) {
            console.error('Erro ao carregar avaliações:', error);
            listaAvaliacoes.innerHTML = `
                <div class="status error">
                    <p>Erro ao carregar avaliações. ${error.message}</p>
                    <button onclick="location.reload()">Tentar novamente</button>
                </div>
            `;
        }
    }

    // Função para renderizar as avaliações
    function renderizarAvaliacoes() {
        if (todasAvaliacoes.length === 0) {
            mostrarEmptyState();
            return;
        }

        // Filtrar avaliações
        const avaliacoesFiltradas = todasAvaliacoes.filter(avaliacao => {
            if (currentFilter === 'all') return true;
            if (currentFilter === 'positive') return avaliacao.sentimento === 'POSITIVO';
            if (currentFilter === 'negative') return avaliacao.sentimento === 'NEGATIVO';
            if (currentFilter === 'neutral') return avaliacao.sentimento === 'NEUTRO';
            return true;
        });

        // Limitar quantidade visível
        const avaliacoesParaMostrar = avaliacoesFiltradas.slice(0, visibleCount);

        if (avaliacoesParaMostrar.length === 0) {
            mostrarEmptyState();
            return;
        }

        // Limpar lista
        listaAvaliacoes.innerHTML = '';

        // Adicionar cada avaliação
        avaliacoesParaMostrar.forEach(avaliacao => {
            const reviewElement = criarReviewElement(avaliacao);
            listaAvaliacoes.appendChild(reviewElement);
        });

        // Controlar botão "Carregar mais"
        if (visibleCount >= avaliacoesFiltradas.length) {
            loadMoreBtn.style.display = 'none';
        } else {
            loadMoreBtn.style.display = 'block';
        }

        emptyState.style.display = 'none';
    }

    // Função para criar elemento HTML de uma avaliação
    function criarReviewElement(avaliacao) {
        const review = document.createElement('article');
        review.className = `review-card ${avaliacao.sentimento ? avaliacao.sentimento.toLowerCase() : 'neutral'}`;

        // Formatar data
        const dataFormatada = formatarData(avaliacao.createdAt);

        // Determinar classe e texto do sentimento
        let sentimentoClasse = '';
        let sentimentoTexto = '';

        switch(avaliacao.sentimento) {
            case 'POSITIVO':
                sentimentoClasse = 'positive';
                sentimentoTexto = 'Positiva';
                break;
            case 'NEGATIVO':
                sentimentoClasse = 'negative';
                sentimentoTexto = 'Negativa';
                break;
            case 'NEUTRO':
                sentimentoClasse = 'neutral';
                sentimentoTexto = 'Neutra';
                break;
            default:
                sentimentoClasse = 'neutral';
                sentimentoTexto = 'Neutra';
        }

        // Gerar avatar baseado na empresa
        const avatarInicial = avaliacao.empresa ? avaliacao.empresa.charAt(0).toUpperCase() : 'U';

        review.innerHTML = `
            <div class="review-header">
                <div class="reviewer-info">
                    <div class="reviewer-avatar">${avatarInicial}</div>
                    <div>
                        <div class="reviewer-name">${avaliacao.empresa || 'Empresa não informada'}</div>
                        <div class="reviewer-role">${avaliacao.cargo || 'Cargo não informado'}</div>
                    </div>
                </div>
                <div class="company">${avaliacao.vinculo === 'ATUAL' ? 'Atual' : 'Ex-funcionário'}</div>
            </div>

            <div class="review-date">${dataFormatada}</div>

            <h4 class="review-title">${avaliacao.titulo || 'Sem título'}</h4>
            <p class="review-text">${avaliacao.texto}</p>

            <div class="review-tags">
                <span class="tag">${avaliacao.situacao || 'Situação não informada'}</span>
                <span class="tag">${avaliacao.vinculo === 'ATUAL' ? 'Atual' : 'Ex-funcionário'}</span>
                ${avaliacao.cargo ? `<span class="tag">${avaliacao.cargo}</span>` : ''}
            </div>

            <div class="review-footer">
                <span class="sentiment-badge ${sentimentoClasse}">${sentimentoTexto}</span>
            </div>
        `;

        return review;
    }

    // Função para formatar data
    function formatarData(dataISO) {
        if (!dataISO) return 'Data não informada';

        try {
            const data = new Date(dataISO);
            return data.toLocaleDateString('pt-BR', {
                day: '2-digit',
                month: '2-digit',
                year: 'numeric'
            });
        } catch (e) {
            return 'Data inválida';
        }
    }

    // Função para mostrar estado vazio
    function mostrarEmptyState() {
        listaAvaliacoes.innerHTML = '';
        emptyState.style.display = 'block';
        loadMoreBtn.style.display = 'none';
    }

    // Configurar eventos dos filtros
    filterBtns.forEach(btn => {
        btn.addEventListener('click', function() {
            // Remover classe active de todos
            filterBtns.forEach(b => b.classList.remove('active'));

            // Adicionar ao botão clicado
            this.classList.add('active');

            // Atualizar filtro
            currentFilter = this.dataset.filter;
            visibleCount = 5; // Resetar contador

            // Renderizar com novo filtro
            if (todasAvaliacoes.length > 0) {
                renderizarAvaliacoes();
            }
        });
    });

    // Configurar botão "Carregar mais"
    loadMoreBtn.addEventListener('click', function() {
        visibleCount += 5;
        renderizarAvaliacoes();
    });

    // Carregar avaliações ao iniciar
    carregarAvaliacoes();

    // Atualizar a cada 30 segundos (opcional)
    setInterval(carregarAvaliacoes, 30000);
});