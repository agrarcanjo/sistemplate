# Exemplos de Uso da API - Sistema de Templates

Este documento contém exemplos práticos de como usar a API do Sistema de Templates.

## 1. Criando um Template Avançado

### Requisição POST /api/templates

```bash
curl -X POST "http://localhost:8080/api/templates" \
  -H "Content-Type: application/json" \
  -d '{
    "name": "relatorio-vendas",
    "type": "PDF",
    "description": "Template para relatório de vendas com gráficos e condicionais",
    "content": "<!DOCTYPE html><html><head><meta charset=\"UTF-8\"><title>Relatório de Vendas</title><style>{|body{font-family:Arial,sans-serif;margin:0;padding:20px;background-color:#f5f5f5}.container{max-width:800px;margin:0 auto;background:white;padding:30px;border-radius:8px;box-shadow:0 2px 10px rgba(0,0,0,0.1)}.header{text-align:center;border-bottom:3px solid #007bff;padding-bottom:20px;margin-bottom:30px}.logo{max-width:150px;height:auto;margin-bottom:10px}.title{color:#333;font-size:28px;margin:10px 0}.subtitle{color:#666;font-size:16px;margin:5px 0}.info-section{margin:20px 0;padding:15px;background:#f8f9fa;border-left:4px solid #007bff;border-radius:4px}.section-title{color:#007bff;font-size:18px;font-weight:bold;margin-bottom:10px}.metrics{display:flex;justify-content:space-around;margin:20px 0}.metric-card{text-align:center;padding:15px;background:linear-gradient(135deg,#007bff,#0056b3);color:white;border-radius:8px;min-width:120px}.metric-value{font-size:24px;font-weight:bold}.metric-label{font-size:12px;opacity:0.9}.table{width:100%;border-collapse:collapse;margin:20px 0}.table th,.table td{padding:12px;text-align:left;border-bottom:1px solid #ddd}.table th{background-color:#007bff;color:white;font-weight:bold}.table tr:hover{background-color:#f5f5f5}.status-success{color:#28a745;font-weight:bold}.status-warning{color:#ffc107;font-weight:bold}.status-danger{color:#dc3545;font-weight:bold}.chart-container{text-align:center;margin:20px 0;padding:15px;background:#f8f9fa;border-radius:8px}.chart-image{max-width:100%;height:auto;border-radius:4px}.footer{margin-top:40px;padding-top:20px;border-top:2px solid #eee;text-align:center;color:#666;font-size:14px}.signature-section{margin-top:30px;display:flex;justify-content:space-between}.signature-box{text-align:center;min-width:200px}.signature-line{border-top:1px solid #333;margin-top:40px;padding-top:5px}@media print{body{background:white}.container{box-shadow:none;padding:20px}.metrics{flex-direction:column}.metric-card{margin:5px 0}}|}</style></head><body><div class=\"container\"><div class=\"header\">{#if company.logo}<img src=\"{company.logo}\" alt=\"Logo\" class=\"logo\">{/if}<h1 class=\"title\">{title}</h1><p class=\"subtitle\">{subtitle}</p><p>Período: {period} | Data: {date}</p></div>{#if executiveSummary}<div class=\"info-section\"><h2 class=\"section-title\">Resumo Executivo</h2><p>{executiveSummary}</p></div>{/if}{#if metrics}<div class=\"metrics\">{#for metric in metrics}<div class=\"metric-card\"><div class=\"metric-value\">{metric.value}</div><div class=\"metric-label\">{metric.label}</div></div>{/for}</div>{/if}{#if salesData}<div class=\"info-section\"><h2 class=\"section-title\">Dados de Vendas</h2><table class=\"table\"><thead><tr><th>Produto</th><th>Quantidade</th><th>Valor Unitário</th><th>Total</th><th>Status</th></tr></thead><tbody>{#for item in salesData}<tr><td>{item.produto}</td><td>{item.quantidade}</td><td>R$ {item.valorUnitario}</td><td>R$ {item.total}</td><td class=\"{#if item.status == 'Excelente'}status-success{#else}{#if item.status == 'Bom'}status-warning{#else}status-danger{/if}{/if}\">{item.status}</td></tr>{/for}</tbody></table></div>{/if}{#if chartImage}<div class=\"chart-container\"><h2 class=\"section-title\">Gráfico de Performance</h2><img src=\"{chartImage}\" alt=\"Gráfico de Vendas\" class=\"chart-image\"><p>Evolução das vendas no período analisado</p></div>{/if}{#if observations}<div class=\"info-section\"><h2 class=\"section-title\">Observações</h2><ul>{#for obs in observations}<li>{obs}</li>{/for}</ul></div>{/if}{#if showSignatures}<div class=\"signature-section\">{#for signature in signatures}<div class=\"signature-box\"><div class=\"signature-line\">{signature.name}</div><p>{signature.title}</p><p>{signature.date}</p></div>{/for}</div>{/if}<div class=\"footer\"><p>Relatório gerado automaticamente pelo Sistema de Templates</p><p>© {#if company.name}{company.name}{#else}Sua Empresa{/if} - {date}</p></div></div></body></html>"
  }'
```

### Resposta Esperada

```json
{
  "name": "relatorio-vendas",
  "type": "PDF",
  "description": "Template para relatório de vendas com gráficos e condicionais",
  "content": "<!DOCTYPE html>..."
}
```

## 2. Fazendo Upload de uma Imagem

### Requisição POST /api/images/upload

```bash
curl -X POST "http://localhost:8080/api/images/upload" \
  -H "Content-Type: multipart/form-data" \
  -F "file=@logo-empresa.png" \
  -F "name=logo-empresa" \
  -F "description=Logo oficial da empresa para relatórios" \
  -F "category=logos" \
  -F "owner=admin"
```

### Resposta Esperada

```json
{
  "name": "logo-empresa",
  "description": "Logo oficial da empresa para relatórios",
  "category": "logos",
  "owner": "admin",
  "size": 15234,
  "contentType": "image/png",
  "createdAt": "2024-01-31T10:30:00Z"
}
```

## 3. Gerando um Documento PDF

### Requisição POST /documents/generate

```bash
curl -X POST "http://localhost:8080/documents/generate" \
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
      "executiveSummary": "Durante o mês de janeiro de 2024, observamos um crescimento significativo nas vendas.",
      "metrics": [
        {
          "value": "R$ 250.000",
          "label": "Receita Total"
        },
        {
          "value": "1.250",
          "label": "Unidades Vendidas"
        }
      ],
      "salesData": [
        {
          "produto": "Produto Premium A",
          "quantidade": 450,
          "valorUnitario": "120,00",
          "total": "54.000,00",
          "status": "Excelente"
        }
      ]
    },
    "options": {
      "filename": "relatorio-vendas-janeiro-2024.pdf",
      "orientation": "portrait",
      "pageSize": "A4",
      "author": "Sistema de Templates",
      "subject": "Relatório de Vendas Janeiro 2024"
    }
  }'
```

### Resposta Esperada

Retorna um arquivo PDF com o cabeçalho:
```
Content-Type: application/pdf
Content-Disposition: attachment; filename="relatorio-vendas-janeiro-2024.pdf"
```

## 4. Buscando Templates

### Requisição GET /api/templates

```bash
curl -X GET "http://localhost:8080/api/templates"
```

### Resposta Esperada

```json
[
  {
    "name": "relatorio-vendas",
    "type": "PDF",
    "description": "Template para relatório de vendas",
    "author": "Sistema",
    "version": "1.0",
    "active": true,
    "createdAt": "2024-01-31T10:00:00Z",
    "updatedAt": "2024-01-31T10:00:00Z"
  }
]
```

## 5. Obtendo Imagem em Base64

### Requisição GET /api/images/{name}/base64

```bash
curl -X GET "http://localhost:8080/api/images/logo-empresa/base64"
```

### Resposta Esperada

```
data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAYAAAAfFcSJAAAADUlEQVR42mNkYPhfDwAChAI9jU8j8wAAAABJRU5ErkJggg==
```

## 6. Template com Condicionais e Loops

### Exemplo de Template Avançado

```html
<!DOCTYPE html>
<html>
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
                    <td>
                        {#if item.status == 'ativo'}
                            <span style="color: green;">Ativo</span>
                        {#else}
                            {#if item.status == 'pendente'}
                                <span style="color: orange;">Pendente</span>
                            {#else}
                                <span style="color: red;">Inativo</span>
                            {/if}
                        {/if}
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

## 7. Códigos de Status HTTP

- **200 OK**: Operação realizada com sucesso
- **201 Created**: Recurso criado com sucesso
- **400 Bad Request**: Dados inválidos na requisição
- **404 Not Found**: Recurso não encontrado
- **409 Conflict**: Conflito (ex: nome duplicado)
- **500 Internal Server Error**: Erro interno do servidor

## 8. Dicas de Performance

1. **Imagens**: Use imagens otimizadas e considere o tamanho do base64
2. **Templates**: Mantenha o HTML limpo e evite CSS excessivo inline
3. **Dados**: Estruture os dados de forma eficiente para os loops
4. **Cache**: O sistema utiliza cache para imagens frequentemente acessadas
5. **Paginação**: Para relatórios grandes, considere quebrar em seções

## 9. Troubleshooting

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

## 10. Resolução de Problemas com CSS

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

### Verificação Rápida:

Para testar se seu template está correto, use o endpoint de debug:

```bash
curl -X POST "http://localhost:8080/documents/debug/generate" \
  -H "Content-Type: application/json" \
  -d '{
    "templateName": "seu-template",
    "data": { "teste": "valor" }
  }'
```

Este endpoint retorna informações sobre o processamento sem gerar o PDF, facilitando a identificação de problemas.

## 11. Sintaxe Qute - Condicionais

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