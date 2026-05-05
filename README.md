# Sistema de Carga com IA e RabbitMQ

Este projeto implementa um sistema distribuído conteinerizado em Java, utilizando RabbitMQ como broker de mensagens para rotear imagens simuladas para modelos de IA específicos.

## 🏗️ Arquitetura
O sistema é composto por 4 containers conectados via rede Docker interna (`rabbitmq-net`):
1. **RabbitMQ Broker:** Servidor de mensageria com interface de administração habilitada.
2. **Gerador (Producer):** Gera carga constante enviando mais de 5 mensagens por segundo para o RabbitMQ (Topic Exchange). As mensagens são publicadas com as routing keys `image.plate` ou `image.sign`.
3. **Consumidor IA 1 (Placas):** Retira mensagens da fila `fila_placas`. Possui uma IA embutida que simula a extração de caracteres e a classificação do tipo de veículo (Carro, Moto, Caminhão) utilizando a biblioteca Smile.
4. **Consumidor IA 2 (Sinais):** Retira mensagens da fila `fila_sinais`. Possui uma IA embutida que simula a identificação e classificação de sinais de trânsito (Pare, Velocidade, Proibido) utilizando a biblioteca Smile.

## ⚠️ Observação sobre o Gargalo
Em conformidade com os requisitos técnicos do sistema, a taxa de processamento dos consumidores possui um atraso intencional. Isso garante que as mensagens sejam consumidas de forma mais lenta do que a taxa de geração, permitindo que a fila encha visivelmente no painel de administração.

## 🚀 Como Executar

1. Certifique-se de ter o Docker e o Docker Compose instalados no seu ambiente.
2. Clone este repositório.
3. Na raiz do projeto, suba os containers executando o comando:
   ```bash
   docker-compose up
   ```
4. Acesse a interface administrativa do RabbitMQ através do navegador: `http://localhost:15672`.
   * **Credenciais padrão:** `guest` / `guest`
5. Navegue até a aba **Queues** para monitorar o crescimento do enfileiramento das mensagens em tempo real.

## 🛠️ Tecnologias Utilizadas
* Java 11 (Eclipse Temurin)
* Maven
* RabbitMQ (AMQP Client)
* Smile (Statistical Machine Intelligence and Learning Engine)
* Docker & Docker Compose