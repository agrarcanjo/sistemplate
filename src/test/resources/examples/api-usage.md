# Exemplos de Uso da API - Sistema de Templates

Este documento contém exemplos práticos de como usar a API do Sistema de Templates.

## 1. Criando um Template Completo

### Requisição POST /api/templates

```bash
curl -X POST "http://localhost:8080/api/templates" \
  -H "Content-Type: application/json" \
  -d '{
    "name": "relatorio-vendas",
    "type": "PDF",
    "description": "Template para relatório de vendas com gráficos e condicionais",
    "author": "João Silva",
    "owner": "departamento-vendas",
    "manager": "maria.santos@empresa.com",
    "category": "relatorios",
    "tags": ["vendas", "mensal", "grafico", "condicional"],
    "content": "<!DOCTYPE html><html><head><meta charset=\"UTF-8\"><title>Relatório de Vendas</title><style>{|body{font-family:Arial,sans-serif;margin:0;padding:20px;background-color:#f5f5f5}.container{max-width:800px;margin:0 auto;background:white;padding:30px;border-radius:8px;box-shadow:0 2px 10px rgba(0,0,0,0.1)}.header{text-align:center;border-bottom:3px solid #007bff;padding-bottom:20px;margin-bottom:30px}.logo{max-width:150px;height:auto;margin-bottom:10px}.title{color:#333;font-size:28px;margin:10px 0}.subtitle{color:#666;font-size:16px;margin:5px 0}.info-section{margin:20px 0;padding:15px;background:#f8f9fa;border-left:4px solid #007bff;border-radius:4px}.section-title{color:#007bff;font-size:18px;font-weight:bold;margin-bottom:10px}.metrics{display:flex;justify-content:space-around;margin:20px 0}.metric-card{text-align:center;padding:15px;background:linear-gradient(135deg,#007bff,#0056b3);color:white;border-radius:8px;min-width:120px}.metric-value{font-size:24px;font-weight:bold}.metric-label{font-size:12px;opacity:0.9}.table{width:100%;border-collapse:collapse;margin:20px 0}.table th,.table td{padding:12px;text-align:left;border-bottom:1px solid #ddd}.table th{background-color:#007bff;color:white;font-weight:bold}.table tr:hover{background-color:#f5f5f5}.status-success{color:#28a745;font-weight:bold}.status-warning{color:#ffc107;font-weight:bold}.status-danger{color:#dc3545;font-weight:bold}.chart-container{text-align:center;margin:20px 0;padding:15px;background:#f8f9fa;border-radius:8px}.chart-image{max-width:100%;height:auto;border-radius:4px}.footer{margin-top:40px;padding-top:20px;border-top:2px solid #eee;text-align:center;color:#666;font-size:14px}.signature-section{margin-top:30px;display:flex;justify-content:space-between}.signature-box{text-align:center;min-width:200px}.signature-line{border-top:1px solid #333;margin-top:40px;padding-top:5px}@media print{body{background:white}.container{box-shadow:none;padding:20px}.metrics{flex-direction:column}.metric-card{margin:5px 0}}|}</style></head><body><div class=\"container\"><div class=\"header\">{#if company.logo}<img src=\"{company.logo}\" alt=\"Logo\" class=\"logo\">{/if}<h1 class=\"title\">{title}</h1><p class=\"subtitle\">{subtitle}</p><p>Período: {period} | Data: {date}</p></div>{#if executiveSummary}<div class=\"info-section\"><h2 class=\"section-title\">Resumo Executivo</h2><p>{executiveSummary}</p></div>{/if}{#if metrics}<div class=\"metrics\">{#for metric in metrics}<div class=\"metric-card\"><div class=\"metric-value\">{metric.value}</div><div class=\"metric-label\">{metric.label}</div></div>{/for}</div>{/if}{#if salesData}<div class=\"info-section\"><h2 class=\"section-title\">Dados de Vendas</h2><table class=\"table\"><thead><tr><th>Produto</th><th>Quantidade</th><th>Valor Unitário</th><th>Total</th><th>Status</th></tr></thead><tbody>{#for item in salesData}<tr><td>{item.produto}</td><td>{item.quantidade}</td><td>R$ {item.valorUnitario}</td><td>R$ {item.total}</td><td class=\"{#if item.status == 'Excelente'}status-success{#else}{#if item.status == 'Bom'}status-warning{#else}status-danger{/if}{/if}\">{item.status}</td></tr>{/for}</tbody></table></div>{/if}{#if chartImage}<div class=\"chart-container\"><h2 class=\"section-title\">Gráfico de Performance</h2><img src=\"{chartImage}\" alt=\"Gráfico de Vendas\" class=\"chart-image\"><p>Evolução das vendas no período analisado</p></div>{/if}{#if observations}<div class=\"info-section\"><h2 class=\"section-title\">Observações</h2><ul>{#for obs in observations}<li>{obs}</li>{/for}</ul></div>{/if}{#if showSignatures}<div class=\"signature-section\">{#for signature in signatures}<div class=\"signature-box\"><div class=\"signature-line\">{signature.name}</div><p>{signature.title}</p><p>{signature.date}</p></div>{/for}</div>{/if}<div class=\"footer\"><p>Relatório gerado automaticamente pelo Sistema de Templates</p><p>© {#if company.name}{company.name}{#else}Sua Empresa{/if} - {date}</p></div></div></body></html>",
    "metadata": {
      "requiredFields": ["title", "subtitle", "period", "date", "company"],
      "optionalFields": ["executiveSummary", "metrics", "salesData", "chartImage", "observations", "showSignatures", "signatures"],
      "sampleData": "{\"title\":\"Relatório de Vendas - Janeiro 2024\",\"subtitle\":\"Análise Mensal de Performance\",\"period\":\"Janeiro 2024\",\"date\":\"31/01/2024\",\"company\":{\"name\":\"TechCorp Solutions\",\"logo\":\"data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAYAAAAfFcSJAAAADUlEQVR42mNkYPhfDwAChAI9jU8j8wAAAABJRU5ErkJggg==\"}}",
      "documentation": "Template para geração de relatórios de vendas mensais. Suporta gráficos, tabelas dinâmicas e assinaturas condicionais."
    }
  }'
```

### Resposta Esperada

```json
{
  "data": {
    "name": "relatorio-vendas",
    "type": "PDF",
    "description": "Template para relatório de vendas com gráficos e condicionais",
    "author": "João Silva",
    "owner": "departamento-vendas",
    "manager": "maria.santos@empresa.com",
    "category": "relatorios",
    "tags": ["vendas", "mensal", "grafico", "condicional"],
    "version": 1.0,
    "active": true,
    "createdAt": "2024-01-31T10:30:00Z",
    "updatedAt": "2024-01-31T10:30:00Z",
    "content": "<!DOCTYPE html>...",
    "metadata": {
      "requiredFields": ["title", "subtitle", "period", "date", "company"],
      "optionalFields": ["executiveSummary", "metrics", "salesData", "chartImage", "observations", "showSignatures", "signatures"],
      "sampleData": "{\"title\":\"Relatório de Vendas - Janeiro 2024\"...}",
      "documentation": "Template para geração de relatórios de vendas mensais..."
    }
  }
}
```

## 2. Listando Todos os Templates

### Requisição GET /api/templates

```bash
curl -X GET "http://localhost:8080/api/templates"
```

### Resposta Esperada

```json
{
  "data": [
    {
      "name": "relatorio-vendas",
      "type": "PDF",
      "description": "Template para relatório de vendas",
      "author": "João Silva",
      "owner": "departamento-vendas",
      "manager": "maria.santos@empresa.com",
      "category": "relatorios",
      "tags": ["vendas", "mensal"],
      "version": 1.0,
      "active": true,
      "createdAt": "2024-01-31T10:00:00Z",
      "updatedAt": "2024-01-31T10:00:00Z"
    }
  ]
}
```

## 3. Buscando Template por Nome

### Requisição GET /api/templates/{name}

```bash
curl -X GET "http://localhost:8080/api/templates/relatorio-vendas"
```

### Resposta Esperada

```json
{
  "data": {
    "name": "relatorio-vendas",
    "type": "PDF",
    "description": "Template para relatório de vendas com gráficos e condicionais",
    "author": "João Silva",
    "owner": "departamento-vendas",
    "manager": "maria.santos@empresa.com",
    "category": "relatorios",
    "tags": ["vendas", "mensal", "grafico", "condicional"],
    "version": 1.0,
    "active": true,
    "createdAt": "2024-01-31T10:30:00Z",
    "updatedAt": "2024-01-31T10:30:00Z",
    "content": "<!DOCTYPE html>...",
    "metadata": {
      "requiredFields": ["title", "subtitle", "period", "date", "company"],
      "optionalFields": ["executiveSummary", "metrics", "salesData"],
      "sampleData": "{\"title\":\"Relatório de Vendas - Janeiro 2024\"...}",
      "documentation": "Template para geração de relatórios de vendas mensais..."
    }
  }
}
```

## 4. Buscando Templates por Tipo

### Requisição GET /api/templates/type/{type}

```bash
curl -X GET "http://localhost:8080/api/templates/type/PDF"
```

### Tipos Disponíveis
- `PDF` - Documentos PDF
- `EMAIL` - Templates de email
- `SMS` - Templates de SMS

### Resposta Esperada

```json
{
  "data": [
    {
      "name": "relatorio-vendas",
      "type": "PDF",
      "description": "Template para relatório de vendas",
      "category": "relatorios",
      "tags": ["vendas", "mensal"]
    },
    {
      "name": "contrato-servicos",
      "type": "PDF",
      "description": "Template para contratos de serviços",
      "category": "contratos",
      "tags": ["contrato", "servicos"]
    }
  ]
}
```

## 5. Pesquisando Templates por Nome

### Requisição GET /api/templates/search?name={pattern}

```bash
curl -X GET "http://localhost:8080/api/templates/search?name=relatorio"
```

### Resposta Esperada

```json
{
  "data": [
    {
      "name": "relatorio-vendas",
      "type": "PDF",
      "description": "Template para relatório de vendas",
      "category": "relatorios"
    },
    {
      "name": "relatorio-financeiro",
      "type": "PDF",
      "description": "Template para relatório financeiro",
      "category": "relatorios"
    }
  ]
}
```

## 6. Listando Versões de um Template

### Requisição GET /api/templates/{name}/versions

```bash
curl -X GET "http://localhost:8080/api/templates/relatorio-vendas/versions"
```

### Resposta Esperada

```json
{
  "data": [
    {
      "name": "relatorio-vendas",
      "version": 1.0,
      "active": true,
      "createdAt": "2024-01-31T10:00:00Z",
      "updatedAt": "2024-01-31T10:00:00Z"
    },
    {
      "name": "relatorio-vendas",
      "version": 2.0,
      "active": false,
      "createdAt": "2024-02-15T14:30:00Z",
      "updatedAt": "2024-02-15T14:30:00Z"
    }
  ]
}
```

## 7. Atualizando um Template

### Requisição PUT /api/templates/{name}

```bash
curl -X PUT "http://localhost:8080/api/templates/relatorio-vendas" \
  -H "Content-Type: application/json" \
  -d '{
    "name": "relatorio-vendas",
    "type": "PDF",
    "description": "Template atualizado para relatório de vendas com novos recursos",
    "author": "João Silva",
    "owner": "departamento-vendas",
    "manager": "maria.santos@empresa.com",
    "category": "relatorios",
    "tags": ["vendas", "mensal", "grafico", "condicional", "v2"],
    "content": "<!DOCTYPE html>...",
    "metadata": {
      "requiredFields": ["title", "subtitle", "period", "date", "company"],
      "optionalFields": ["executiveSummary", "metrics", "salesData", "chartImage"],
      "sampleData": "{\"title\":\"Relatório Atualizado\"}",
      "documentation": "Versão atualizada do template com novos recursos."
    }
  }'
```

### Resposta Esperada

```json
{
  "data": {
    "name": "relatorio-vendas",
    "type": "PDF",
    "description": "Template atualizado para relatório de vendas com novos recursos",
    "version": 2.0,
    "active": true,
    "updatedAt": "2024-02-15T14:30:00Z"
  }
}
```

## 8. Removendo um Template

### Requisição DELETE /api/templates/{name}

```bash
curl -X DELETE "http://localhost:8080/api/templates/relatorio-vendas"
```

### Resposta Esperada

```
HTTP/1.1 204 No Content
```

## 9. Gerando um Documento PDF

### Requisição POST /api/documents/generate

```bash
curl -X POST "http://localhost:8080/api/documents/generate" \
  -H "Content-Type: application/json" \
  -d '{
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
      "executiveSummary": "Durante o mês de janeiro de 2024, observamos um crescimento significativo nas vendas, superando as metas estabelecidas em 15%.",
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
        }
      ],
      "chartImage": "data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAYAAAAfFcSJAAAADUlEQVR42mNkYPhfDwAChAI9jU8j8wAAAABJRU5ErkJggg==",
      "observations": [
        "Crescimento significativo no segmento premium",
        "Necessidade de expansão do estoque para produtos básicos"
      ],
      "showSignatures": true,
      "signatures": [
        {
          "name": "João Silva",
          "title": "Diretor Comercial",
          "date": "31/01/2024"
        }
      ]
    },
    "options": {
      "filename": "relatorio-vendas-janeiro-2024.pdf",
      "orientation": "portrait",
      "pageSize": "A4",
      "author": "Sistema de Templates",
      "subject": "Relatório de Vendas Janeiro 2024",
      "keywords": "vendas, relatório, janeiro, 2024"
    }
  }'
```

### Resposta Esperada

Retorna um arquivo PDF com os cabeçalhos:
```
Content-Type: application/pdf
Content-Disposition: attachment; filename="relatorio-vendas-janeiro-2024.pdf"
```

## 10. Gerando Documento em Base64

### Requisição POST /api/documents/generate-base64

```bash
curl -X POST "http://localhost:8080/api/documents/generate-base64" \
  -H "Content-Type: application/json" \
  -d '{
    "templateName": "relatorio-vendas",
    "data": {
      "title": "Relatório de Vendas - Janeiro 2024",
      "company": {
        "name": "TechCorp Solutions"
      }
    },
    "options": {
      "filename": "relatorio-vendas-janeiro-2024.pdf",
      "orientation": "portrait",
      "pageSize": "A4"
    }
  }'
```

### Resposta Esperada

```json
{
  "data": {
    "filename": "relatorio-vendas-janeiro-2024.pdf",
    "content": "JVBERi0xLjQKJcOkw7zDtsO8w6...",
    "size": 15234,
    "contentType": "application/pdf"
  }
}
```

## 11. Template com Condicionais e Loops

### Exemplo de Template Avançado

```html
<!DOCTYPE html>
<html lang="pt">
<head>
    <meta charset="UTF-8">
    <title>{title}</title>
    <style>
    {|
    body { font-family: Arial, sans-serif; margin: 40px; }
    .header { text-align: center; margin-bottom: 30px; }
    .section { margin: 20px 0; }
    .table { width: 100%; border-collapse: collapse; }
    .table th, .table td { border: 1px solid #ddd; padding: 8px; }
    .table th { background-color: #f2f2f2; }
    .status-success { color: #28a745; font-weight: bold; }
    .status-warning { color: #ffc107; font-weight: bold; }
    .status-danger { color: #dc3545; font-weight: bold; }
    |}
    </style>
</head>
<body>
    <div class="header">
        {#if company.logo}
        <img src="{company.logo}" alt="Logo" style="max-width: 150px;">
        {/if}
        <h1>{title}</h1>
        {#if subtitle}<h2>{subtitle}</h2>{/if}
    </div>

    {#if executiveSummary}
    <div class="section">
        <h3>Resumo Executivo</h3>
        <p>{executiveSummary}</p>
    </div>
    {/if}

    {#if items}
    <div class="section">
        <h3>Itens</h3>
        <table class="table">
            <thead>
                <tr>
                    <th>Nome</th>
                    <th>Valor</th>
                    <th>Status</th>
                </tr>
            </thead>
            <tbody>
                {#for item in items}
                <tr>
                    <td>{item.nome}</td>
                    <td>R$ {item.valor}</td>
                    <td class="{#if item.status == 'Excelente'}status-success{#else}{#if item.status == 'Bom'}status-warning{#else}status-danger{/if}{/if}">
                        {item.status}
                    </td>
                </tr>
                {/for}
            </tbody>
        </table>
    </div>
    {/if}

    <div class="section">
        <p>Data: {date}</p>
        {#if signature}
        <p>Assinatura: {signature}</p>
        {/if}
    </div>
</body>
</html>
```

## 12. Códigos de Status HTTP

- **200 OK**: Operação realizada com sucesso
- **201 Created**: Recurso criado com sucesso
- **204 No Content**: Recurso removido com sucesso
- **400 Bad Request**: Dados inválidos na requisição
- **404 Not Found**: Recurso não encontrado
- **409 Conflict**: Conflito (ex: nome duplicado)
- **500 Internal Server Error**: Erro interno do servidor

## 13. Estrutura de Metadados

### Campos de Metadata

```json
{
  "metadata": {
    "requiredFields": ["title", "date", "company"],
    "optionalFields": ["subtitle", "logo", "observations"],
    "sampleData": "{\"title\":\"Exemplo\",\"date\":\"2024-01-31\"}",
    "documentation": "Documentação detalhada do template"
  }
}
```

### Descrição dos Campos

- **requiredFields**: Lista de campos obrigatórios no JSON de dados
- **optionalFields**: Lista de campos opcionais
- **sampleData**: JSON de exemplo para teste do template
- **documentation**: Documentação detalhada sobre o uso do template

## 14. Dicas de Performance

1. **Templates**: Mantenha o HTML limpo e evite CSS excessivo inline
2. **Dados**: Estruture os dados de forma eficiente para os loops
3. **Metadados**: Use os campos `requiredFields` e `optionalFields` para validação
4. **Versionamento**: O sistema mantém versões automáticas dos templates
5. **Categorização**: Use `category` e `tags` para organizar templates
6. **Paginação**: Para relatórios grandes, considere quebrar em seções

## 15. Troubleshooting

### Template não encontrado
```json
{
  "error": "Template not found",
  "message": "Template 'nome-template' não existe"
}
```

### Dados inválidos
```json
{
  "error": "Validation failed",
  "message": "Campo 'name' é obrigatório"
}
```

### Erro na geração do PDF
```json
{
  "error": "PDF generation failed",
  "message": "Erro ao processar template HTML"
}
```

### Template já existe
```json
{
  "error": "Template already exists",
  "message": "Template 'nome-template' já existe"
}
```

## 16. Resolução de Problemas com CSS

### Problema: Erro de Renderização CSS

Se você encontrar o erro:
```
Rendering error: No namespace resolver found for [font-family] in expression {font-family:Arial,sans-serif;margin:40px;line-height:1.6}
```

**Causa**: O Qute Template Engine interpreta as chaves `{}` no CSS como expressões de template.

**Solução**: Envolva todo o CSS em seções literais usando `{|` e `|}`:

#### ❌ Incorreto:
```html
<style>
body {
    font-family: Arial, sans-serif;
    margin: 40px;
    line-height: 1.6;
}
</style>
```

#### ✅ Correto:
```html
<style>
{|
body {
    font-family: Arial, sans-serif;
    margin: 40px;
    line-height: 1.6;
}
|}
</style>
```

### Exemplo de Template Corrigido

```json
{
  "name": "carta-simples",
  "type": "PDF",
  "content": "<!DOCTYPE html><html><head><style>{|body{font-family:Arial,sans-serif;margin:40px;line-height:1.6}.header{text-align:center}|}</style></head><body><div class=\"header\"><h1>{company.name}</h1></div><p>{message}</p></body></html>"
}
```

### Dicas Importantes para CSS:

1. **Sempre use seções literais** `{| ... |}` para CSS inline
2. **Teste templates** antes de usar em produção
3. **Considere CSS externo** para templates muito complexos
4. **Mantenha logs habilitados** para debug durante desenvolvimento

## 17. Sintaxe Qute - Condicionais

### Problema com `{#elseif}`

O Qute Template Engine **NÃO SUPORTA** a sintaxe `{#elseif}`. Isso causará o erro:

```
Parser error: no section helper found for {#elseif item.status == 'Bom'}
```

### Sintaxe Correta para Condicionais

#### ❌ Incorreto (não funciona):
```html
{#if item.status == 'Excelente'}
  status-success
{#elseif item.status == 'Bom'}
  status-warning
{#else}
  status-danger
{/if}
```

#### ✅ Correto (condicionais aninhadas):
```html
{#if item.status == 'Excelente'}
  status-success
{#else}
  {#if item.status == 'Bom'}
    status-warning
  {#else}
    status-danger
  {/if}
{/if}
```

#### ✅ Alternativa com Switch (mais limpa):
```html
{#switch item.status}
  {#case 'Excelente'}status-success{/case}
  {#case 'Bom'}status-warning{/case}
  {#case}status-danger{/case}
{/switch}
```

### Exemplo Prático Corrigido

```html
<table class="table">
  <tbody>
    {#for item in salesData}
    <tr>
      <td>{item.produto}</td>
      <td class="{#if item.status == 'Excelente'}status-success{#else}{#if item.status == 'Bom'}status-warning{#else}status-danger{/if}{/if}">
        {item.status}
      </td>
    </tr>
    {/for}
  </tbody>
</table>
```

### Outros Helpers Úteis do Qute

1. **Loop com Index**: `{#for item in items}{it_index}: {item.name}{/for}`
2. **Verificar Vazio**: `{#if items.isEmpty()}Nenhum item{/if}`
3. **Operadores Lógicos**: `{#if user.isActive && user.hasPermission}...{/if}`

### Dicas Importantes para Qute:

1. **Use condicionais aninhadas** ao invés de `elseif`
2. **Switch é mais limpo** para múltiplas condições
3. **Teste templates** antes de usar em produção
4. **Use parênteses** para operações complexas: `{#if (a && b) || c}`
5. **Cuidado com espaços** na sintaxe: `{#if}` não `{ #if }`

## 18. Campos de Template

### Campos Obrigatórios
- **name**: Nome único do template
- **type**: Tipo do documento (PDF, EMAIL, SMS)
- **content**: Conteúdo HTML do template

### Campos Opcionais
- **description**: Descrição do template
- **author**: Autor do template
- **owner**: Proprietário/departamento responsável
- **manager**: Email do gerente responsável
- **category**: Categoria para organização
- **tags**: Lista de tags para busca e organização
- **metadata**: Metadados estruturados do template

### Campos Automáticos
- **version**: Versão do template (incrementada automaticamente)
- **active**: Status ativo/inativo
- **createdAt**: Data de criação
- **updatedAt**: Data da última atualização