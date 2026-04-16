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

---

## 4. Rodar o Backend

```bash
cd autoatendimento
./mvnw clean package
./mvnw spring-boot:run
```

Backend: **http://localhost:8080**

---

## 5. Rodar o Frontend

Em outro terminal:

```bash
cd self-checkout
npm install --legacy-peer-deps
npm run dev
```

Frontend: **http://localhost:3000**

---

## 6. Configurar Scanner de Código de Barras

O sistema utiliza **DroidCam** para escanear códigos de barras via câmera do celular.

1. Instale o **DroidCam** no celular
2. Conecte o celular no mesmo WiFi do computador
3. Abra o DroidCam e anote o IP e porta (ex: `192.168.1.100:4747`)
4. Configure no frontend quando solicitado

---

## Configurações Opcionais

### Sistema de Pontos

```yaml
pontos:
  valor-por-ponto: 5.0      # R$ 5 = 1 ponto
  pontos-por-bloco: 10       # blocos de pontos ganhos
```

---

## Troubleshooting

### Erro de conexão com banco
Verifique se o MySQL está rodando e as credenciais estão corretas.

### Erro de CORS
O backend já está configurado para `localhost:3000`.

### DroidCam não conecta
Verifique se celular e computador estão na mesma rede e libere a porta 4747.

---

## Links Úteis

- [Documentação Spring Boot](https://spring.io/projects/spring-boot)
- [Documentação Next.js](https://nextjs.org/docs)
- [API Mercado Livre](https://developers.mercadolivre.com.br)