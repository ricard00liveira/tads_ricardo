# 📚 Sistema de Biblioteca - TADS Ricardo

Este é um sistema de gerenciamento de biblioteca desenvolvido para fins acadêmicos. O projeto utiliza **Java 17** com **Spring Boot**, **JPA/Hibernate** e **banco de dados MariaDB**, seguindo o padrão MVC e utilizando herança nas entidades.

---

## 🚀 Tecnologias Utilizadas

- Java 17
- Spring Boot 3.2+
- Spring Data JPA
- Spring Security
- Spring Web
- MariaDB
- Lombok
- Maven

---

## 🏗️ Estrutura de Entidades

### 🧍 Cliente (abstract)
- Subclasses:
    - `Aluno`
    - `PaiDeAluno`

### 📚 Exemplar (abstract)
- Subclasses:
    - `Livro`
    - `Artigo`
    - `Periodico`

### 🔁 Emprestimo
- Relaciona Cliente + Exemplar
- Possui `dataEmprestimo` e `dataDevolucao`

---

## 🛠️ Configurações (application.properties)

```properties
spring.datasource.url=jdbc:mariadb://localhost:3306/biblioteca
spring.jpa.hibernate.ddl-auto=update
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.MariaDBDialect
spring.data.rest.basePath=/api
