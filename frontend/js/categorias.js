const corpoTabela = document.getElementById("corpoTabela");
const estadoVazio = document.getElementById("estadoVazio");
const campoBusca = document.getElementById("campoBusca");
const filtroTipo = document.getElementById("filtroTipo");
const caixaMensagem = document.getElementById("caixaMensagem");

const modalFundo = document.getElementById("modalFundo");
const modalTitulo = document.getElementById("modalTitulo");
const formCategoria = document.getElementById("formCategoria");
const categoriaIdInput = document.getElementById("categoriaId");
const campoNome = document.getElementById("campoNome");
const campoTipo = document.getElementById("campoTipo");

const botaoNovaCategoria = document.getElementById("botaoNovaCategoria");
const botaoFecharModal = document.getElementById("botaoFecharModal");
const botaoCancelar = document.getElementById("botaoCancelar");

let temporizadorBusca = null;

function mostrarMensagem(texto, tipo) {
  caixaMensagem.textContent = texto;
  caixaMensagem.className = "mensagem " + (tipo === "erro" ? "mensagem-erro" : "mensagem-sucesso");

  // some sozinha depois de alguns segundos
  clearTimeout(mostrarMensagem._timer);
  mostrarMensagem._timer = setTimeout(() => {
    caixaMensagem.classList.add("oculta");
  }, 4000);
}

async function carregarCategorias() {
  const busca = campoBusca.value.trim();
  const tipo = filtroTipo.value;

  const parametros = new URLSearchParams();
  if (busca) parametros.set("busca", busca);
  if (tipo) parametros.set("tipo", tipo);

  const query = parametros.toString() ? `?${parametros.toString()}` : "";

  corpoTabela.innerHTML = `<tr><td colspan="3">Carregando...</td></tr>`;
  estadoVazio.style.display = "none";

  try {
    const categorias = await api.get(`/categorias${query}`);
    renderizarTabela(categorias);
  } catch (erro) {
    corpoTabela.innerHTML = "";
    mostrarMensagem(erro.message, "erro");
  }
}

function renderizarTabela(categorias) {
  corpoTabela.innerHTML = "";

  if (!categorias || categorias.length === 0) {
    estadoVazio.style.display = "block";
    return;
  }
  estadoVazio.style.display = "none";

  categorias.forEach((categoria) => {
    const seloClasse = categoria.tipo === "RECEITA" ? "selo-receita" : "selo-despesa";
    const seloTexto = categoria.tipo === "RECEITA" ? "Receita" : "Despesa";

    const linha = document.createElement("tr");
    linha.innerHTML = `
      <td>${categoria.nome}</td>
      <td><span class="selo ${seloClasse}">${seloTexto}</span></td>
      <td class="tabela__acoes">
        <button class="btn-icone" title="Editar" onclick="abrirModalEdicao(${categoria.id}, '${categoria.nome.replace(/'/g, "\\'")}', '${categoria.tipo}')">✏️</button>
        <button class="btn-icone" title="Excluir" onclick="excluirCategoria(${categoria.id})">🗑️</button>
      </td>
    `;
    corpoTabela.appendChild(linha);
  });
}

campoBusca.addEventListener("input", () => {
  clearTimeout(temporizadorBusca);
  temporizadorBusca = setTimeout(carregarCategorias, 400);
});

filtroTipo.addEventListener("change", carregarCategorias);

function abrirModalCriacao() {
  modalTitulo.textContent = "Nova categoria";
  categoriaIdInput.value = "";
  campoNome.value = "";
  campoTipo.value = "";
  limparErrosFormulario();
  modalFundo.classList.remove("oculto");
}

function abrirModalEdicao(id, nome, tipo) {
  modalTitulo.textContent = "Editar categoria";
  categoriaIdInput.value = id;
  campoNome.value = nome;
  campoTipo.value = tipo;
  limparErrosFormulario();
  modalFundo.classList.remove("oculto");
}

function fecharModal() {
  modalFundo.classList.add("oculto");
}

botaoNovaCategoria.addEventListener("click", abrirModalCriacao);
botaoFecharModal.addEventListener("click", fecharModal);
botaoCancelar.addEventListener("click", fecharModal);

modalFundo.addEventListener("click", (evento) => {
  if (evento.target === modalFundo) fecharModal();
});

function limparErrosFormulario() {
  document.getElementById("campoNomeWrapper").classList.remove("erro");
  document.getElementById("campoTipoWrapper").classList.remove("erro");
  document.getElementById("erroNome").textContent = "";
  document.getElementById("erroTipo").textContent = "";
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

  return valido;
}

formCategoria.addEventListener("submit", async (evento) => {
  evento.preventDefault();

  if (!validarFormulario()) return;

  const id = categoriaIdInput.value;
  const corpo = {
    nome: campoNome.value.trim(),
    tipo: campoTipo.value,
  };

  try {
    if (id) {
      await api.put(`/categorias/${id}`, corpo);
      mostrarMensagem("Categoria atualizada com sucesso.", "sucesso");
    } else {
      await api.post("/categorias", corpo);
      mostrarMensagem("Categoria criada com sucesso.", "sucesso");
    }
    fecharModal();
    carregarCategorias();
  } catch (erro) {
    // Erro 409 (nome duplicado) ou qualquer outro erro da API
    // aparece aqui, com a mensagem exata que o back mandou.
    mostrarMensagem(erro.message, "erro");
  }
});

async function excluirCategoria(id) {
  const confirmar = confirm("Tem certeza que deseja excluir esta categoria?");
  if (!confirmar) return;

  try {
    await api.delete(`/categorias/${id}`);
    mostrarMensagem("Categoria excluída com sucesso.", "sucesso");
    carregarCategorias();
  } catch (erro) {

    mostrarMensagem(erro.message, "erro");
  }
}

carregarCategorias();