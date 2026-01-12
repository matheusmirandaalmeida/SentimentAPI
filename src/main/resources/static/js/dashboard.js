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

        // Tenta buscar da API real
        const response = await fetch('/api/dashboard', {
            headers: {
                'Authorization': token ? token : '',
                'Content-Type': 'application/json'
            }
        });

        let data;

        if (response.ok) {
            data = await response.json();
            console.log('Dados da API:', data);
        } else {
            // Fallback para dados mockados
            console.warn('API não disponível, usando dados mockados');
            data = {
                positivo: 65,
                neutro: 20,
                negativo: 15,
                total: 100,
                comentarios: [
                    { texto: "Excelente serviço, recomendo!", sentimento: "positivo" },
                    { texto: "Poderia ser melhor", sentimento: "negativo" },
                    { texto: "Muito satisfeito", sentimento: "positivo" },
                    { texto: "Não gostei do atendimento", sentimento: "negativo" },
                    { texto: "Produto de boa qualidade", sentimento: "positivo" },
                    { texto: "Mais ou menos", sentimento: "neutro" }
                ]
            };
        }

        updateMetrics(data);
        createChart(data);
        showSummary('positivo', data);

    } catch (error) {
        console.error('Erro ao carregar dados:', error);
        // Fallback para dados mock em caso de erro
        const fallbackData = {
            positivo: 45,
            neutro: 30,
            negativo: 25,
            total: 100,
            comentarios: []
        };
        updateMetrics(fallbackData);
        createChart(fallbackData);
    }
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
                    const mockData = {
                        positivo: 65,
                        neutro: 20,
                        negativo: 15,
                        total: 100,
                        comentarios: [
                            { texto: "Excelente serviço, recomendo!", sentimento: "positivo" },
                            { texto: "Poderia ser melhor", sentimento: "negativo" },
                            { texto: "Muito satisfeito", sentimento: "positivo" }
                        ]
                    };
                    showSummary(sentiments[index], mockData);

                    // Destacar card selecionado
                    highlightCard(sentiments[index]);
                }
            }
        }
    });
}

function showSummary(sentiment, data) {
    console.log('Mostrando resumo para:', sentiment);

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

    // Atualizar info
    const infoElement = document.getElementById('info-resumo');
    if (infoElement) {
        const porcentagem = data[sentiment] || 0;
        const totalMensagens = Math.round(data.total * porcentagem / 100);
        infoElement.textContent = `${porcentagem}% do total (${totalMensagens} mensagens)`;
    }

    // Atualizar lista
    const lista = document.getElementById('lista-resumo');
    if (lista) {
        lista.innerHTML = '';

        // Filtrar comentários pelo sentimento
        const comentariosFiltrados = data.comentarios
            ? data.comentarios.filter(c => c.sentimento === sentiment)
            : [];

        if (comentariosFiltrados.length === 0) {
            const li = document.createElement('li');
            li.className = 'estado-vazio';
            li.innerHTML = `
                <i class="fas fa-info-circle"></i>
                <p>Nenhum comentário ${sentiment} encontrado</p>
            `;
            lista.appendChild(li);
        } else {
            comentariosFiltrados.slice(0, 5).forEach(comentario => {
                const li = document.createElement('li');
                li.textContent = comentario.texto;
                lista.appendChild(li);
            });
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

            // Atualizar resumo
            const mockData = {
                positivo: 65,
                neutro: 20,
                negativo: 15,
                total: 100,
                comentarios: [
                    { texto: "Excelente serviço, recomendo!", sentimento: "positivo" },
                    { texto: "Poderia ser melhor", sentimento: "negativo" },
                    { texto: "Muito satisfeito", sentimento: "positivo" }
                ]
            };
            showSummary(sentiment, mockData);
        });
    });

    // Evento para etiquetas
    document.querySelectorAll('.etiqueta').forEach(etiqueta => {
        etiqueta.addEventListener('click', function() {
            const sentiment = this.dataset.sentimento;
            console.log('Etiqueta clicada:', sentiment);

            const mockData = {
                positivo: 65,
                neutro: 20,
                negativo: 15,
                total: 100,
                comentarios: [
                    { texto: "Excelente serviço, recomendo!", sentimento: "positivo" },
                    { texto: "Poderia ser melhor", sentimento: "negativo" },
                    { texto: "Muito satisfeito", sentimento: "positivo" }
                ]
            };
            showSummary(sentiment, mockData);

            // Destacar card correspondente
            highlightCard(sentiment);
        });
    });

    // Evento para seleção de tempo
    const tempoSelect = document.querySelector('.selecao-tempo');
    if (tempoSelect) {
        tempoSelect.addEventListener('change', function() {
            console.log('Período selecionado:', this.value);
            // Aqui você pode carregar dados diferentes baseado no período
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

    // Botões de ação
    document.querySelectorAll('.botao-acao').forEach(botao => {
        botao.addEventListener('click', function() {
            const texto = this.textContent.trim();
            console.log('Botão clicado:', texto);

            if (texto.includes('Exportar')) {
                alert('Funcionalidade de exportação em desenvolvimento!');
            } else if (texto.includes('Ver Detalhes')) {
                alert('Redirecionando para detalhes...');
                // Aqui você pode redirecionar para uma página de detalhes
            }
        });
    });
}

// Inicialização alternativa se o DOM já estiver carregado
if (document.readyState === 'complete' || document.readyState === 'interactive') {
    setTimeout(() => {
        const token = localStorage.getItem('token');
        if (token) {
            loadDashboardData();
            setupEventListeners();
        }
    }, 100);
}