package com.example.rxjava.routers;

import com.example.rxjava.mappers.MessageMapper;
import com.example.rxjava.messages.UserMessage;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.sun.org.apache.bcel.internal.generic.LOOKUPSWITCH;
import io.vertx.core.impl.logging.Logger;
import io.vertx.core.impl.logging.LoggerFactory;
import io.vertx.core.json.Json;
import io.vertx.reactivex.core.AbstractVerticle;
import io.vertx.reactivex.core.eventbus.EventBus;
import io.vertx.reactivex.ext.web.Router;
import io.vertx.reactivex.ext.web.RoutingContext;

import javax.swing.*;

public class UserRouter extends AbstractVerticle {
    private static final int PORT = 8080;
    private static final String PATH_PARAM_USER = "username";
    private static final String PATH_PARAM_SDL = "key";
    private static final String ADDRESS = "prueba-address";
    private static final String ADDRESS_PUBLISH = "prueba-publish";
    private static final String ADDRESS_SHARED_DATA_LOCAL = "prueba-sdl";
    private static final String ADDRESS_SHARED_DATA_LOCAL_READ = "prueba-get-sdl";
    private MessageMapper messageMapper = new MessageMapper();

    private static final Logger LOGGER = LoggerFactory.getLogger(UserRouter.class);


    public void start() {

        Router router = Router.router(vertx);
        router.get("/users")
                .handler(routingContext -> {
                    UserMessage userMessage = new UserMessage("findAll", "");
                    publishMessage(userMessage);
                    requestResponseMessage(userMessage, routingContext);
                });

        router.get("/users/:username").handler(routingContext -> {
            String username = routingContext.request().getParam(PATH_PARAM_USER);
            UserMessage userMessage = new UserMessage("findUser", username);
            //Añadimos como prueba la publicacion de mensajes
            publishMessage(userMessage);
            requestResponseMessage(userMessage, routingContext);
        });
        router.post("/users").handler(routingContext -> {
            String user = routingContext.getBodyAsString();
            UserMessage userMessage = new UserMessage("addUser", user);
            requestResponseMessage(userMessage, routingContext);
            routingContext.response().setStatusCode(204).end();
        });
        router.post("/sharedData/local/:key").handler(routingContext -> {
            String data = routingContext.request().getParam(PATH_PARAM_SDL);
            LOGGER.info("CLAVE A ENVIAR: "+data);
            sendMessageLocalSharedData(data, routingContext, ADDRESS_SHARED_DATA_LOCAL);
            routingContext.response().setStatusCode(204).end();
        });
        router.get("/sharedData/local/:key").handler(routingContext -> {
            String key = routingContext.request().getParam(PATH_PARAM_SDL);
            LOGGER.info("CLAVE A ENVIAR: "+key);
            requestResponseSDL(key,routingContext);
        });

        vertx.createHttpServer()
                .requestHandler(router)
                .rxListen(PORT)
                .subscribe();
    }

    //Metodo para enviar mensajes por eventbus
    /*
     * En este caso hacemos uno del modo eventbus.request que esperará una respuesta, enviando el mensaje por el eventbus a un solo microservicio
     * registrado a esa dirección.
     * Si por el contrario utilizasemos eventbus.publish el mensaje le llegaría a todos los microservicios registrados a esa dirección
     */
    private void requestResponseMessage(UserMessage userMessage, RoutingContext routingContext) {
        final EventBus eventBus = vertx.eventBus();
        try {

            String message = messageMapper.messageToJson(userMessage);
            eventBus.request(ADDRESS, message, reply -> {
                if (reply.succeeded()) {
                    LOGGER.info("Received reply: " + reply.result().body());
                    Object user = reply.result().body();
                    routingContext.response().setStatusCode(200)
                            .putHeader("content-type", "application/json; charset=utf-8").end(Json.encodePrettily(user));
                } else {
                    LOGGER.info("No reply");
                    routingContext.response().setStatusCode(404).end("No reply");
                }
            });
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }

    /*
     * Ejemplo con publicacion de mensajes
     */

    private void publishMessage(UserMessage userMessage) {
        LOGGER.info("NOS DISPONEMOS A PUBLICAR MENSAJE");
        final EventBus eventBus = vertx.eventBus();
        try {
            userMessage.setUser("PRUEBA PUBLICACION");
            String message = messageMapper.messageToJson(userMessage);
            eventBus.publish(ADDRESS_PUBLISH, message);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }
    /*
     * Con este metodo enviaremos datos, de la tercera forma posible, punto a punto a traves del eventbus
     */
    private void sendMessageLocalSharedData(String message, RoutingContext routingContext, String address) {
        final EventBus eventBus = vertx.eventBus();
        LOGGER.info("Envio de datos para shared data");
        eventBus.send(address, message);
    }

    private void requestResponseSDL(String message, RoutingContext routingContext ){
        final EventBus eventBus = vertx.eventBus();
        eventBus.request(ADDRESS_SHARED_DATA_LOCAL_READ, message, reply -> {
            if (reply.succeeded()) {
                LOGGER.info("Received reply: " + reply.result().body());
                Object resp = reply.result().body();
                routingContext.response().setStatusCode(200)
                        .putHeader("content-type", "application/json; charset=utf-8").end(Json.encodePrettily(resp));
            } else {
                LOGGER.info("No reply");
                routingContext.response().setStatusCode(404).end("No reply");
            }
        });

    }


}