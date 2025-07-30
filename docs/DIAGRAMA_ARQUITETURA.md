# Diagrama de Arquitetura - Sistema de Templates (sistemplate)

Este documento apresenta diagramas detalhados da arquitetura do Sistema de Templates para facilitar o entendimento do projeto por LLMs e desenvolvedores.

## 1. Visão Geral da Arquitetura

```mermaid
graph TB
    subgraph "Camada de Apresentação"
        API[REST API Endpoints]
        SWAGGER[Swagger/OpenAPI]
    end
    
    subgraph "Camada de Aplicação"
        DS[DocumentService]
        TS[TemplateService]
        RFV[RequiredFieldsValidator]
    end
    
    subgraph "Camada de Geração"
        PDF[PdfGenerator]
        EMAIL[EmailGenerator]
        SMS[SmsGenerator]
        QUTE[Qute Template Engine]
    end
    
    subgraph "Camada de Dados"
        MONGO[(MongoDB)]
        TR[TemplateRepository]
    end
    
    subgraph "Modelos e DTOs"
        TEMPLATE[Template Model]
        DREQ[DocumentRequest DTO]
        METADATA[Template Metadata]
    end
    
    subgraph "Tratamento de Erros"
        RFVE[RequiredFieldsValidationException]
        EH[Exception Handlers]
    end
    
    API --> DS
    DS --> TS
    DS --> RFV
    DS --> QUTE
    DS --> PDF
    DS --> EMAIL
    DS --> SMS
    
    TS --> TR
    TR --> MONGO
    
    RFV --> RFVE
    DS --> DREQ
    TS --> TEMPLATE
    TEMPLATE --> METADATA
    
    API --> SWAGGER
```

## 2. Fluxo de Geração de Documentos

```mermaid
sequenceDiagram
    participant Client
    participant API
    participant DocumentService
    participant TemplateService
    participant RequiredFieldsValidator
    participant QuteEngine
    participant Generator
    participant MongoDB
    
    Client->>API: POST /documents/generate
    API->>DocumentService: generateDocument(request)
    
    DocumentService->>TemplateService: findByName(templateName)
    TemplateService->>MongoDB: query template
    MongoDB-->>TemplateService: template data
    TemplateService-->>DocumentService: Template object
    
    alt Template not found
        DocumentService-->>API: RuntimeException
        API-->>Client: 404 Not Found
    end
    
    DocumentService->>DocumentService: convertJsonNodeToMap(data)
    DocumentService->>RequiredFieldsValidator: validateRequiredFields(template, dataMap)
    
    alt Required fields missing
        RequiredFieldsValidator-->>DocumentService: RequiredFieldsValidationException
        DocumentService-->>API: Exception
        API-->>Client: 400 Bad Request
    end
    
    DocumentService->>QuteEngine: parse(template.content).data(dataMap).render()
    QuteEngine-->>DocumentService: processedContent
    
    alt PDF Generation
        DocumentService->>Generator: pdfGenerator.generatePdf(content, data, options)
    else EMAIL Generation
        DocumentService->>Generator: emailGenerator.generateEmail(content, data)
    else SMS Generation
        DocumentService->>Generator: smsGenerator.generateSms(content, data)
    end
    
    Generator-->>DocumentService: byte[] result
    DocumentService-->>API: document bytes
    API-->>Client: Generated document
```

## 3. Estrutura de Classes e Relacionamentos

```mermaid
classDiagram
    class DocumentService {
        -Logger LOG
        -TemplateService templateService
        -PdfGenerator pdfGenerator
        -EmailGenerator emailGenerator
        -SmsGenerator smsGenerator
        -Engine engine
        -ObjectMapper objectMapper
        -RequiredFieldsValidator requiredFieldsValidator
        +generateDocument(DocumentRequest) byte[]
        -convertJsonNodeToMap(JsonNode) Map~String,Object~
    }
    
    class RequiredFieldsValidator {
        -Logger LOG
        +validateRequiredFields(Template, Map~String,Object~) void
        -findMissingFields(List~String~, Map~String,Object~) List~String~
        -isFieldPresent(String, Map~String,Object~) boolean
    }
    
    class RequiredFieldsValidationException {
        -List~String~ missingFields
        -String templateName
        +RequiredFieldsValidationException(String, List~String~)
        -buildMessage(String, List~String~) String
    }
    
    class Template {
        -String id
        -String name
        -TemplateType type
        -String content
        -String description
        -boolean active
        -TemplateMetadata metadata
        -LocalDateTime createdAt
        -LocalDateTime updatedAt
    }
    
    class TemplateMetadata {
        -List~String~ requiredFields
        -List~String~ optionalFields
        -Map~String,Object~ additionalProperties
    }
    
    class DocumentRequest {
        -String templateName
        -JsonNode data
        -Map~String,Object~ options
    }
    
    class TemplateService {
        -TemplateRepository repository
        +findByName(String) Template
        +save(Template) Template
        +findAll() List~Template~
        +delete(String) void
    }
    
    DocumentService --> RequiredFieldsValidator : uses
    DocumentService --> TemplateService : uses
    DocumentService --> DocumentRequest : processes
    RequiredFieldsValidator --> Template : validates
    RequiredFieldsValidator --> RequiredFieldsValidationException : throws
    Template --> TemplateMetadata : contains
    TemplateService --> Template : manages
```

## 4. Fluxo de Validação de Campos Obrigatórios

```mermaid
flowchart TD
    START([Início da Validação])
    GET_TEMPLATE[Obter Template]
    CHECK_METADATA{Template tem<br/>metadata?}
    CHECK_REQUIRED{Tem campos<br/>obrigatórios?}
    CONVERT_DATA[Converter dados<br/>JSON para Map]
    VALIDATE_FIELDS[Validar cada<br/>campo obrigatório]
    CHECK_FIELD{Campo presente<br/>e não nulo?}
    ADD_MISSING[Adicionar à lista<br/>de campos ausentes]
    CHECK_ALL{Todos campos<br/>verificados?}
    ANY_MISSING{Algum campo<br/>ausente?}
    THROW_EXCEPTION[Lançar<br/>RequiredFieldsValidationException]
    SUCCESS([Validação Concluída<br/>com Sucesso])
    
    START --> GET_TEMPLATE
    GET_TEMPLATE --> CHECK_METADATA
    CHECK_METADATA -->|Não| SUCCESS
    CHECK_METADATA -->|Sim| CHECK_REQUIRED
    CHECK_REQUIRED -->|Não| SUCCESS
    CHECK_REQUIRED -->|Sim| CONVERT_DATA
    CONVERT_DATA --> VALIDATE_FIELDS
    VALIDATE_FIELDS --> CHECK_FIELD
    CHECK_FIELD -->|Não| ADD_MISSING
    CHECK_FIELD -->|Sim| CHECK_ALL
    ADD_MISSING --> CHECK_ALL
    CHECK_ALL -->|Não| VALIDATE_FIELDS
    CHECK_ALL -->|Sim| ANY_MISSING
    ANY_MISSING -->|Sim| THROW_EXCEPTION
    ANY_MISSING -->|Não| SUCCESS
```

## 5. Estrutura de Dados do Template

```mermaid
graph LR
    subgraph "Template Structure"
        T[Template]
        T --> ID[id: String]
        T --> NAME[name: String]
        T --> TYPE[type: TemplateType]
        T --> CONTENT[content: String]
        T --> DESC[description: String]
        T --> ACTIVE[active: boolean]
        T --> META[metadata: TemplateMetadata]
        T --> CREATED[createdAt: LocalDateTime]
        T --> UPDATED[updatedAt: LocalDateTime]
    end
    
    subgraph "Metadata Structure"
        META --> REQ[requiredFields: List&lt;String&gt;]
        META --> OPT[optionalFields: List&lt;String&gt;]
        META --> ADD[additionalProperties: Map]
    end
    
    subgraph "Required Fields Examples"
        REQ --> RF1["funcionario.nome"]
        REQ --> RF2["funcionario.email"]
        REQ --> RF3["empresa.nome"]
        REQ --> RF4["contrato.salario"]
    end
```

## 6. Tipos de Documentos Suportados

```mermaid
graph TD
    subgraph "Document Types"
        DT[TemplateType]
        DT --> PDF[PDF Documents]
        DT --> EMAIL[Email Messages]
        DT --> SMS[SMS Messages]
    end
    
    subgraph "PDF Generation"
        PDF --> PDFG[PdfGenerator]
        PDFG --> PDFOPTS[Options: filename, orientation, pageSize]
        PDFG --> PDFOUT[Output: byte[] PDF]
    end
    
    subgraph "Email Generation"
        EMAIL --> EMAILG[EmailGenerator]
        EMAILG --> EMAILOUT[Output: byte[] Email]
    end
    
    subgraph "SMS Generation"
        SMS --> SMSG[SmsGenerator]
        SMSG --> SMSOUT[Output: byte[] SMS]
    end
```

## 7. Tratamento de Erros e Exceções

```mermaid
graph TD
    subgraph "Exception Hierarchy"
        RE[RuntimeException]
        RE --> RFVE[RequiredFieldsValidationException]
        RE --> TNF[Template Not Found]
        RE --> PGE[Processing Error]
        RE --> CGE[Conversion Error]
    end
    
    subgraph "Error Information"
        RFVE --> FIELDS[missingFields: List&lt;String&gt;]
        RFVE --> TNAME[templateName: String]
        RFVE --> MSG[Formatted Error Message]
    end
    
    subgraph "Error Handling Flow"
        ERR[Error Occurs]
        ERR --> LOG[Log Error Details]
        LOG --> WRAP[Wrap in RuntimeException]
        WRAP --> RETURN[Return Error Response]
    end
```

## 8. Configuração e Dependências

```mermaid
graph TB
    subgraph "Quarkus Framework"
        CDI[CDI Container]
        QUTE_ENG[Qute Template Engine]
        JACKSON[Jackson ObjectMapper]
        LOGGING[JBoss Logging]
    end
    
    subgraph "Database"
        MONGO_DB[(MongoDB)]
        MONGO_CLIENT[MongoDB Client]
    end
    
    subgraph "Application Services"
        DS[DocumentService]
        TS[TemplateService]
        RFV[RequiredFieldsValidator]
        GENERATORS[PDF/Email/SMS Generators]
    end
    
    CDI --> DS
    CDI --> TS
    CDI --> RFV
    CDI --> GENERATORS
    
    QUTE_ENG --> DS
    JACKSON --> DS
    LOGGING --> DS
    LOGGING --> RFV
    
    MONGO_CLIENT --> MONGO_DB
    TS --> MONGO_CLIENT
```

## 9. Exemplo de Uso Completo

```mermaid
sequenceDiagram
    participant Dev as Developer
    participant API as REST API
    participant System as Sistema
    participant DB as MongoDB
    
    Note over Dev,DB: 1. Criação de Template
    Dev->>API: POST /templates
    API->>System: Create template with metadata
    System->>DB: Save template
    
    Note over Dev,DB: 2. Geração de Documento
    Dev->>API: POST /documents/generate
    Note right of API: Request contains:<br/>- templateName<br/>- data (JSON)<br/>- options
    
    API->>System: Process request
    System->>DB: Find template
    System->>System: Validate required fields
    System->>System: Process with Qute
    System->>System: Generate document (PDF/Email/SMS)
    System-->>API: Return document bytes
    API-->>Dev: Document generated successfully
    
    Note over Dev,DB: 3. Error Scenario
    Dev->>API: POST /documents/generate (missing fields)
    API->>System: Process request
    System->>System: Validate required fields
    System-->>API: RequiredFieldsValidationException
    API-->>Dev: 400 Bad Request with field details
```

## Resumo dos Componentes Principais

### Serviços Core
- **DocumentService**: Orquestra todo o processo de geração de documentos
- **TemplateService**: Gerencia templates no banco de dados
- **RequiredFieldsValidator**: Valida campos obrigatórios antes da geração

### Geradores
- **PdfGenerator**: Gera documentos PDF
- **EmailGenerator**: Gera mensagens de email
- **SmsGenerator**: Gera mensagens SMS

### Modelos de Dados
- **Template**: Representa um template com metadata
- **TemplateMetadata**: Contém configurações como campos obrigatórios
- **DocumentRequest**: DTO para requisições de geração

### Tratamento de Erros
- **RequiredFieldsValidationException**: Exceção específica para campos ausentes
- Sistema de logging detalhado para debugging

### Tecnologias
- **Quarkus**: Framework principal
- **Qute**: Engine de templates
- **MongoDB**: Banco de dados
- **Jackson**: Processamento JSON
- **CDI**: Injeção de dependências

Este diagrama fornece uma visão completa da arquitetura e pode ser usado como referência para entender o funcionamento do sistema, implementar novas funcionalidades ou realizar manutenções.