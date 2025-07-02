# Sistema de Templates (sistemplate)

Este projeto é uma API REST para gerenciamento de templates e geração de documentos PDF, desenvolvida com Quarkus, o Supersonic Subatomic Java Framework.

Se você quiser aprender mais sobre Quarkus, visite o site: <https://quarkus.io/>.

## Pré-requisitos

- Java 17+
- Maven 3.8+
- Docker e Docker Compose (para executar o MongoDB)

## Configuração do Banco de Dados

O projeto utiliza MongoDB como banco de dados. Para iniciar o MongoDB usando Docker Compose:

```bash
docker-compose up -d
```

Isso irá iniciar um container MongoDB na porta 27017 com o banco de dados `sistemplate`.

## Executando a aplicação em modo de desenvolvimento

Você pode executar a aplicação em modo de desenvolvimento que permite live coding usando:

```bash
./mvnw quarkus:dev
```

> **_NOTA:_** O Quarkus agora vem com uma Dev UI, que está disponível apenas em modo de desenvolvimento em <http://localhost:8080/q/dev/>.

## Acessando a Documentação da API (Swagger)

A documentação da API está disponível através do Swagger UI. Com a aplicação em execução, acesse:

- **Swagger UI**: <http://localhost:8080/q/swagger-ui/>
- **OpenAPI Spec (JSON)**: <http://localhost:8080/q/openapi>

### Endpoints Principais

A API oferece os seguintes endpoints principais:

#### Templates (`/api/templates`)
- `GET /api/templates` - Lista todos os templates
- `GET /api/templates/{name}` - Busca um template pelo nome
- `GET /api/templates/type/{type}` - Lista templates por tipo de documento
- `GET /api/templates/search?name={pattern}` - Busca templates por parte do nome
- `POST /api/templates` - Cria um novo template
- `PUT /api/templates/{name}` - Atualiza um template existente
- `DELETE /api/templates/{name}` - Remove um template
- `POST /api/templates/{name}/generate-pdf` - Gera um PDF a partir de um template

#### Documentos (`/documents`)
- `POST /documents/generate` - Gera documentos a partir de templates

### Tipos de Documento Suportados

- `PDF` - Documentos PDF
- `EMAIL` - Templates para email (futuro)
- `SMS` - Templates para SMS (futuro)

### Exemplo de Template

```json
{
  "name": "carta-comercial",
  "type": "PDF",
  "description": "Template para cartas comerciais",
  "content": "<html><body><h1>{header}</h1><p>Caro {recipient},</p><p>{content}</p><p>Data: {date}</p><p>Assinatura: {signature}</p></body></html>"
}
```

### Exemplo de Geração de PDF

```json
{
  "templateName": "carta-comercial",
  "metadata": {
    "header": "Proposta Comercial",
    "recipient": "João Silva",
    "content": "Temos o prazer de apresentar nossa proposta...",
    "date": "2024-01-15",
    "signature": "Maria Santos"
  }
}
```


## Empacotamento e execução da aplicação

A aplicação pode ser empacotada usando:

```bash
./mvnw package
```

Isso produz o arquivo `quarkus-run.jar` no diretório `target/quarkus-app/`.
Note que não é um _über-jar_ pois as dependências são copiadas para o diretório `target/quarkus-app/lib/`.

A aplicação agora pode ser executada usando `java -jar target/quarkus-app/quarkus-run.jar`.

Se você quiser construir um _über-jar_, execute o seguinte comando:

```bash
./mvnw package -Dquarkus.package.jar.type=uber-jar
```

A aplicação, empacotada como um _über-jar_, agora pode ser executada usando `java -jar target/*-runner.jar`.

## Criando um executável nativo

Você pode criar um executável nativo usando:

```bash
./mvnw package -Dnative
```

Ou, se você não tiver o GraalVM instalado, pode executar a construção do executável nativo em um container usando:

```bash
./mvnw package -Dnative -Dquarkus.native.container-build=true
```

Você pode então executar seu executável nativo com: `./target/sistemplate-1.0.0-SNAPSHOT-runner`

Se você quiser aprender mais sobre a construção de executáveis nativos, consulte <https://quarkus.io/guides/maven-tooling>.

## Tecnologias Utilizadas

- **Quarkus** - Framework Java supersônico e subatômico
- **MongoDB** - Banco de dados NoSQL
- **Panache** - Camada de persistência simplificada
- **Qute** - Engine de templates
- **iText** - Biblioteca para geração de PDF
- **SmallRye OpenAPI** - Documentação automática da API
- **Lombok** - Redução de boilerplate code

## Guias Relacionados

- REST ([guia](https://quarkus.io/guides/rest)): Uma implementação Jakarta REST utilizando processamento em tempo de construção e Vert.x
- MongoDB client ([guia](https://quarkus.io/guides/mongodb)): Conecte-se ao MongoDB em estilo imperativo ou reativo
- Qute ([guia](https://quarkus.io/guides/qute)): Engine de templates type-safe