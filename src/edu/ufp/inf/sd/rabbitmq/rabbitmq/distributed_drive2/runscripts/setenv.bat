@REM ************************************************************************************
@REM Description: setenv para projeto RabbitMQ - projeto_finalSD2025
@REM Author: sebas
@REM ************************************************************************************

@SET SCRIPT_ROLE=%1

@REM ==== CONFIGURAÇÕES DO TEU PC E IDE ====
@SET USERNAME=sebas
@SET JDK=C:\Users\sebas\.jdks\corretto-1.8.0_422
@SET CURRENT_IDE=IntelliJ

@REM ==== DADOS DO PROJETO ====
@SET JAVAPROJ_NAME=projeto_finalSD2025
@SET JAVAPROJ=C:\Users\sebas\Desktop\faculdade\sd\%JAVAPROJ_NAME%

@REM ==== CAMINHO PARA AS CLASSES E LIBS ====
@SET JAVAPROJ_SRC=src
@SET JAVAPROJ_CLASSES=out\production\%JAVAPROJ_NAME%
@SET JAVAPROJ_LIB=src\edu\ufp\inf\sd\rabbitmq\rabbitmq\lib

@SET JAVA_LIBS="%JAVAPROJ%\%JAVAPROJ_LIB%\amqp-client-5.9.0.jar";"%JAVAPROJ%\%JAVAPROJ_LIB%\slf4j-api-1.7.30.jar";"%JAVAPROJ%\%JAVAPROJ_LIB%\slf4j-simple-1.7.30.jar"

@REM ==== CLASSPATH FINAL ====
@SET CLASSPATH="%JAVAPROJ%\%JAVAPROJ_CLASSES%";%JAVA_LIBS%

@REM ==== OUTROS CAMINHOS ====
@SET ABSPATH2CLASSES=%JAVAPROJ%\%JAVAPROJ_CLASSES%
@SET ABSPATH2SRC=%JAVAPROJ%\%JAVAPROJ_SRC%

@REM ==== CONFIG DO BROKER RABBITMQ ====
@SET BROKER_HOST=localhost
@SET BROKER_PORT=5672
@SET BROKER_QUEUE=project_final_queue
