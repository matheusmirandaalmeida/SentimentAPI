let chart;

export function renderChart({ positivo, negativo, neutro }) {
  const ctx = document.getElementById("sentimentChart");

  if (chart) chart.destroy();

  chart = new Chart(ctx, {
    type: "doughnut",
    data: {
      labels: ["Positivo", "Negativo", "Neutro"],
      datasets: [{
        data: [positivo, negativo, neutro],
        backgroundColor: ["#0074D9", "#8B5E3C", "#2CA89A"],
        borderWidth: 0
      }]
    },
    options: {
      cutout: "70%",
      responsive: true,
      maintainAspectRatio: false
    }
  });
}
