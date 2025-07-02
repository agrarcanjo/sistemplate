# API Usage Examples - Sistema de Templates

## Visão Geral

Esta API permite o gerenciamento completo de templates e geração dinâmica de documentos PDF com suporte a:
- Templates HTML com engine Qute
- Upload e gestão de imagens
- Dados JSON complexos e aninhados
- Metadados de PDF personalizáveis
- Cache inteligente de recursos

## Endpoints Principais

### 1. Gestão de Templates

#### Criar Template
```http
POST /api/templates
Content-Type: application/json

{
  "name": "executive-report",
  "type": "PDF",
  "content": "<!DOCTYPE html>...",
  "description": "Template para relatórios executivos",
  "author": "Sistema",
  "category": "reports",
  "tags": ["executive", "quarterly", "kpi"],
  "metadata": {
    "requiredFields": ["title", "company", "kpis"],
    "optionalFields": ["charts", "analysis"],
    "imageReferences": [
      {
        "placeholder": "company.logo",
        "description": "Logo da empresa",
        "recommendedSize": "200x100",
        "required": false
      }
    ],
    "sampleData": "{\"title\": \"Exemplo\"}",
    "documentation": "Template para relatórios executivos com KPIs"
  }
}
```

#### Listar Templates
```http
GET /api/templates
```

#### Buscar por Categoria
```http
GET /api/templates/category/reports
```

### 2. Gestão de Imagens

#### Upload de Imagem
```http
POST /api/images/upload
Content-Type: multipart/form-data

file: [arquivo de imagem]
name: "company_logo"
description: "Logo da empresa principal"
category: "logos"
owner: "admin"
```

#### Obter Imagem como Base64
```http
GET /api/images/company_logo/base64
```

### 3. Geração de Documentos

#### Gerar PDF com Dados JSON Complexos
```http
POST /documents/generate
Content-Type: application/json

{
  "templateName": "executive-report",
  "data": {
    "title": "Relatório Q1 2024",
    "company": {
      "name": "TechCorp",
      "logo": "company_logo"
    },
    "kpis": [
      {
        "value": "R$ 2.5M",
        "label": "Receita"
      }
    ],
    "charts": [
      {
        "title": "Vendas Mensais",
        "image": "sales_chart",
        "description": "Crescimento consistente"
      }
    ]
  },
  "options": {
    "filename": "relatorio-q1-2024.pdf",
    "orientation": "portrait",
    "pageSize": "A4",
    "author": "Sistema de Relatórios",
    "subject": "Relatório Executivo Q1 2024",
    "keywords": "relatório, vendas, kpi"
  }
}
```

## Exemplos de Dados JSON

### Relatório Executivo Completo
```json
{
  "title": "Relatório Executivo - Q1 2024",
  "subtitle": "Análise de Performance",
  "company": {
    "name": "TechCorp Solutions",
    "logo": "company_logo"
  },
  "executiveSummary": "Crescimento de 25% no trimestre...",
  "kpis": [
    {"value": "R$ 2.5M", "label": "Receita Total"},
    {"value": "25%", "label": "Crescimento"}
  ],
  "charts": [
    {
      "title": "Evolução das Vendas",
      "image": "sales_chart",
      "description": "Crescimento consistente"
    }
  ],
  "dataTable": {
    "title": "Vendas por Produto",
    "headers": ["Produto", "Vendas", "Crescimento"],
    "rows": [
      ["Software Premium", "R$ 850k", "32%"],
      ["Consultoria", "R$ 650k", "18%"]
    ]
  },
  "goals": [
    {
      "name": "Meta de Receita",
      "current": 2500000,
      "target": 10000000,
      "percentage": 25
    }
  ],
  "alerts": [
    {
      "type": "success",
      "title": "Meta Superada",
      "message": "Satisfação superou expectativas"
    }
  ]
}
```

### Contrato Dinâmico
```json
{
  "contractType": "service",
  "parties": {
    "contractor": {
      "name": "Empresa ABC Ltda",
      "document": "12.345.678/0001-90",
      "address": "Rua das Flores, 123"
    },
    "client": {
      "name": "Cliente XYZ",
      "document": "987.654.321-00",
      "address": "Av. Principal, 456"
    }
  },
  "services": [
    {
      "description": "Desenvolvimento de Software",
      "duration": "6 meses",
      "value": 50000.00
    }
  ],
  "terms": [
    "Pagamento em 6 parcelas mensais",
    "Garantia de 12 meses",
    "Suporte técnico incluído"
  ],
  "signatures": [
    {"name": "João Silva", "role": "Diretor", "date": "2024-03-31"}
  ]
}
```

## Recursos Avançados

### 1. Processamento Automático de Imagens
- Imagens são automaticamente convertidas para base64
- Campos terminados em "_image" ou "Image" são processados automaticamente
- Suporte a thumbnails para preview

### 2. Templates Condicionais
```html
{#if company.logo}
<img src="{company.logo}" alt="Logo">
{/if}

{#for item in items}
<li>{item.name}: {item.value}</li>
{/for}

{#switch status}
  {#case 'active'}Ativo{/case}
  {#case 'inactive'}Inativo{/case}
  {#default}Desconhecido{/default}
{/switch}
```

### 3. Formatação Automática
- Números: `{value.format('currency')}`
- Datas: `{date.format('dd/MM/yyyy')}`
- Percentuais: `{percentage}%`

## Códigos de Status HTTP

- `200 OK` - Operação bem-sucedida
- `201 Created` - Recurso criado com sucesso
- `400 Bad Request` - Dados inválidos
- `404 Not Found` - Recurso não encontrado
- `409 Conflict` - Conflito (ex: nome duplicado)
- `500 Internal Server Error` - Erro interno

## Considerações de Performance

### Cache
- Imagens são cacheadas automaticamente
- Templates compilados são mantidos em cache
- Base64 de imagens é pré-calculado

### Otimizações
- Upload de imagens com detecção de duplicatas por hash
- Compressão automática de thumbnails
- Pool de conexões MongoDB otimizado

### Limites
- Tamanho máximo de upload: 50MB
- Tamanho máximo de formulário: 10MB
- Cache de imagens: 1000 itens, 1 hora TTL

## Segurança

### Validações
- Tipos de arquivo permitidos para upload
- Sanitização de HTML em templates
- Validação de dados JSON

### Controle de Acesso
- Imagens por proprietário
- Templates por categoria
- Logs de auditoria (futuro)

## Monitoramento

### Métricas Disponíveis
- `/q/health` - Status da aplicação
- `/q/metrics` - Métricas do MongoDB
- Logs estruturados em produção

### Swagger UI
- Disponível em `/q/swagger-ui`
- Documentação interativa completa
- Exemplos de payloads