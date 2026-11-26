
const corpoTabela = document.getElementById("corpoTabela");
const estadoVazio = document.getElementById("estadoVazio");
const campoBusca = document.getElementById("campoBusca");
const filtroTipo = document.getElementById("filtroTipo");
const filtroStatus = document.getElementById("filtroStatus");
const filtroMes = document.getElementById("filtroMes");
const caixaMensagem = document.getElementById("caixaMensagem");

const modalFundo = document.getElementById("modalFundo");
const modalTitulo = document.getElementById("modalTitulo");
const formTransacao = document.getElementById("formTransacao");

const transacaoId = document.getElementById("transacaoId");
const campoDescricao = document.getElementById("campoDescricao");
const campoValor = document.getElementById("campoValor");
const campoVencimento = document.getElementById("campoVencimento");
const campoTipo = document.getElementById("campoTipo");
const campoConta = document.getElementById("campoConta");
const campoCategoria = document.getElementById("campoCategoria");

const botaoNovaTransacao = document.getElementById("botaoNovaTransacao");
const botaoFecharModal = document.getElementById("botaoFecharModal");
const botaoCancelar = document.getElementById("botaoCancelar");
const botaoLimparFiltros = document.getElementById("botaoLimparFiltros");

let temporizadorBusca = null;
let contasDisponiveis = [];
let categoriasDisponiveis = [];

const ROTULOS = {
  RECEITA: "Receita",
  DESPESA: "Despesa",
  PENDENTE: "Pendente",
  PAGA: "Paga",
  ATRASADA: "Atrasada"
};


function escaparHTML(valor) {
  return String(valor ?? "").replace(/[&<>"']/g, caractere => ({
    "&": "&amp;",
    "<": "&lt;",
    ">": "&gt;",
    '"': "&quot;",
    "'": "&#39;"
  })[caractere]);
}

function formatarMoeda(valor) {
  return Number(valor).toLocaleString("pt-BR", {
    style: "currency",
    currency: "BRL"
  });
}

function formatarData(data) {
  if (!data) return "-";

  const [ano, mes, dia] = data.split("-");
  return `${dia}/${mes}/${ano}`;
}

function mostrarMensagem(texto, tipo = "sucesso") {
  caixaMensagem.textContent = texto;

  caixaMensagem.className =
    "mensagem " +
    (tipo === "erro" ? "mensagem-erro" : "mensagem-sucesso");

  clearTimeout(mostrarMensagem.timer);

  mostrarMensagem.timer = setTimeout(() => {
    caixaMensagem.classList.add("oculta");
  }, 4000);
}

async function carregarTransacoes() {
  const parametros = new URLSearchParams();

  const busca = campoBusca.value.trim();
  const tipo = filtroTipo.value;
  const status = filtroStatus.value;
  const mes = filtroMes.value;

  if (busca) parametros.set("busca", busca);
  if (tipo) parametros.set("tipo", tipo);
  if (status) parametros.set("status", status);
  if (mes) parametros.set("mes", mes);

  const query = parametros.toString()
    ? `?${parametros.toString()}`
    : "";

  corpoTabela.innerHTML =
    `<tr><td colspan="8">Carregando transações...</td></tr>`;

  estadoVazio.style.display = "none";

  try {
    const transacoes = await api.get(`/transacoes${query}`);
    renderizarTabela(transacoes);
  } catch (erro) {
    corpoTabela.innerHTML = "";
    mostrarMensagem(erro.message, "erro");
  }
}

function renderizarTabela(transacoes) {
  corpoTabela.innerHTML = "";

  if (!transacoes || transacoes.length === 0) {
    estadoVazio.style.display = "block";
    return;
  }

  estadoVazio.style.display = "none";

  // Ordenação por vencimento
  const ordenadas = [...transacoes].sort((a, b) =>
    a.vencimento.localeCompare(b.vencimento)
  );

  ordenadas.forEach(transacao => {
    const tipo = transacao.tipo;
    const status = transacao.status;

    const linha = document.createElement("tr");

    if (status === "ATRASADA") {
      linha.classList.add("linha-atrasada");
    }

    const classeValor =
      tipo === "RECEITA" ? "valor-positivo" : "valor-negativo";

    const classeStatus = {
      PAGA: "selo-paga",
      PENDENTE: "selo-pendente",
      ATRASADA: "selo-atrasada"
    }[status] || "";

    linha.innerHTML = `
      <td>${escaparHTML(transacao.descricao)}</td>

      <td class="${classeValor}">
        ${formatarMoeda(transacao.valor)}
      </td>

      <td>${formatarData(transacao.vencimento)}</td>

      <td>
        <span class="selo ${tipo === "RECEITA" ? "selo-receita" : "selo-despesa"}">
          ${ROTULOS[tipo] || escaparHTML(tipo)}
        </span>
      </td>

      <td>${escaparHTML(transacao.contaNome)}</td>

      <td>${escaparHTML(transacao.categoriaNome)}</td>

      <td>
        <span class="selo ${classeStatus}">
          ${ROTULOS[status] || escaparHTML(status)}
        </span>
      </td>

      <td class="tabela__acoes">

        ${status !== "PAGA" ? `
          <button
            type="button"
            class="btn-icone"
            data-acao="pagar"
            data-id="${transacao.id}"
            title="Marcar como paga"
          >✔️</button>
        ` : ""}

        <button
          type="button"
          class="btn-icone"
          data-acao="editar"
          data-id="${transacao.id}"
          title="Editar"
        >✏️</button>

        <button
          type="button"
          class="btn-icone"
          data-acao="excluir"
          data-id="${transacao.id}"
          title="Excluir"
        >🗑️</button>

      </td>
    `;

    corpoTabela.appendChild(linha);
  });
}
async function carregarContas() {
  contasDisponiveis = await api.get("/contas");

  campoConta.innerHTML =
    '<option value="" disabled selected hidden>Selecione uma conta</option>';

  contasDisponiveis.forEach(conta => {
    campoConta.innerHTML += `
      <option value="${conta.id}">
        ${escaparHTML(conta.nome)}
      </option>
    `;
  });
}

async function carregarCategorias(tipo = "") {
  const query = tipo
    ? `?tipo=${encodeURIComponent(tipo)}`
    : "";

  categoriasDisponiveis = await api.get(`/categorias${query}`);

  atualizarSelectCategorias();
}

function atualizarSelectCategorias() {
  const tipo = campoTipo.value;

  campoCategoria.innerHTML =
    '<option value="" disabled selected hidden>Selecione uma categoria</option>';

  const filtradas = categoriasDisponiveis.filter(categoria =>
    categoria.tipo === tipo
  );

  filtradas.forEach(categoria => {
    campoCategoria.innerHTML += `
      <option value="${categoria.id}">
        ${escaparHTML(categoria.nome)}
      </option>
    `;
  });
}
async function abrirModalCriacao() {
  formTransacao.reset();
  transacaoId.value = "";

  modalTitulo.textContent = "Nova transação";

  limparErrosFormulario();

  try {
    await Promise.all([
      carregarContas(),
      carregarCategorias()
    ]);

    modalFundo.classList.remove("oculto");
  } catch (erro) {
    mostrarMensagem(erro.message, "erro");
  }
}

async function abrirModalEdicao(id) {
  try {
    const [transacao] = await Promise.all([
      api.get(`/transacoes/${id}`),
      carregarContas(),
      carregarCategorias()
    ]);

    modalTitulo.textContent = "Editar transação";

    transacaoId.value = transacao.id;
    campoDescricao.value = transacao.descricao;
    campoValor.value = transacao.valor;
    campoVencimento.value = transacao.vencimento;
    campoTipo.value = transacao.tipo;

    await carregarCategorias(transacao.tipo);

    campoConta.value = transacao.contaId;
    campoCategoria.value = transacao.categoriaId;

    limparErrosFormulario();

    modalFundo.classList.remove("oculto");

  } catch (erro) {
    mostrarMensagem(erro.message, "erro");
  }
}

function fecharModal() {
  modalFundo.classList.add("oculto");
}

botaoNovaTransacao.addEventListener("click", abrirModalCriacao);
botaoFecharModal.addEventListener("click", fecharModal);
botaoCancelar.addEventListener("click", fecharModal);

modalFundo.addEventListener("click", evento => {
  if (evento.target === modalFundo) {
    fecharModal();
  }
});

campoBusca.addEventListener("input", () => {
  clearTimeout(temporizadorBusca);

  temporizadorBusca = setTimeout(carregarTransacoes, 400);
});

filtroTipo.addEventListener("change", carregarTransacoes);
filtroStatus.addEventListener("change", carregarTransacoes);
filtroMes.addEventListener("change", carregarTransacoes);

botaoLimparFiltros.addEventListener("click", () => {
  campoBusca.value = "";
  filtroTipo.value = "";
  filtroStatus.value = "";
  filtroMes.value = "";

  carregarTransacoes();
});

campoTipo.addEventListener("change", async () => {
  try {
    await carregarCategorias(campoTipo.value);
  } catch (erro) {
    mostrarMensagem(erro.message, "erro");
  }
});

const CAMPOS_ERRO = [
  ["campoDescricaoWrapper", "erroDescricao"],
  ["campoValorWrapper", "erroValor"],
  ["campoVencimentoWrapper", "erroVencimento"],
  ["campoTipoWrapper", "erroTipo"],
  ["campoContaWrapper", "erroConta"],
  ["campoCategoriaWrapper", "erroCategoria"]
];

function limparErrosFormulario() {
  CAMPOS_ERRO.forEach(([wrapper, erro]) => {
    document.getElementById(wrapper).classList.remove("erro");
    document.getElementById(erro).textContent = "";
  });
}

function marcarErro(wrapper, erro, mensagem) {
  document.getElementById(wrapper).classList.add("erro");
  document.getElementById(erro).textContent = mensagem;
}

function validarFormulario() {
  limparErrosFormulario();

  let valido = true;

  if (!campoDescricao.value.trim()) {
    marcarErro(
      "campoDescricaoWrapper",
      "erroDescricao",
      "Informe a descrição."
    );
    valido = false;
  }

  if (!campoValor.value || Number(campoValor.value) <= 0) {
    marcarErro(
      "campoValorWrapper",
      "erroValor",
      "Informe um valor maior que zero."
    );
    valido = false;
  }

  if (!campoVencimento.value) {
    marcarErro(
      "campoVencimentoWrapper",
      "erroVencimento",
      "Informe a data de vencimento."
    );
    valido = false;
  }

  if (!campoTipo.value) {
    marcarErro(
      "campoTipoWrapper",
      "erroTipo",
      "Selecione o tipo."
    );
    valido = false;
  }

  if (!campoConta.value) {
    marcarErro(
      "campoContaWrapper",
      "erroConta",
      "Selecione uma conta."
    );
    valido = false;
  }

  if (!campoCategoria.value) {
    marcarErro(
      "campoCategoriaWrapper",
      "erroCategoria",
      "Selecione uma categoria."
    );
    valido = false;
  }

  return valido;
}

formTransacao.addEventListener("submit", async evento => {
  evento.preventDefault();

  if (!validarFormulario()) return;

  const id = transacaoId.value;

  const corpo = {
    descricao: campoDescricao.value.trim(),
    valor: Number(campoValor.value),
    vencimento: campoVencimento.value,
    tipo: campoTipo.value,
    contaId: Number(campoConta.value),
    categoriaId: Number(campoCategoria.value)
  };

  try {
    if (id) {
      await api.put(`/transacoes/${id}`, corpo);

      mostrarMensagem(
        "Transação atualizada com sucesso.",
        "sucesso"
      );
    } else {
      await api.post("/transacoes", corpo);

      mostrarMensagem(
        "Transação criada com sucesso.",
        "sucesso"
      );
    }

    fecharModal();
    carregarTransacoes();

  } catch (erro) {
    mostrarMensagem(erro.message, "erro");
  }
});

async function excluirTransacao(id) {
  const confirmar = confirm(
    "Tem certeza que deseja excluir esta transação?"
  );

  if (!confirmar) return;

  try {
    await api.delete(`/transacoes/${id}`);

    mostrarMensagem(
      "Transação excluída com sucesso.",
      "sucesso"
    );

    carregarTransacoes();

  } catch (erro) {
    mostrarMensagem(erro.message, "erro");
  }
}

async function marcarComoPaga(id) {
  const confirmar = confirm(
    "Deseja realmente marcar esta transação como paga?"
  );

  if (!confirmar) return;

  try {
    await api.patch(`/transacoes/${id}/pagar`);

    mostrarMensagem(
      "Transação marcada como paga!",
      "sucesso"
    );

    carregarTransacoes();

  } catch (erro) {
    mostrarMensagem(erro.message, "erro");
  }
}


corpoTabela.addEventListener("click", evento => {
  const botao = evento.target.closest("button[data-acao]");

  if (!botao) return;

  const id = botao.dataset.id;
  const acao = botao.dataset.acao;

  if (acao === "editar") {
    abrirModalEdicao(id);
  }

  if (acao === "excluir") {
    excluirTransacao(id);
  }

  if (acao === "pagar") {
    marcarComoPaga(id);
  }
});

carregarTransacoes();
