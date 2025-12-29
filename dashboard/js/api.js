import { mockDashboardData } from "./mockData.js";

const USE_MOCK = true; // ← TROQUE PARA false quando a API estiver pronta
const API_URL = "/api/v1/dashboard";

export async function getDashboardData() {
  if (USE_MOCK) {
    await new Promise(r => setTimeout(r, 800)); // simula latência
    return mockDashboardData;
  }

  const token = localStorage.getItem("token");

  const res = await fetch("/api/dashboard", {
    headers: { Authorization: `Bearer ${token}` }
  });

  if (!res.ok) throw new Error("Erro ao buscar dados do dashboard");

  return res.json();
}

export async function getDashboardData() {
  const token = localStorage.getItem("token");

  const res = await fetch("/api/dashboard", {
    headers: { Authorization: `Bearer ${token}` }
  });

  if (!res.ok) throw new Error("Erro na API");

  return res.json();
}
