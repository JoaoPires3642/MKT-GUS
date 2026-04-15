# Tutorial de Instalação e Execução

## Pré-requisitos

- **Java 21** ou superior
- **Node.js 18+** (para o frontend)
- **MySQL 8+** ou **PostgreSQL**
- **Git**

---

## 1. Clonar o Repositório

```bash
git clone https://github.com/JoaoPires3642/MKT-GUS.git
cd MKT-GUS
```

---

## 2. Configurar o Banco de Dados

### MySQL (padrão)

```bash
# Criar banco de dados
mysql -u root -p -e "CREATE DATABASE mkt_gus CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;"
```

### PostgreSQL (alternativa)

Edite o `application.yml`:
```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/mkt_gus
    driver-class-name: org.postgresql.Driver
```

---

## 3. Configurar Variáveis de Ambiente

### Linux/Mac

```bash
export SPRING_DATASOURCE_URL=jdbc:mysql://localhost:3306/mkt_gus
export SPRING_DATASOURCE_USERNAME=root
export SPRING_DATASOURCE_PASSWORD=sua_senha
```

### Windows (PowerShell)

```powershell
$env:SPRING_DATASOURCE_URL="jdbc:mysql://localhost:3306/mkt_gus"
$env:SPRING_DATASOURCE_USERNAME="root"
$env:SPRING_DATASOURCE_PASSWORD="sua_senha"
```

### Ou criar arquivo `.env` na raiz do projeto:

```env
SPRING_DATASOURCE_URL=jdbc:mysql://localhost:3306/mkt_gus
SPRING_DATASOURCE_USERNAME=root
SPRING_DATASOURCE_PASSWORD=sua_senha
```

---

## 4. Rodar o Backend

```bash
cd autoatendimento

# Compilar
./mvnw clean package

# Executar
./mvnw spring-boot:run
```

O backend estará disponível em: **http://localhost:8080**

---

## 5. Rodar o Frontend

Em outro terminal:

```bash
cd self-checkout

# Instalar dependências
npm install --legacy-peer-deps

# Executar
npm run dev
```

O frontend estará disponível em: **http://localhost:3000**

---

## 6. Configurar Scanner de Código de Barras

O sistema utiliza **DroidCam** para escanear códigos de barras via câmera do celular.

### Passos:

1. Instale o **DroidCam** no celular (Android/iOS)
2. Conecte o celular no mesmo WiFi do computador
3. Abra o DroidCam e anote o IP e porta (ex: `192.168.1.100:4747`)
4. Configure o DroidCam no frontend quando solicitado

---

## Configurações Opcionais

### Configurar Pontos (Sistema de Fidelidade)

```yaml
# application.yml
pontos:
  valor-por-ponto: 5.0      # R$ 5 = 1 ponto
  pontos-por-bloco: 10     # blocos de pontos ganhos
```

### Porta Customizada

```bash
# Backend (padrão: 8080)
./mvnw spring-boot:run -Dspring-boot.run.arguments="--server.port=8081"

# Frontend (padrão: 3000)
npm run dev -- -p 3001
```

### URL do Mercado Livre (para desenvolvimento)

```env
MERCADO_LIVRE_API_URL=https://api.mercadolivre.com
MERCADO_LIVRE_API_KEY=sua_chave_aqui
```

---

## Estrutura de Pastas

```
MKT-GUS/
├── autoatendimento/      # Backend Java
│   ├── src/main/java/   # Código fonte
│   ├── src/main/resources/
│   │   └── application.yml
│   └── pom.xml
├── self-checkout/        # Frontend Next.js
│   ├── app/             # Páginas
│   ├── components/      # Componentes React
│   └── lib/             # Utilitários
└── docs/                # Documentação
```

---

## Troubleshooting

### Erro de conexão com banco

```
Check your database settings.
```

**Solução:** Verifique se o MySQL está rodando e as credenciais estão corretas.

### Erro de CORS

```
Access-Control-Allow-Origin
```

**Solução:** O backend já está configurado para `localhost:3000`. Certifique-se de estar rodando o frontend nessa porta.

### Erro ao buscar produto

Verifique:
1. Conexão com internet (para API do Mercado Livre)
2. Código de barras correto
3. Produto disponível no Mercado Livre

### DroidCam não conecta

1. Verifique se celular e computador estão na mesma rede
2. Verifique firewall (libere porta 4747)
3. Tente outro app de câmera IP como alternativa

---

## Links Úteis

- [Documentação Spring Boot](https://spring.io/projects/spring-boot)
- [Documentação Next.js](https://nextjs.org/docs)
- [API Mercado Livre](https://developers.mercadolivre.com.br)
- [Documentação DroidCam](https://www.dev47apps.com/droidcam)

---

## Próximos Passos

Após instalar, consulte:
- [Arquitetura do Sistema](arquitetura.md)
- [Endpoints da API](endpoints.md)
- [Integração com Sistema do Cliente](integracao-api.md)