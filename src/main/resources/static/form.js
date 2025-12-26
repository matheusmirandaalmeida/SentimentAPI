const form = document.querySelector("#formAvaliacao");

function getAvaliacoes() {
  return JSON.parse(localStorage.getItem("avaliacoes") || "[]");
}

function setAvaliacoes(lista) {
  localStorage.setItem("avaliacoes", JSON.stringify(lista));
}

form.addEventListener("submit", (e) => {
  e.preventDefault();

  const empresa = document.querySelector("#empresa").value.trim();
  const vinculo = document.querySelector('input[name="vinculo"]:checked')?.value;
  const situacao = document.querySelector("#situacao").value;
  const cargo = document.querySelector("#cargo").value.trim();
  const titulo = document.querySelector("#titulo").value.trim();
  const texto = document.querySelector("#avaliacao").value.trim();
  const termos = document.querySelector('input[name="termos"]').checked;

  if (!termos) return;

  const nova = {
    id: crypto.randomUUID(),
    nome: "Anônimo",
    empresa,
    vinculo,
    situacao,
    cargo,
    titulo,
    texto,
    createdAt: new Date().toISOString()
  };

  const atual = getAvaliacoes();
  atual.unshift(nova);
  setAvaliacoes(atual);

  window.location.href = "avaliacoes.html";
});
