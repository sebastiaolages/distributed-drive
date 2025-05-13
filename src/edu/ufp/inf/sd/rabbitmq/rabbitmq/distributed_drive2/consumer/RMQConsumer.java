package edu.ufp.inf.sd.rabbitmq.rabbitmq.distributed_drive2.consumer;

import com.rabbitmq.client.*;

public class RMQConsumer {

    private final static String QUEUE_NAME = "project_final_queue";

    public static void startConsumer() {
        try {
            ConnectionFactory factory = new ConnectionFactory();
            factory.setHost("localhost");
            Connection connection = factory.newConnection();
            Channel channel = connection.createChannel();

            channel.queueDeclare(QUEUE_NAME, false, false, false, null);
            System.out.println("[RabbitMQ] A ouvir mensagens na fila: " + QUEUE_NAME);

            DeliverCallback deliverCallback = (consumerTag, delivery) -> {
                String mensagem = new String(delivery.getBody(), "UTF-8");
                System.out.println("[RabbitMQ] RECEBIDO: '" + mensagem + "'");
            };

            channel.basicConsume(QUEUE_NAME, true, deliverCallback, consumerTag -> {});
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
