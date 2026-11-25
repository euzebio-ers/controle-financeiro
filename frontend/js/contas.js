/* ===================================================================
   CONTAS.JS — Controle Financeiro
   Responsabilidade: Pessoa 3

   Depende de api.js (window.api) já carregado antes deste arquivo.
   Segue o contrato:
     GET    /api/contas?busca=texto&tipo=CORRENTE
     POST   /api/contas        body: { nome, tipo, saldoInicial }
     PUT    /api/contas/{id}   body: { nome, tipo, saldoInicial }
     DELETE /api/contas/{id}
   =================================================================== */

// ---- Referências dos elementos da página ----
const corpoTabela = document.getElementById("corpoTabela");
const estadoVazio = document.getElementById("estadoVazio");
const campoBusca = document.getElementById("campoBusca");
const caixaMensagem = document.getElementById("caixaMensagem");

const modalFundo = document.getElementById("modalFundo");
const modalTitulo = document.getElementById("modalTitulo");
const formConta = document.getElementById("formConta");
const contaIdInput = document.getElementById("contaId");
const campoNome = document.getElementById("campoNome");
const campoTipo = document.getElementById("campoTipo");
const campoSaldo = document.getElementById("campoSaldo");

const botaoNovaConta = document.getElementById("botaoNovaConta");
const botaoFecharModal = document.getElementById("botaoFecharModal");
const botaoCancelar = document.getElementById("botaoCancelar");

let temporizadorBusca = null;

const ROTULOS_TIPO = {
  CORRENTE: "Conta corrente",
  POUPANCA: "Poupança",
  CARTEIRA: "Carteira",
};

// ---------------------------------------------------------------
// FORMATAÇÃO
// ---------------------------------------------------------------
function formatarMoeda(valor) {
  return Number(valor).toLocaleString("pt-BR", {
    style: "currency",
    currency: "BRL",
  });
}

function classeSaldo(valor) {
  return Number(valor) < 0 ? "valor-negativo" : "valor-positivo";
}

// ---------------------------------------------------------------
// MENSAGENS
// ---------------------------------------------------------------
function mostrarMensagem(texto, tipo) {
  caixaMensagem.textContent = texto;
  caixaMensagem.className = "mensagem " + (tipo === "erro" ? "mensagem-erro" : "mensagem-sucesso");

  clearTimeout(mostrarMensagem._timer);
  mostrarMensagem._timer = setTimeout(() => {
    caixaMensagem.classList.add("oculta");
  }, 4000);
}

// ---------------------------------------------------------------
// LISTAGEM
// ---------------------------------------------------------------
async function carregarContas() {
  const busca = campoBusca.value.trim();

  const parametros = new URLSearchParams();
  if (busca) parametros.set("busca", busca);

  const query = parametros.toString() ? `?${parametros.toString()}` : "";

  corpoTabela.innerHTML = `<tr><td colspan="5">Carregando...</td></tr>`;
  estadoVazio.style.display = "none";

  try {
    const contas = await api.get(`/contas${query}`);
    renderizarTabela(contas);
  } catch (erro) {
    corpoTabela.innerHTML = "";
    mostrarMensagem(erro.message, "erro");
  }
}

function renderizarTabela(contas) {
  corpoTabela.innerHTML = "";

  if (!contas || contas.length === 0) {
    estadoVazio.style.display = "block";
    return;
  }
  estadoVazio.style.display = "none";

  contas.forEach((conta) => {
    const rotuloTipo = ROTULOS_TIPO[conta.tipo] || conta.tipo;

    const linha = document.createElement("tr");
    linha.innerHTML = `
      <td>${conta.nome}</td>
      <td><span class="selo selo-neutro">${rotuloTipo}</span></td>
      <td class="${classeSaldo(conta.saldoInicial)}">${formatarMoeda(conta.saldoInicial)}</td>
      <td class="${classeSaldo(conta.saldoAtual)}">${formatarMoeda(conta.saldoAtual)}</td>
      <td class="tabela__acoes">
        <button class="btn-icone" title="Editar" onclick="abrirModalEdicao(${conta.id}, '${conta.nome.replace(/'/g, "\\'")}', '${conta.tipo}', ${conta.saldoInicial})">✏️</button>
        <button class="btn-icone" title="Excluir" onclick="excluirConta(${conta.id})">🗑️</button>
      </td>
    `;
    corpoTabela.appendChild(linha);
  });
}

// ---------------------------------------------------------------
// BUSCA (com debounce) E FILTRO
// ---------------------------------------------------------------
campoBusca.addEventListener("input", () => {
  clearTimeout(temporizadorBusca);
  temporizadorBusca = setTimeout(carregarContas, 400);
});


// ---------------------------------------------------------------
// MODAL: abrir / fechar
// ---------------------------------------------------------------
function abrirModalCriacao() {
  modalTitulo.textContent = "Nova conta";
  contaIdInput.value = "";
  campoNome.value = "";
  campoTipo.value = "";
  campoSaldo.value = "";
  limparErrosFormulario();
  modalFundo.classList.remove("oculto");
}

function abrirModalEdicao(id, nome, tipo, saldoInicial) {
  modalTitulo.textContent = "Editar conta";
  contaIdInput.value = id;
  campoNome.value = nome;
  campoTipo.value = tipo;
  campoSaldo.value = saldoInicial;
  limparErrosFormulario();
  modalFundo.classList.remove("oculto");
}

function fecharModal() {
  modalFundo.classList.add("oculto");
}

botaoNovaConta.addEventListener("click", abrirModalCriacao);
botaoFecharModal.addEventListener("click", fecharModal);
botaoCancelar.addEventListener("click", fecharModal);

modalFundo.addEventListener("click", (evento) => {
  if (evento.target === modalFundo) fecharModal();
});

// ---------------------------------------------------------------
// VALIDAÇÃO DO FORMULÁRIO (front)
// ---------------------------------------------------------------
function limparErrosFormulario() {
  document.getElementById("campoNomeWrapper").classList.remove("erro");
  document.getElementById("campoTipoWrapper").classList.remove("erro");
  document.getElementById("campoSaldoWrapper").classList.remove("erro");
  document.getElementById("erroNome").textContent = "";
  document.getElementById("erroTipo").textContent = "";
  document.getElementById("erroSaldo").textContent = "";
}

function validarFormulario() {
  limparErrosFormulario();
  let valido = true;

  if (!campoNome.value.trim()) {
    document.getElementById("campoNomeWrapper").classList.add("erro");
    document.getElementById("erroNome").textContent = "O nome é obrigatório.";
    valido = false;
  }

  if (!campoTipo.value) {
    document.getElementById("campoTipoWrapper").classList.add("erro");
    document.getElementById("erroTipo").textContent = "Selecione o tipo.";
    valido = false;
  }

  if (campoSaldo.value === "") {
    document.getElementById("campoSaldoWrapper").classList.add("erro");
    document.getElementById("erroSaldo").textContent = "Informe o saldo inicial.";
    valido = false;
  }

  return valido;
}

// ---------------------------------------------------------------
// SALVAR (criar ou editar)
// ---------------------------------------------------------------
formConta.addEventListener("submit", async (evento) => {
  evento.preventDefault();

  if (!validarFormulario()) return;

  const id = contaIdInput.value;
  const corpo = {
    nome: campoNome.value.trim(),
    tipo: campoTipo.value,
    saldoInicial: Number(campoSaldo.value),
  };

  try {
    if (id) {
      await api.put(`/contas/${id}`, corpo);
      mostrarMensagem("Conta atualizada com sucesso.", "sucesso");
    } else {
      await api.post("/contas", corpo);
      mostrarMensagem("Conta criada com sucesso.", "sucesso");
    }
    fecharModal();
    carregarContas();
  } catch (erro) {
    mostrarMensagem(erro.message, "erro");
  }
});

// ---------------------------------------------------------------
// EXCLUIR
// ---------------------------------------------------------------
async function excluirConta(id) {
  const confirmar = confirm("Tem certeza que deseja excluir esta conta?");
  if (!confirmar) return;

  try {
    await api.delete(`/contas/${id}`);
    mostrarMensagem("Conta excluída com sucesso.", "sucesso");
    carregarContas();
  } catch (erro) {
    // Se a conta tiver transações vinculadas, o back devolve 409
    // e a mensagem exata aparece aqui.
    mostrarMensagem(erro.message, "erro");
  }
}

// ---------------------------------------------------------------
// INICIALIZAÇÃO
// ---------------------------------------------------------------
carregarContas();