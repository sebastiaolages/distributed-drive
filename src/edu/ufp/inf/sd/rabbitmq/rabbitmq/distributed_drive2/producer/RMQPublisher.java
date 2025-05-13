package edu.ufp.inf.sd.rabbitmq.rabbitmq.distributed_drive2.producer;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

public class RMQPublisher {

    private final static String QUEUE_NAME = "project_final_queue";

    public static void publishUpdate(String message) {
        try {
            ConnectionFactory factory = new ConnectionFactory();
            factory.setHost("localhost");
            try (Connection connection = factory.newConnection();
                 Channel channel = connection.createChannel()) {
                channel.queueDeclare(QUEUE_NAME, false, false, false, null);
                channel.basicPublish("", QUEUE_NAME, null, message.getBytes("UTF-8"));
                System.out.println("[RabbitMQ] Mensagem publicada: '" + message + "'");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
