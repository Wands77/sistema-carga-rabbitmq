package com.sistema;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;

public class SignConsumer {
    private static final String EXCHANGE_NAME = "image_exchange";

    public static void main(String[] argv) throws Exception {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("rabbitmq");

        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();

        channel.exchangeDeclare(EXCHANGE_NAME, "topic");
        String queueName = "fila_sinais";
        channel.queueDeclare(queueName, true, false, false, null);
        
        // Faz o bind para pegar apenas mensagens de sinais
        channel.queueBind(queueName, EXCHANGE_NAME, "*.sign");
        channel.basicQos(1);

        System.out.println(" [*] Consumidor de Sinais (IA 2) aguardando mensagens.");

        DeliverCallback deliverCallback = (consumerTag, delivery) -> {
            String message = new String(delivery.getBody(), "UTF-8");
            System.out.println(" [x] Recebido: '" + delivery.getEnvelope().getRoutingKey() + "':'" + message + "'");
            
            processImageWithSmile(message);
            channel.basicAck(delivery.getEnvelope().getDeliveryTag(), false);
        };
        channel.basicConsume(queueName, false, deliverCallback, consumerTag -> { });
    }

    private static void processImageWithSmile(String imagePayload) {
        try {
            // Simulando processamento da IA
            System.out.println("     -> [IA 2] Identificando formato e cor do sinal...");
            System.out.println("     -> [IA 2] Classificando classe com Smile (Pare, Velocidade, Proibido)...");
            
            // Atraso intencional para gargalo (1.5 segundos)
            Thread.sleep(1500); 
            
            System.out.println("     -> [Concluído] Sinal: Pare");
        } catch (InterruptedException _ignored) {
            Thread.currentThread().interrupt();
        }
    }
}
