#!/usr/bin/env bash
java $JMX_OPTIONS -Dvertx.logger-delegate-factory-class-name=io.vertx.core.logging.SLF4JLogDelegateFactory -jar target/service-verticle-fat.jar -cluster prueba-cluster
