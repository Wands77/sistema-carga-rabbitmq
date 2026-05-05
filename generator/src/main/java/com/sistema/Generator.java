package com.sistema;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.util.Random;

public class Generator {
    private static final String EXCHANGE_NAME = "image_exchange";

    public static void main(String[] argv) throws Exception {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("rabbitmq"); // Nome do container do RabbitMQ no Docker network

        try (Connection connection = factory.newConnection();
             Channel channel = connection.createChannel()) {

            // Declara um exchange do tipo TOPIC
            channel.exchangeDeclare(EXCHANGE_NAME, "topic");
            Random random = new Random();
            int count = 0;

            System.out.println(" [*] Iniciando gerador de imagens...");

            while (true) {
                // Sorteia o tipo de imagem e define a routing key (plate ou sign)
                boolean isPlate = random.nextBoolean();
                String routingKey = isPlate ? "image.plate" : "image.sign";
                String message = "Imagem_" + count + " (Conteudo simulado em Base64...)";

                channel.basicPublish(EXCHANGE_NAME, routingKey, null, message.getBytes("UTF-8"));
                System.out.println(" [x] Enviado '" + routingKey + "':'" + message + "'");
                count++;

                // Pausa de 150ms resulta em ~6.6 mensagens por segundo (mais de 5/seg)
                Thread.sleep(150);
            }
        }
    }
}