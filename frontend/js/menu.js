function montarMenu(paginaAtual) {
  const links = [
    { nome: "Dashboard", pagina: "dashboard", href: "dashboard.html" },
    { nome: "Contas", pagina: "contas", href: "contas.html" },
    { nome: "Categorias", pagina: "categorias", href: "categorias.html" },
    { nome: "Transações", pagina: "transacoes", href: "transacoes.html" },
  ];

  const container = document.getElementById("menu-lateral");
  if (!container) return;

  const linksHtml = links
    .map((link) => {
      const classeAtiva = link.pagina === paginaAtual ? "ativo" : "";
      return `<a class="menu-lateral__link ${classeAtiva}" href="${link.href}">${link.nome}</a>`;
    })
    .join("");

  container.innerHTML = `
    <button class="menu-botao-abrir" id="botaoAbrirMenu">☰ Menu</button>
    <nav class="menu-lateral" id="menuLateral">
      <div class="menu-lateral__titulo">Controle Financeiro</div>
      <div class="menu-lateral__links">
        ${linksHtml}
      </div>
    </nav>
  `;

  const botao = document.getElementById("botaoAbrirMenu");
  const menu = document.getElementById("menuLateral");
  botao.addEventListener("click", () => {
    menu.classList.toggle("aberto");
  });
}