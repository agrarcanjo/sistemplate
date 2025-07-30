# Sistema de Templates (sistemplate)

Este projeto é uma API REST para gerenciamento de templates e geração de documentos PDF, desenvolvida com Quarkus, o Supersonic Subatomic Java Framework.

Se você quiser aprender mais sobre Quarkus, visite o site: <https://quarkus.io/>.

## Pré-requisitos

- Java 21+
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

A documentação da API está disponível em:
- **Swagger UI**: <http://localhost:8080/q/swagger-ui/>
- **OpenAPI Spec**: <http://localhost:8080/q/openapi>

## Funcionalidades

### 1. Gerenciamento de Templates

#### Criar Template
**POST** `/api/templates`

Exemplo de requisição completa com template avançado:

```json
{
  "name": "relatorio-vendas",
  "type": "PDF",
  "description": "Template para relatório de vendas com gráficos e condicionais",
  "content": "<!DOCTYPE html><html><head><meta charset=\"UTF-8\"><title>Relatório de Vendas</title><style>body{font-family:Arial,sans-serif;margin:0;padding:20px;background-color:#f5f5f5}.container{max-width:800px;margin:0 auto;background:white;padding:30px;border-radius:8px;box-shadow:0 2px 10px rgba(0,0,0,0.1)}.header{text-align:center;border-bottom:3px solid #007bff;padding-bottom:20px;margin-bottom:30px}.logo{max-width:150px;height:auto;margin-bottom:10px}.title{color:#333;font-size:28px;margin:10px 0}.subtitle{color:#666;font-size:16px;margin:5px 0}.info-section{margin:20px 0;padding:15px;background:#f8f9fa;border-left:4px solid #007bff;border-radius:4px}.section-title{color:#007bff;font-size:18px;font-weight:bold;margin-bottom:10px}.metrics{display:flex;justify-content:space-around;margin:20px 0}.metric-card{text-align:center;padding:15px;background:linear-gradient(135deg,#007bff,#0056b3);color:white;border-radius:8px;min-width:120px}.metric-value{font-size:24px;font-weight:bold}.metric-label{font-size:12px;opacity:0.9}.table{width:100%;border-collapse:collapse;margin:20px 0}.table th,.table td{padding:12px;text-align:left;border-bottom:1px solid #ddd}.table th{background-color:#007bff;color:white;font-weight:bold}.table tr:hover{background-color:#f5f5f5}.status-success{color:#28a745;font-weight:bold}.status-warning{color:#ffc107;font-weight:bold}.status-danger{color:#dc3545;font-weight:bold}.chart-container{text-align:center;margin:20px 0;padding:15px;background:#f8f9fa;border-radius:8px}.chart-image{max-width:100%;height:auto;border-radius:4px}.footer{margin-top:40px;padding-top:20px;border-top:2px solid #eee;text-align:center;color:#666;font-size:14px}.signature-section{margin-top:30px;display:flex;justify-content:space-between}.signature-box{text-align:center;min-width:200px}.signature-line{border-top:1px solid #333;margin-top:40px;padding-top:5px}@media print{body{background:white}.container{box-shadow:none;padding:20px}.metrics{flex-direction:column}.metric-card{margin:5px 0}}</style></head><body><div class=\"container\"><div class=\"header\">{#if company.logo}<img src=\"{company.logo}\" alt=\"Logo\" class=\"logo\">{/if}<h1 class=\"title\">{title}</h1><p class=\"subtitle\">{subtitle}</p><p>Período: {period} | Data: {date}</p></div>{#if executiveSummary}<div class=\"info-section\"><h2 class=\"section-title\">Resumo Executivo</h2><p>{executiveSummary}</p></div>{/if}{#if metrics}<div class=\"metrics\">{#for metric in metrics}<div class=\"metric-card\"><div class=\"metric-value\">{metric.value}</div><div class=\"metric-label\">{metric.label}</div></div>{/for}</div>{/if}{#if salesData}<div class=\"info-section\"><h2 class=\"section-title\">Dados de Vendas</h2><table class=\"table\"><thead><tr><th>Produto</th><th>Quantidade</th><th>Valor Unitário</th><th>Total</th><th>Status</th></tr></thead><tbody>{#for item in salesData}<tr><td>{item.produto}</td><td>{item.quantidade}</td><td>R$ {item.valorUnitario}</td><td>R$ {item.total}</td><td class=\"{#if item.status == 'Excelente'}status-success{#elseif item.status == 'Bom'}status-warning{#else}status-danger{/if}\">{item.status}</td></tr>{/for}</tbody></table></div>{/if}{#if chartImage}<div class=\"chart-container\"><h2 class=\"section-title\">Gráfico de Performance</h2><img src=\"{chartImage}\" alt=\"Gráfico de Vendas\" class=\"chart-image\"><p>Evolução das vendas no período analisado</p></div>{/if}{#if observations}<div class=\"info-section\"><h2 class=\"section-title\">Observações</h2><ul>{#for obs in observations}<li>{obs}</li>{/for}</ul></div>{/if}{#if showSignatures}<div class=\"signature-section\">{#for signature in signatures}<div class=\"signature-box\"><div class=\"signature-line\">{signature.name}</div><p>{signature.title}</p><p>{signature.date}</p></div>{/for}</div>{/if}<div class=\"footer\"><p>Relatório gerado automaticamente pelo Sistema de Templates</p><p>© {#if company.name}{company.name}{#else}Sua Empresa{/if} - {date}</p></div></div></body></html>"
}
```

#### Exemplo de Dados para o Template

```json
{
  "title": "Relatório de Vendas - Janeiro 2024",
  "subtitle": "Análise Mensal de Performance",
  "period": "Janeiro 2024",
  "date": "31/01/2024",
  "company": {
    "name": "TechCorp Solutions",
    "logo": "data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAYAAAAfFcSJAAAADUlEQVR42mNkYPhfDwAChAI9jU8j8wAAAABJRU5ErkJggg=="
  },
  "executiveSummary": "Durante o mês de janeiro de 2024, observamos um crescimento significativo nas vendas, superando as metas estabelecidas em 15%. Os principais destaques incluem o aumento na demanda por produtos premium e a expansão para novos mercados.",
  "metrics": [
    {
      "value": "R$ 250.000",
      "label": "Receita Total"
    },
    {
      "value": "1.250",
      "label": "Unidades Vendidas"
    },
    {
      "value": "85",
      "label": "Novos Clientes"
    },
    {
      "value": "15%",
      "label": "Crescimento"
    }
  ],
  "salesData": [
    {
      "produto": "Produto Premium A",
      "quantidade": 450,
      "valorUnitario": "120,00",
      "total": "54.000,00",
      "status": "Excelente"
    },
    {
      "produto": "Produto Standard B",
      "quantidade": 600,
      "valorUnitario": "80,00",
      "total": "48.000,00",
      "status": "Bom"
    },
    {
      "produto": "Produto Basic C",
      "quantidade": 200,
      "valorUnitario": "45,00",
      "total": "9.000,00",
      "status": "Regular"
    }
  ],
  "chartImage": "data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAYAAAAfFcSJAAAADUlEQVR42mNkYPhfDwAChAI9jU8j8wAAAABJRU5ErkJggg==",
  "observations": [
    "Crescimento de 25% em produtos premium comparado ao mês anterior",
    "Expansão bem-sucedida para 3 novos estados",
    "Implementação de novo sistema de CRM resultou em melhor conversão",
    "Necessário aumentar estoque para o próximo trimestre"
  ],
  "showSignatures": true,
  "signatures": [
    {
      "name": "Maria Silva",
      "title": "Gerente de Vendas",
      "date": "31/01/2024"
    },
    {
      "name": "João Santos",
      "title": "Diretor Comercial",
      "date": "31/01/2024"
    }
  ]
}
```

#### Outros Endpoints de Templates

- **GET** `/api/templates` - Listar todos os templates
- **GET** `/api/templates/{name}` - Buscar template por nome
- **GET** `/api/templates/type/{type}` - Buscar templates por tipo (PDF, EMAIL, SMS)
- **GET** `/api/templates/search?name={pattern}` - Buscar templates por padrão no nome
- **PUT** `/api/templates/{name}` - Atualizar template
- **DELETE** `/api/templates/{name}` - Deletar template
- **POST** `/api/templates/{name}/generate-pdf` - Gerar PDF diretamente do template

### 2. Geração de Documentos

#### Gerar Documento
**POST** `/documents/generate`

```json
{
  "templateName": "relatorio-vendas",
  "data": {
    "title": "Relatório de Vendas - Janeiro 2024",
    "subtitle": "Análise Mensal de Performance",
    "period": "Janeiro 2024",
    "date": "31/01/2024",
    "company": {
      "name": "TechCorp Solutions",
      "logo": "data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAYAAAAfFcSJAAAADUlEQVR42mNkYPhfDwAChAI9jU8j8wAAAABJRU5ErkJggg=="
    },
    "executiveSummary": "Resumo do desempenho mensal...",
    "metrics": [
      {"value": "R$ 250.000", "label": "Receita Total"},
      {"value": "1.250", "label": "Unidades Vendidas"}
    ],
    "salesData": [
      {
        "produto": "Produto A",
        "quantidade": 450,
        "valorUnitario": "120,00",
        "total": "54.000,00",
        "status": "Excelente"
      }
    ],
    "showSignatures": true,
    "signatures": [
      {
        "name": "Maria Silva",
        "title": "Gerente de Vendas",
        "date": "31/01/2024"
      }
    ]
  },
  "options": {
    "filename": "relatorio-vendas-janeiro-2024.pdf",
    "orientation": "portrait",
    "pageSize": "A4",
    "includeMetadata": true,
    "author": "Sistema de Templates",
    "subject": "Relatório de Vendas",
    "keywords": "vendas, relatório, janeiro, 2024"
  }
}
```

## Recursos do Template

O sistema suporta templates HTML avançados com:

### Condicionais
```html
{#if condition}
  Conteúdo exibido se verdadeiro
{#elseif otherCondition}
  Conteúdo alternativo
{#else}
  Conteúdo padrão
{/if}
```

### Loops
```html
{#for item in items}
  <p>{item.name}: {item.value}</p>
{/for}
```

### Imagens Base64
```html
<!-- Onde company.logo contém: data:image/png;base64,iVBORw0... -->
```

### Estilização CSS
- CSS inline ou em `<style>` tags
- Suporte a Flexbox e Grid
- Media queries para impressão
- Gradientes e sombras
- Responsividade

### Formatação Condicional
```html
<td class="{#if item.status == 'Excelente'}status-success{#elseif item.status == 'Bom'}status-warning{#else}status-danger{/if}">
  {item.status}
</td>
```

## Tecnologias Utilizadas

- **Quarkus** - Framework Java supersônico
- **MongoDB** - Banco de dados NoSQL
- **Panache** - Camada de persistência simplificada
- **Qute** - Engine de templates
- **iText** - Geração de PDF
- **SmallRye OpenAPI** - Documentação da API
- **Lombok** - Redução de boilerplate
- **RestAssured** - Testes de API
- **JUnit 5** - Framework de testes

## Executando Testes

```bash
# Todos os testes
./mvnw test

# Testes específicos
./mvnw test -Dtest=DocumentResourceTest

# Teste Metodo específico
./mvnw test -Dtest=DocumentResourceTest#testGenerateSimpleDocument
```

## Empacotando e executando a aplicação

A aplicação pode ser empacotada usando:

```shell script
./mvnw package
```

Isso produz o arquivo `quarkus-run.jar` no diretório `target/quarkus-app/`.
As dependências são copiadas para o diretório `target/quarkus-app/lib/`.

A aplicação agora pode ser executada usando `java -jar target/quarkus-app/quarkus-run.jar`.

## Criando um executável nativo

Você pode criar um executável nativo usando:

```shell script
./mvnw package -Dnative
```

Ou, se você não tem o GraalVM instalado, pode executar a construção do executável nativo em um container usando:

```shell script
./mvnw package -Dnative -Dquarkus.native.container-build=true
```

Você pode então executar seu executável nativo com: `./target/sistemplate-1.0.0-SNAPSHOT-runner`

Se você quiser aprender mais sobre a construção de executáveis nativos, consulte <https://quarkus.io/guides/maven-tooling>.

## Guias Relacionados

- RESTEasy Reactive ([guide](https://quarkus.io/guides/resteasy-reactive)): Uma implementação Jakarta REST usando processamento reativo
- SmallRye OpenAPI ([guide](https://quarkus.io/guides/openapi-swaggerui)): Documente suas APIs REST com OpenAPI - vem com Swagger UI
- MongoDB with Panache ([guide](https://quarkus.io/guides/mongodb-panache)): Simplifique seu código de persistência para MongoDB via Active Record ou Repository pattern
