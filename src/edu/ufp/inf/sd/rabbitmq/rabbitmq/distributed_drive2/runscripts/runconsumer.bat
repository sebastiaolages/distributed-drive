@echo off
call setenv consumer

java -cp %CLASSPATH% edu.ufp.inf.sd.rabbitmq.rabbitmq.distributed_drive2.consumer.DDClientRabbitMQ
pause
