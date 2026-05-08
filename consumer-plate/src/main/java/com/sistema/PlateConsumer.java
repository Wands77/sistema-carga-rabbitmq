package com.sistema;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;
import smile.classification.RandomForest;
import smile.data.formula.Formula;
import smile.data.DataFrame;

public class PlateConsumer {
    private static final String EXCHANGE_NAME = "image_exchange";

    public static void main(String[] argv) throws Exception {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("rabbitmq");

        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();

        channel.exchangeDeclare(EXCHANGE_NAME, "topic");
        String queueName = "fila_placas";
        // queueDeclare(nome, durable, exclusive, autoDelete, arguments)
        channel.queueDeclare(queueName, true, false, false, null);
        
        // Faz o bind para pegar mensagens de placas (routing key termina em .plate)
        channel.queueBind(queueName, EXCHANGE_NAME, "*.plate");
        channel.basicQos(1);

        System.out.println(" [*] Consumidor de Placas (IA 1) aguardando mensagens.");

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
            // Simulando um processamento pesado de IA (Extração de features + OCR/Classificação)
            // Aqui inicializaríamos modelos do Smile se tivéssemos um dataset.
            System.out.println("     -> [IA 1] Extraindo caracteres da placa...");
            System.out.println("     -> [IA 1] Classificando tipo de veículo usando Smile (Carro, Moto, Caminhão)...");
            
            // Pausa de 1000ms para garantir que seja mais lento que a geração, acumulando fila
            Thread.sleep(1000); 
            
            System.out.println("     -> [Concluído] Veículo: Carro | Placa: ABC-1234");
        } catch (InterruptedException _ignored) {
            Thread.currentThread().interrupt();
        }
    }
}
