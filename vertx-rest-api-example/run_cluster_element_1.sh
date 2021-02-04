#!/usr/bin/env bash
mvn clean package -DskipTests
JMX_OPTIONS="-Dcom.sun.management.jmxremote=true -Dcom.sun.management.jmxremote.port=7778 -Dcom.sun.management.jmxremote.authenticate=false -Dcom.sun.management.jmxremote.ssl=false -Djava.rmi.server.hostname=paradigma.pruebavertx "
java $JMX_OPTIONS -Dvertx.logger-delegate-factory-class-name=io.vertx.core.logging.SLF4JLogDelegateFactory -jar target/observer-pattern-fat.jar -cluster prueba-cluster
