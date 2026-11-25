const API_URL = "http://localhost:8080/api"; // trocar pelo mock enquanto o back não sai

async function requisicao(metodo, caminho, corpo) {
  const opcoes = { method: metodo, headers: { "Content-Type": "application/json" } };
  if (corpo) opcoes.body = JSON.stringify(corpo);

  let resposta;
  try {
    resposta = await fetch(API_URL + caminho, opcoes);
  } catch {
    throw new Error("Não foi possível conectar ao servidor.");
  }

  if (resposta.status === 204) return null;
  const dados = await resposta.json().catch(() => ({}));
  if (!resposta.ok) throw new Error(dados.mensagem || "Erro inesperado.");
  return dados;
}

const api = {
  get:    (c)    => requisicao("GET", c),
  post:   (c, b) => requisicao("POST", c, b),
  put:    (c, b) => requisicao("PUT", c, b),
  patch:  (c)    => requisicao("PATCH", c),
  delete: (c)    => requisicao("DELETE", c),
};