package edu.ufp.inf.sd.rabbitmq.rabbitmq.distributed_drive2.consumer;

public class DDClientRabbitMQ {

    public static void main(String[] args) {
        System.out.println("[CLIENT] A ouvir mensagens...");
        RMQConsumer.startConsumer();
    }
}
