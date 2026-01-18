document.addEventListener('DOMContentLoaded', function() {
    console.log('Dashboard.js carregado');

    // Verificar se está logado
    const token = localStorage.getItem('token');
    if (!token) {
        alert('Você precisa fazer login primeiro!');
        window.location.href = '/';
        return;
    }

    // Mostrar email do usuário
    const userEmail = localStorage.getItem('userEmail') || 'admin@sentiment.com';
    document.getElementById('user').textContent = userEmail;

    // Carregar dados iniciais
    loadDashboardData();
    setupEventListeners();
});

async function loadDashboardData() {
    try {
        console.log('Carregando dados do dashboard...');

        const token = localStorage.getItem('token');

        // Buscar da API real
        const response = await fetch('/api/dashboard', {
            headers: {
                'Authorization': token ? token : '',
                'Content-Type': 'application/json'
            }
        });

        if (!response.ok) {
            throw new Error(`Erro na API: ${response.status}`);
        }

        const data = await response.json();
        console.log('Dados da API:', data);

        // Ajustar estrutura dos dados para o frontend
        const dadosFormatados = {
            positivo: data.positivo || 0,
            neutro: data.neutro || 0,
            negativo: data.negativo || 0,
            total: data.total || 0,
            comentarios: formatarComentarios(data.comentarios || [])
        };

        updateMetrics(dadosFormatados);
        createChart(dadosFormatados);
        showSummary('positivo', dadosFormatados);

    } catch (error) {
        console.error('Erro ao carregar dados:', error);

        // Fallback ajustado para estrutura correta
        const fallbackData = {
            positivo: 45,
            neutro: 30,
            negativo: 25,
            total: 100,
            comentarios: [
                { texto: "Sistema offline. Verifique a conexão.", sentimento: "negativo" },
                { texto: "Dados de teste sendo exibidos", sentimento: "neutro" }
            ]
        };
        updateMetrics(fallbackData);
        createChart(fallbackData);
        showSummary('positivo', fallbackData);
    }
}

function formatarComentarios(comentariosAPI) {
    console.log('Formatando comentários da API:', comentariosAPI);

    if (!comentariosAPI || comentariosAPI.length === 0) {
        return [];
    }

    // Se já for array de objetos com texto e sentimento
    if (Array.isArray(comentariosAPI) && comentariosAPI[0] && comentariosAPI[0].texto !== undefined) {
        return comentariosAPI.map(c => ({
            texto: c.texto || c,
            sentimento: c.sentimento || "neutro"
        }));
    }

    // Se for array de strings (legado)
    if (Array.isArray(comentariosAPI) && typeof comentariosAPI[0] === 'string') {
        return comentariosAPI.map(texto => ({
            texto: texto,
            sentimento: "neutro" // default
        }));
    }

    return [];
}

function updateMetrics(data) {
    console.log('Atualizando métricas com dados reais:', data);

    // Atualizar porcentagens
    document.getElementById('porcentagem-positivo').textContent = `${data.positivo}%`;
    document.getElementById('porcentagem-neutro').textContent = `${data.neutro}%`;
    document.getElementById('porcentagem-negativo').textContent = `${data.negativo}%`;

    // Atualizar barras de progresso
    document.querySelector('.progresso-positivo').style.width = `${data.positivo}%`;
    document.querySelector('.progresso-neutro').style.width = `${data.neutro}%`;
    document.querySelector('.progresso-negativo').style.width = `${data.negativo}%`;

    // Atualizar totais
    if (document.getElementById('total-avaliacoes')) {
        document.getElementById('total-avaliacoes').textContent = data.total || 0;
    }
}

function createChart(data) {
    const ctx = document.getElementById('grafico-sentimento');
    if (!ctx) {
        console.error('Canvas do gráfico não encontrado!');
        return;
    }

    if (window.sentimentChart) {
        window.sentimentChart.destroy();
    }

    window.sentimentChart = new Chart(ctx, {
        type: 'doughnut',
        data: {
            labels: ['Positivo', 'Neutro', 'Negativo'],
            datasets: [{
                data: [data.positivo, data.neutro, data.negativo],
                backgroundColor: [
                    '#10b981', // verde
                    '#f59e0b', // laranja
                    '#ef4444'  // vermelho
                ],
                borderWidth: 2,
                borderColor: '#ffffff'
            }]
        },
        options: {
            responsive: true,
            maintainAspectRatio: false,
            plugins: {
                legend: {
                    position: 'bottom',
                    labels: {
                        padding: 20,
                        font: {
                            size: 14
                        }
                    }
                },
                tooltip: {
                    callbacks: {
                        label: function(context) {
                            return `${context.label}: ${context.raw}%`;
                        }
                    }
                }
            },
            onClick: (evt, elements) => {
                if (elements.length > 0) {
                    const index = elements[0].index;
                    const sentiments = ['positivo', 'neutro', 'negativo'];

                    // Usar dados reais do gráfico
                    const chartData = window.sentimentChart.data.datasets[0].data;
                    const dadosAtuais = {
                        positivo: chartData[0],
                        neutro: chartData[1],
                        negativo: chartData[2],
                        total: data.total || 0,
                        comentarios: data.comentarios || []
                    };

                    showSummary(sentiments[index], dadosAtuais);
                    highlightCard(sentiments[index]);
                }
            }
        }
    });
}

function showSummary(sentiment, data) {
    console.log('Mostrando resumo para:', sentiment, 'com', data.comentarios?.length, 'comentários');

    const tituloMap = {
        'positivo': 'Comentários Positivos',
        'neutro': 'Comentários Neutros',
        'negativo': 'Comentários Negativos'
    };

    const corMap = {
        'positivo': 'positiva',
        'neutro': 'neutra',
        'negativo': 'negativa'
    };

    // Atualizar título
    const tituloElement = document.getElementById('titulo-resumo');
    if (tituloElement) {
        tituloElement.textContent = tituloMap[sentiment] || 'Resumo';
    }

    // Atualizar info com dados REAIS do banco
    const infoElement = document.getElementById('info-resumo');
    if (infoElement) {
        const porcentagem = data[sentiment] || 0;

        // Calcular número de mensagens baseado no total e porcentagem
        const totalMensagens = Math.round((data.total * porcentagem) / 100);

        // Ou usar contagem direta dos comentários filtrados
        const comentariosFiltrados = data.comentarios.filter(c =>
            c.sentimento && c.sentimento.toLowerCase() === sentiment.toLowerCase()
        );
        const contagemReal = comentariosFiltrados.length;

        infoElement.textContent = `${porcentagem}% do total (${contagemReal > 0 ? contagemReal : totalMensagens} mensagens)`;
    }

    // Atualizar lista com dados REAIS
    const lista = document.getElementById('lista-resumo');
    if (lista) {
        lista.innerHTML = '';

        // Filtrar comentários pelo sentimento (case insensitive)
        const comentariosFiltrados = data.comentarios.filter(c =>
            c.sentimento && c.sentimento.toLowerCase() === sentiment.toLowerCase()
        );

        console.log(`Comentários ${sentiment} encontrados:`, comentariosFiltrados.length);

        if (comentariosFiltrados.length === 0) {
            const li = document.createElement('li');
            li.className = 'estado-vazio';
            li.innerHTML = `
                <i class="fas fa-info-circle"></i>
                <p>Nenhum comentário ${sentiment} encontrado</p>
            `;
            lista.appendChild(li);
        } else {
            comentariosFiltrados.slice(0, 10).forEach((comentario, index) => {
                const li = document.createElement('li');
                li.className = 'item-comentario';
                li.innerHTML = `
                    <span class="numero-comentario">${index + 1}.</span>
                    <span class="texto-comentario">${comentario.texto}</span>
                `;
                lista.appendChild(li);
            });

            // Mostrar mensagem se houver mais comentários
            if (comentariosFiltrados.length > 10) {
                const li = document.createElement('li');
                li.className = 'mais-comentarios';
                li.innerHTML = `
                    <i class="fas fa-ellipsis-h"></i>
                    <span>... e mais ${comentariosFiltrados.length - 10} comentários</span>
                `;
                lista.appendChild(li);
            }
        }
    }

    // Ativar etiqueta correspondente
    document.querySelectorAll('.etiqueta').forEach(etiqueta => {
        etiqueta.classList.remove('ativa');
        if (etiqueta.dataset.sentimento === sentiment) {
            etiqueta.classList.add('ativa');
        }
    });
}

function highlightCard(sentiment) {
    // Remover destaque de todos os cards
    document.querySelectorAll('.cartao-metrica').forEach(card => {
        card.classList.remove('ativo');
    });

    // Destacar card correspondente
    const card = document.querySelector(`.cartao-metrica[data-sentimento="${sentiment}"]`);
    if (card) {
        card.classList.add('ativo');
    }
}

function setupEventListeners() {
    console.log('Configurando listeners...');

    // Evento para cartões de métrica
    document.querySelectorAll('.cartao-metrica').forEach(card => {
        card.addEventListener('click', function() {
            const sentiment = this.dataset.sentimento;
            console.log('Card clicado:', sentiment);

            // Mostrar qual está ativo
            highlightCard(sentiment);

            // Recarregar dados do banco
            fetchDashboardData(sentiment);
        });
    });

    // Evento para etiquetas
    document.querySelectorAll('.etiqueta').forEach(etiqueta => {
        etiqueta.addEventListener('click', function() {
            const sentiment = this.dataset.sentimento;
            console.log('Etiqueta clicada:', sentiment);

            // Recarregar dados do banco
            fetchDashboardData(sentiment);
        });
    });

    // Evento para seleção de tempo
    const tempoSelect = document.querySelector('.selecao-tempo');
    if (tempoSelect) {
        tempoSelect.addEventListener('change', function() {
            console.log('Período selecionado:', this.value);
            // Recarregar dados
            loadDashboardData();
        });
    }

    // Botão de logout
    window.sair = function() {
        console.log('Logout solicitado');
        localStorage.removeItem('token');
        localStorage.removeItem('userRole');
        localStorage.removeItem('userEmail');
        window.location.href = '/';
    };
}

// Função auxiliar para buscar dados do dashboard
async function fetchDashboardData(sentiment) {
    try {
        const token = localStorage.getItem('token');
        const response = await fetch('/api/dashboard', {
            headers: {
                'Authorization': token ? token : '',
                'Content-Type': 'application/json'
            }
        });

        if (!response.ok) throw new Error('Erro na API');

        const data = await response.json();
        const dadosFormatados = {
            positivo: data.positivo || 0,
            neutro: data.neutro || 0,
            negativo: data.negativo || 0,
            total: data.total || 0,
            comentarios: formatarComentarios(data.comentarios || [])
        };

        showSummary(sentiment, dadosFormatados);
        highlightCard(sentiment);
    } catch (error) {
        console.error('Erro ao buscar dados:', error);
    }
}

if (document.readyState === 'complete' || document.readyState === 'interactive') {
    setTimeout(() => {
        const token = localStorage.getItem('token');
        if (token) {
            loadDashboardData();
            setupEventListeners();
        }
    }, 100);
}