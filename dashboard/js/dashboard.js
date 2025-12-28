/* =========================
   CONFIGURAÇÃO DA API
========================= */
const API_URL = "http://localhost:8080/api/v1/sentiment/dashboard";

/* =========================
   ELEMENTOS DO DOM
========================= */
const positiveValue = document.getElementById("positive-value");
const neutralValue = document.getElementById("neutral-value");
const negativeValue = document.getElementById("negative-value");

const summaryTitle = document.getElementById("summary-title");
const summaryInfo = document.getElementById("summary-info");
const summaryList = document.getElementById("summary-list");

const sentimentCards = document.querySelectorAll(".sentiment-card");
const chartCanvas = document.getElementById("sentimentChart");

/* =========================
   ESTADO GLOBAL
========================= */
let dashboardData = {
  positive: { count: 0, comments: [] },
  neutral: { count: 0, comments: [] },
  negative: { count: 0, comments: [] }
};

let sentimentChart;

/* =========================
   LOAD DASHBOARD
========================= */
async function loadDashboard() {
  try {
    const response = await fetch(API_URL);
    const data = await response.json();

    dashboardData = {
      positive: {
        count: data.positive,
        comments: data.comments
      },
      neutral: {
        count: data.neutral,
        comments: data.comments
      },
      negative: {
        count: data.negative,
        comments: data.comments
      }
    };

    updateMetrics();
    updateChart();
    renderSummary("positive");

  } catch (error) {
    console.error("Erro ao carregar dashboard:", error);
  }
}

/* =========================
   MÉTRICAS
========================= */
function updateMetrics() {
  const total = getTotalMessages();

  positiveValue.textContent = `${getPercentage(dashboardData.positive.count, total)}%`;
  neutralValue.textContent = `${getPercentage(dashboardData.neutral.count, total)}%`;
  negativeValue.textContent = `${getPercentage(dashboardData.negative.count, total)}%`;
}

function getTotalMessages() {
  return (
    dashboardData.positive.count +
    dashboardData.neutral.count +
    dashboardData.negative.count
  );
}

function getPercentage(value, total = getTotalMessages()) {
  if (total === 0) return 0;
  return Math.round((value / total) * 100);
}

/* =========================
   GRÁFICO
========================= */
function updateChart() {
  const chartData = [
    dashboardData.positive.count,
    dashboardData.neutral.count,
    dashboardData.negative.count
  ];

  if (!sentimentChart) {
    sentimentChart = new Chart(chartCanvas, {
      type: "doughnut",
      data: {
        labels: ["Positivo", "Neutro", "Negativo"],
        datasets: [{
          data: chartData
        }]
      },
      options: {
        responsive: true,
        onClick: (_, elements) => {
          if (elements.length > 0) {
            const index = elements[0].index;
            const sentiment = ["positive", "neutral", "negative"][index];
            renderSummary(sentiment);
          }
        }
      }
    });
  } else {
    sentimentChart.data.datasets[0].data = chartData;
    sentimentChart.update();
  }
}

/* =========================
   RESUMO
========================= */
function renderSummary(sentiment) {
  const data = dashboardData[sentiment];
  const total = getTotalMessages();
  const percentage = getPercentage(data.count, total);

  // TÍTULO FIXO
  summaryTitle.textContent = "Resumo";

  // TEXTO DINÂMICO
  summaryInfo.textContent =
    `${sentiment.toUpperCase()} — ${data.count} mensagens (${percentage}%)`;

  // LISTA DE MENSAGENS
  summaryList.innerHTML = "";

  data.comments.forEach(comment => {
    const li = document.createElement("li");
    li.textContent = comment;
    summaryList.appendChild(li);
  });

  // CARD ATIVO
  sentimentCards.forEach(card => card.classList.remove("active"));
  document
    .querySelector(`.sentiment-card[data-sentiment="${sentiment}"]`)
    .classList.add("active");
}

/* =========================
   EVENTOS DOS CARDS
========================= */
sentimentCards.forEach(card => {
  card.addEventListener("click", () => {
    const sentiment = card.dataset.sentiment;
    renderSummary(sentiment);
  });
});

/* =========================
   TEMPO REAL (POLLING)
========================= */
setInterval(loadDashboard, 5000);

/* =========================
   INIT
========================= */
loadDashboard();
