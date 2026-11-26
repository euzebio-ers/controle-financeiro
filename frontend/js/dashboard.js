const cardSaldoTotal = document.getElementById("cardSaldoTotal");
const cardReceitas = document.getElementById("cardReceitas");
const cardDespesas = document.getElementById("cardDespesas");
const corpoTabela = document.getElementById("corpoTabela");
const estadoVazio = document.getElementById("estadoVazio");
const caixaMensagem = document.getElementById("caixaMensagem");
const mesAtual = document.getElementById("mesAtual");

function escaparHTML(valor) {
  return String(valor ?? "").replace(/[&<>"']/g, (c) => ({
    "&": "&amp;", "<": "&lt;", ">": "&gt;", '"': "&quot;", "'": "&#39;",
  })[c]);
}

function formatarMoeda(valor) {
  return Number(valor).toLocaleString("pt-BR", { style: "currency", currency: "BRL" });
}

function formatarData(data) {
  const [ano, mes, dia] = data.split("-");
  return `${dia}/${mes}/${ano}`;
}

function diasDeAtraso(vencimento) {
  const [ano, mes, dia] = vencimento.split("-").map(Number);
  const venc = new Date(ano, mes - 1, dia);
  const hoje = new Date();
  hoje.setHours(0, 0, 0, 0);
  return Math.round((hoje - venc) / 86400000);
}

function mostrarMensagem(texto, tipo) {
  caixaMensagem.textContent = texto;
  caixaMensagem.className = "mensagem " + (tipo === "erro" ? "mensagem-erro" : "mensagem-sucesso");
}

async function carregarDashboard() {
  // Ex.: "Resumo de novembro de 2026"
  const nomeMes = new Date().toLocaleDateString("pt-BR", { month: "long", year: "numeric" });
  mesAtual.textContent = `Resumo de ${nomeMes}`;

  try {
    const dados = await api.get("/dashboard");
    renderizarCards(dados);
    renderizarAtrasadas(dados.atrasadas);
  } catch (erro) {
    corpoTabela.innerHTML = "";
    mostrarMensagem(erro.message, "erro");
  }
}

function renderizarCards(dados) {
  cardSaldoTotal.textContent = formatarMoeda(dados.saldoTotal);
  cardSaldoTotal.className =
    "card__valor " + (Number(dados.saldoTotal) < 0 ? "valor-negativo" : "valor-positivo");

  cardReceitas.textContent = formatarMoeda(dados.receitasMes);
  cardDespesas.textContent = formatarMoeda(dados.despesasMes);
}

function renderizarAtrasadas(atrasadas) {
  corpoTabela.innerHTML = "";

  if (!atrasadas || atrasadas.length === 0) {
    estadoVazio.style.display = "block";
    return;
  }
  estadoVazio.style.display = "none";

  const ordenadas = [...atrasadas].sort((a, b) => a.vencimento.localeCompare(b.vencimento));

  ordenadas.forEach((t) => {
    const dias = diasDeAtraso(t.vencimento);
    const linha = document.createElement("tr");
    linha.classList.add("linha-atrasada");
    linha.innerHTML = `
      <td>${escaparHTML(t.descricao)}</td>
      <td>${formatarMoeda(t.valor)}</td>
      <td>${formatarData(t.vencimento)}</td>
      <td class="dias-atraso">${dias} ${dias === 1 ? "dia" : "dias"}</td>
    `;
    corpoTabela.appendChild(linha);
  });
}

carregarDashboard();
