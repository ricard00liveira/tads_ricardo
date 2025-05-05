# 📚 Sistema de Gerenciamento de Biblioteca – TADS Ricardo

Este sistema foi desenvolvido como projeto acadêmico para o curso de Tecnologia em Desenvolvimento de Sistemas para Internet/IFSul Campus Pelotas. Ele permite o controle de empréstimos de materiais como livros, periódicos e artigos científicos, com autenticação de usuários e interface RESTful.

---

## 🚀 Tecnologias e Frameworks

- **Java 17**
- **Spring Boot 3.4.4**
- **Spring Data JPA**
- **Spring Security**
- **Spring Web + Spring Data REST**
- **JWT (Java Web Token)** – `com.auth0:java-jwt`
- **Lombok**
- **MariaDB**
- **Maven**

---

## 🧩 Principais Entidades

### 👤 Cliente (classe abstrata)
- Subclasses:
  - `Aluno`
  - `PaiDeAluno`
- Controladores, DTOs e serviços específicos para cadastro, atualização (PATCH/PUT) e listagem.

### 📘 Exemplar (classe abstrata)
- Subclasses:
  - `Livro`
  - `Artigo`
  - `Periódico`
- Representam os materiais disponíveis na biblioteca.

### 🔄 Empréstimo
- Relaciona `Cliente` e `Exemplar`
- Campos como `dataEmprestimo` e `dataDevolucao` gerenciam os prazos de devolução.

### 🔐 Autenticação
- Implementada via **JWT**
- Classes dedicadas: `AutenticacaoController`, `AutenticacaoService`, `UsuarioAutenticacaoDTO`

---

## 📦 Estrutura de Pacotes

```
tads_ricardo_bibli/
├── api/
│   ├── aluno/
│   ├── artigo/
│   ├── autenticacao/
│   ├── cliente/
│   ├── emprestimo/
│   ├── exemplar/
│   ├── infra/exception/
│   └── periodico/
└── TadsRicardoBibliApplication.java
```

---

## ⚙️ Configuração

### application.properties

```properties
spring.datasource.url=jdbc:mariadb://localhost:3306/biblioteca
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
```

> Altere `username` e `password` conforme suas configurações locais do MariaDB.

---

## ▶️ Como Executar

1. Clone o repositório:

```bash
git clone https://github.com/ricard00liveira/tads_ricardo.git
cd tads_ricardo/tads_ricardo_bibli
```

2. Configure o banco de dados MariaDB e atualize o `application.properties`.

3. Execute com Maven:

```bash
./mvnw spring-boot:run
```

---

## 🧪 Testes

- A aplicação possui dependência de testes com `spring-boot-starter-test`.
- Para rodar testes:

```bash
./mvnw test
```

---

## 📄 Licença

Este projeto está sob a licença MIT. Consulte o arquivo `LICENSE` para mais detalhes.

---

## 🤝 Contribuições

Contribuições, sugestões e melhorias são bem-vindas. Abra uma issue ou envie um pull request!

---

Desenvolvido por Ricardo Oliveira – [GitHub](https://github.com/ricard00liveira)
