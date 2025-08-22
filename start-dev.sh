#!/bin/bash

# Script de inicialização do ambiente de desenvolvimento
# Sistema de Templates com Kafka

set -e

echo "🚀 Iniciando Sistema de Templates com Kafka..."

# Cores para output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Função para log colorido
log() {
    echo -e "${GREEN}[$(date +'%Y-%m-%d %H:%M:%S')] $1${NC}"
}

warn() {
    echo -e "${YELLOW}[$(date +'%Y-%m-%d %H:%M:%S')] WARNING: $1${NC}"
}

error() {
    echo -e "${RED}[$(date +'%Y-%m-%d %H:%M:%S')] ERROR: $1${NC}"
}

# Verificar se Docker está rodando
if ! docker info > /dev/null 2>&1; then
    error "Docker não está rodando. Por favor, inicie o Docker primeiro."
    exit 1
fi

# Verificar se Docker Compose está disponível
if ! command -v docker-compose &> /dev/null; then
    error "Docker Compose não está instalado."
    exit 1
fi

log "Parando containers existentes..."
docker-compose down

log "Removendo volumes antigos (se necessário)..."
read -p "Deseja remover os volumes existentes? (y/N): " -n 1 -r
echo
if [[ $REPLY =~ ^[Yy]$ ]]; then
    docker-compose down -v
    warn "Volumes removidos. Dados serão perdidos."
fi

log "Construindo imagens..."
docker-compose build --no-cache sistemplate-app

log "Iniciando serviços de infraestrutura..."
docker-compose up -d mongodb zookeeper kafka

log "Aguardando serviços ficarem prontos..."
sleep 30

# Verificar se MongoDB está pronto
log "Verificando MongoDB..."
until docker exec sistemplate-mongodb mongosh --eval "db.adminCommand('ping')" > /dev/null 2>&1; do
    warn "Aguardando MongoDB ficar pronto..."
    sleep 5
done
log "✅ MongoDB está pronto!"

# Verificar se Kafka está pronto
log "Verificando Kafka..."
until docker exec sistemplate-kafka kafka-topics.sh --bootstrap-server localhost:9092 --list > /dev/null 2>&1; do
    warn "Aguardando Kafka ficar pronto..."
    sleep 5
done
log "✅ Kafka está pronto!"

# Criar tópicos Kafka
log "Criando tópicos Kafka..."
docker exec sistemplate-kafka kafka-topics.sh \
    --bootstrap-server localhost:9092 \
    --create \
    --topic document-generation \
    --partitions 3 \
    --replication-factor 1 \
    --if-not-exists

docker exec sistemplate-kafka kafka-topics.sh \
    --bootstrap-server localhost:9092 \
    --create \
    --topic document-generation-dlq \
    --partitions 1 \
    --replication-factor 1 \
    --if-not-exists

log "✅ Tópicos Kafka criados!"

# Listar tópicos criados
log "Tópicos Kafka disponíveis:"
docker exec sistemplate-kafka kafka-topics.sh --bootstrap-server localhost:9092 --list

log "Iniciando aplicação..."
docker-compose up -d sistemplate-app

log "Aguardando aplicação ficar pronta..."
sleep 20

# Verificar se a aplicação está respondendo
log "Verificando saúde da aplicação..."
until curl -f http://localhost:8080/q/health/ready > /dev/null 2>&1; do
    warn "Aguardando aplicação ficar pronta..."
    sleep 5
done
log "✅ Aplicação está pronta!"

# Iniciar serviços opcionais
log "Iniciando serviços de monitoramento..."
docker-compose up -d kafka-ui prometheus

log "🎉 Sistema iniciado com sucesso!"
echo
echo -e "${BLUE}📊 URLs disponíveis:${NC}"
echo -e "  • Aplicação:     ${GREEN}http://localhost:8080${NC}"
echo -e "  • Health Check:  ${GREEN}http://localhost:8080/q/health${NC}"
echo -e "  • Metrics:       ${GREEN}http://localhost:8081/q/metrics${NC}"
echo -e "  • OpenAPI:       ${GREEN}http://localhost:8080/q/swagger-ui${NC}"
echo -e "  • Kafka UI:      ${GREEN}http://localhost:8090${NC}"
echo -e "  • Prometheus:    ${GREEN}http://localhost:9090${NC}"
echo
echo -e "${BLUE}🔧 Comandos úteis:${NC}"
echo -e "  • Ver logs:      ${YELLOW}docker-compose logs -f sistemplate-app${NC}"
echo -e "  • Parar tudo:    ${YELLOW}docker-compose down${NC}"
echo -e "  • Reiniciar app: ${YELLOW}docker-compose restart sistemplate-app${NC}"
echo
echo -e "${BLUE}📝 Exemplo de teste:${NC}"
echo -e "${YELLOW}curl -X POST http://localhost:8080/documents/generate \\${NC}"
echo -e "${YELLOW}  -H 'Content-Type: application/json' \\${NC}"
echo -e "${YELLOW}  -d '{${NC}"
echo -e "${YELLOW}    \"templateName\": \"test-template\",${NC}"
echo -e "${YELLOW}    \"data\": {\"name\": \"João\"},${NC}"
echo -e "${YELLOW}    \"async\": true,${NC}"
echo -e "${YELLOW}    \"callbackUrl\": \"http://example.com/callback\"${NC}"
echo -e "${YELLOW}  }'${NC}"
echo