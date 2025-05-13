@echo off
call setenv producer

java -cp %CLASSPATH% edu.ufp.inf.sd.rabbitmq.rabbitmq.distributed_drive2.producer.DDServerRabbitMQ
pause
