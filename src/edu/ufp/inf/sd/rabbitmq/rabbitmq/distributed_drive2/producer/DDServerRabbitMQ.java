package edu.ufp.inf.sd.rabbitmq.rabbitmq.distributed_drive2.producer;

public class DDServerRabbitMQ {

    public static void main(String[] args) {
        // Simula uma operação feita no "servidor"
        String mensagem = "Servidor criou ficheiro exemplo.txt";
        RMQPublisher.publishUpdate(mensagem);
        System.out.println("[SERVER] Enviada mensagem: " + mensagem);
    }
}
