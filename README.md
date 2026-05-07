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
   docker-compose up --build
   ```
4. Acesse a interface administrativa do RabbitMQ através do navegador: `http://localhost:15672`.
   * **Credenciais padrão:** `guest` / `guest`
5. Navegue até a aba **Queues and Streams** para monitorar o crescimento do enfileiramento das mensagens em tempo real. Você poderá visualizar o monitoramento das Filas no RabbitMQ. Como na imagem abaixo, onde apresenta o painel administrativo durante a execução do sistema, comprovando o funcionamento da nossa arquitetura distribuída:
   
   <img width="1357" height="646" alt="image" src="https://github.com/user-attachments/assets/a6a53324-778a-47a2-91e7-612f1212e699" />
   
   * **Roteamento Bem-Sucedido:** O *Topic Exchange* está dividindo corretamente as mensagens recebidas em duas filas independentes (`fila_placas` e `fila_sinais`),        baseando-se nas *routing keys*.
   * **Status Ativo:** A coluna *State* como `running` confirma que os containers dos nossos consumidores em Java estão conectados e escutando as filas.
   * **Taxa de Transferência (Message rates):** A coluna *incoming* mostra a carga constante sendo injetada pelo nosso `generator` (ex: 4.2/s para placas e 2.4/s para sinais), enquanto *deliver / get* mostra a taxa em que as IAs estão puxando essas mensagens para processamento.

7. Exemplo de Funcionamento (Logs)

   Abaixo, um exemplo da saída do terminal que demonstra o sistema em operação. Note que, enquanto o **Generator** já enviou a imagem #855, os consumidores ainda estão a processar imagens anteriores (#260 e #168), comprovando o enfileiramento intencional:
   
   ```text
   message-generator  |  [x] Enviado 'image.plate':'Imagem_849 (Conteudo simulado em Base64...)'
   message-generator  |  [x] Enviado 'image.plate':'Imagem_850 (Conteudo simulado em Base64...)'
   message-generator  |  [x] Enviado 'image.plate':'Imagem_851 (Conteudo simulado em Base64...)'
   message-generator  |  [x] Enviado 'image.plate':'Imagem_852 (Conteudo simulado em Base64...)'
   message-generator  |  [x] Enviado 'image.sign':'Imagem_853 (Conteudo simulado em Base64...)'
   message-generator  |  [x] Enviado 'image.sign':'Imagem_854 (Conteudo simulado em Base64...)'
   message-generator  |  [x] Enviado 'image.plate':'Imagem_855 (Conteudo simulado em Base64...)'
   consumer-plate-ai  |      -> [Concluído] Veículo: Carro | Placa: ABC-1234
   consumer-plate-ai  |  [x] Recebido: 'image.plate':'Imagem_260 (Conteudo simulado em Base64...)'
   consumer-plate-ai  |      -> [IA 1] Extraindo caracteres da placa...
   consumer-plate-ai  |      -> [IA 1] Classificando tipo de veículo usando Smile (Carro, Moto, Caminhão)...
   message-generator  |  [x] Enviado 'image.plate':'Imagem_856 (Conteudo simulado em Base64...)'
   message-generator  |  [x] Enviado 'image.plate':'Imagem_857 (Conteudo simulado em Base64...)'
   message-generator  |  [x] Enviado 'image.sign':'Imagem_858 (Conteudo simulado em Base64...)'
   consumer-sign-ai   |      -> [Concluído] Sinal: Pare
   consumer-sign-ai   |  [x] Recebido: 'image.sign':'Imagem_168 (Conteudo simulado em Base64...)'
   consumer-sign-ai   |      -> [IA 2] Identificando formato e cor do sinal...
   consumer-sign-ai   |      -> [IA 2] Classificando classe com Smile (Pare, Velocidade, Proibido)...
   ```

## 🛠️ Tecnologias Utilizadas
* Java 11 (Eclipse Temurin)
* Maven
* RabbitMQ (AMQP Client)
* Smile (Statistical Machine Intelligence and Learning Engine)
* Docker & Docker Compose
