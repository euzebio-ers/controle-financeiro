# Controle Financeiro

Sistema web de controle financeiro pessoal. Permite cadastrar contas, categorias e transações (receitas e despesas), acompanhar o saldo de cada conta e identificar o que está pago, pendente ou atrasado.

🎥 **Vídeo de demonstração:** _adicionar o link aqui_

---

## Funcionalidades

- **Dashboard:** saldo total, receitas e despesas pagas no mês atual e lista de transações atrasadas, com os dias de atraso.
- **Contas:** cadastro, edição, exclusão e busca por nome. O saldo atual é calculado a partir do saldo inicial e das transações pagas.
- **Categorias:** cadastro, edição, exclusão, busca por nome e filtro por tipo (receita ou despesa).
- **Transações:** cadastro, edição, exclusão, marcação como paga, busca por descrição e filtros por tipo, status e mês, que podem ser usados juntos.

### Regras de negócio

- O valor de uma transação deve ser maior que zero.
- A categoria precisa ter o mesmo tipo da transação (receita com categoria de receita, despesa com categoria de despesa).
- Não é permitido cadastrar contas ou categorias com nome repetido.
- Não é possível excluir uma conta ou categoria que tenha transações.
- Uma transação pendente com vencimento anterior a hoje aparece como **Atrasada**.
- Ao marcar uma transação como paga, o saldo da conta é atualizado.

---

## Tecnologias

| Camada | Tecnologia | Versão |
|---|---|---|
| Banco de dados | MySQL (via Docker) | 8.4 |
| Back-end | Java | 21 |
| Back-end | Spring Boot (Web MVC, Data JPA, Validation) | 4.2.0 |
| Back-end | Lombok e MapStruct | — |
| Back-end | Maven (incluído no projeto pelo Maven Wrapper) | — |
| Front-end | HTML, CSS e JavaScript puro | — |

---

## Pré-requisitos

- [Git](https://git-scm.com/)
- [Docker Desktop](https://www.docker.com/products/docker-desktop/) (para o MySQL; não é preciso instalar o MySQL na máquina)
- [JDK 21](https://adoptium.net/) — confira com `java -version`
- Uma forma de servir o front-end na **porta 5500**:
  - **Python 3** (já vem no macOS e na maioria das distribuições Linux), **ou**
  - **VS Code** com a extensão **Live Server**

Não é necessário instalar o Maven nem o Node.js.

---

## Como executar

São três partes rodando ao mesmo tempo: **banco**, **back-end** e **front-end**, nessa ordem. Use um terminal para cada uma.

### 1. Clonar o repositório

```bash
git clone https://github.com/euzebio-ers/controle-financeiro.git
cd controle-financeiro
```

### 2. Configurar a senha do banco

Copie o arquivo de exemplo e defina uma senha no novo arquivo `database/.env`:

```bash
cp database/.env.example database/.env
```

> No Windows (Prompt de Comando): `copy database\.env.example database\.env`

Abra `database/.env` e troque o valor:

```env
DB_ROOT_PASSWORD=sua_senha_aqui
```

Essa mesma senha é lida automaticamente pelo back-end. O arquivo `.env` está no `.gitignore` e não vai para o GitHub.

### 3. Criar e subir o banco (terminal 1)

Com o Docker Desktop aberto:

```bash
cd database
docker compose up -d
docker compose logs -f mysql
```

Na primeira execução, o MySQL cria o banco `controle_financeiro`, as tabelas e os dados de exemplo a partir do `script.sql`. Aguarde aparecer a linha:

```
ready for connections ... port: 3306
```

Depois aperte `Ctrl+C` para sair dos logs (o banco continua rodando).

> **Atenção:** o `script.sql` só é executado quando o banco é criado pela primeira vez. Para recriar o banco do zero (por exemplo, depois de alterar o script), use:
> ```bash
> docker compose down -v
> docker compose up -d
> ```
> O `-v` apaga todos os dados.

### 4. Rodar o back-end (terminal 2)

A partir da raiz do projeto:

```bash
cd backend
./mvnw spring-boot:run
```

> No Windows: `mvnw.cmd spring-boot:run`

O back-end precisa ser executado **de dentro da pasta `backend`**, porque é de lá que ele encontra o arquivo `../database/.env` com a senha. Ao rodar pela IDE, configure o *working directory* para a pasta `backend`.

Na primeira vez o Maven baixa as dependências, o que pode demorar alguns minutos. O back-end está pronto quando aparecer `Started BackendApplication`. Ele roda na porta **8080**.

### 5. Rodar o front-end (terminal 3)

O front-end precisa rodar na **porta 5500**, que é a origem liberada no CORS do back-end.

**Opção A — Python:**

```bash
cd frontend
python3 -m http.server 5500
```

**Opção B — VS Code:** abra a pasta `frontend`, clique com o botão direito em `pages/dashboard.html` e escolha **Open with Live Server**.

Não abra os arquivos HTML com dois cliques: pelo endereço `file://` o navegador bloqueia as chamadas para a API.

---

## Endereços de acesso

| O quê | Endereço |
|---|---|
| Sistema (Dashboard) | http://localhost:5500/pages/dashboard.html |
| Contas | http://localhost:5500/pages/contas.html |
| Categorias | http://localhost:5500/pages/categorias.html |
| Transações | http://localhost:5500/pages/transacoes.html |
| API | http://localhost:8080/api |
| Banco MySQL | `localhost:3306`, banco `controle_financeiro`, usuário `root` |

---

## Endpoints da API

Todas as rotas começam com `/api`. Os erros retornam no formato `{ "mensagem": "..." }`.

| Método | Rota | Descrição |
|---|---|---|
| GET | `/contas?busca=` | Lista as contas, com busca opcional por nome |
| GET | `/contas/{id}` | Busca uma conta |
| POST | `/contas` | Cria uma conta |
| PUT | `/contas/{id}` | Edita uma conta |
| DELETE | `/contas/{id}` | Exclui uma conta (bloqueado se tiver transações) |
| GET | `/categorias?busca=&tipo=` | Lista as categorias, com busca e filtro por tipo opcionais |
| GET | `/categorias/{id}` | Busca uma categoria |
| POST | `/categorias` | Cria uma categoria |
| PUT | `/categorias/{id}` | Edita uma categoria |
| DELETE | `/categorias/{id}` | Exclui uma categoria (bloqueado se tiver transações) |
| GET | `/transacoes?busca=&tipo=&status=&mes=` | Lista as transações, com filtros opcionais |
| GET | `/transacoes/{id}` | Busca uma transação |
| POST | `/transacoes` | Cria uma transação (sempre como pendente) |
| PUT | `/transacoes/{id}` | Edita uma transação |
| PATCH | `/transacoes/{id}/pagar` | Marca a transação como paga |
| DELETE | `/transacoes/{id}` | Exclui uma transação |
| GET | `/dashboard` | Resumo: saldo total, receitas e despesas do mês e atrasadas |

Valores aceitos nos filtros:

- `tipo`: `RECEITA` ou `DESPESA`
- `status`: `PENDENTE`, `PAGA` ou `ATRASADA`
- `mes`: formato `AAAA-MM` (exemplo: `2026-11`)
- Tipos de conta: `CORRENTE`, `POUPANCA` ou `CARTEIRA`

Exemplo de corpo para criar uma transação:

```json
{
  "descricao": "Conta de Luz",
  "valor": 182.75,
  "vencimento": "2026-12-10",
  "tipo": "DESPESA",
  "contaId": 1,
  "categoriaId": 6
}
```

---

## Estrutura de pastas

```
controle-financeiro/
├── backend/                  API REST em Spring Boot
│   ├── pom.xml
│   ├── mvnw / mvnw.cmd       Maven Wrapper
│   └── src/main/
│       ├── java/com/controlefinanceiro/
│       │   ├── config/       Configuração de CORS
│       │   ├── controller/   Rotas da API
│       │   ├── dto/          Objetos de entrada e saída da API
│       │   ├── enums/        Tipos e status
│       │   ├── exception/    Tratamento de erros
│       │   ├── mapper/       Conversão entre entidades e DTOs
│       │   ├── model/        Entidades JPA
│       │   ├── repository/   Acesso ao banco
│       │   └── service/      Regras de negócio
│       └── resources/
│           └── application.yaml
├── database/
│   ├── script.sql            Criação das tabelas e dados de exemplo
│   ├── docker-compose.yml    Container do MySQL
│   └── .env.example          Modelo do arquivo com a senha do banco
├── frontend/
│   ├── css/                  Estilos (global.css e estilos por tela)
│   ├── js/                   api.js, menu.js e um script por tela
│   └── pages/                dashboard, contas, categorias e transações
└── README.md
```

---

## Problemas comuns

| Problema | Solução |
|---|---|
| "Não foi possível conectar ao servidor" no front | Verifique se o back-end está rodando na porta 8080 e se o front está em `http://localhost:5500` (não em `file://`). |
| Back-end não conecta ao banco | Confira se o container está rodando (`docker ps`), se existe o arquivo `database/.env` e se o back-end foi iniciado de dentro da pasta `backend`. |
| `permission denied` ao rodar `./mvnw` | Rode `chmod +x mvnw` dentro da pasta `backend`. |
| Mudança no CSS ou JS não aparece | Recarregue a página ignorando o cache: **Cmd + Shift + R** (macOS) ou **Ctrl + Shift + R** (Windows/Linux). |
| Alterei o `script.sql` e nada mudou | Recrie o banco com `docker compose down -v` e `docker compose up -d` dentro da pasta `database`. |
| Porta 3306 já está em uso | Pare o MySQL instalado na máquina, ou outro container que esteja usando a porta, e suba o banco novamente. |
