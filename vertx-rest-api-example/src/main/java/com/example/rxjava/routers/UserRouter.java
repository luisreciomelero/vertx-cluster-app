package com.example.rxjava.routers;

import com.example.rxjava.mappers.MessageMapper;
import com.example.rxjava.messages.UserMessage;
import com.fasterxml.jackson.core.JsonProcessingException;
import io.vertx.core.impl.logging.Logger;
import io.vertx.core.impl.logging.LoggerFactory;
import io.vertx.core.json.Json;
import io.vertx.reactivex.core.AbstractVerticle;
import io.vertx.reactivex.core.eventbus.EventBus;
import io.vertx.reactivex.ext.web.Router;
import io.vertx.reactivex.ext.web.RoutingContext;

public class UserRouter extends AbstractVerticle {
    private static final int PORT = 8080;
    private static final String PATH_PARAM = "username";
    private static final String ADDRESS = "prueba-address";
    private static final String ADDRESS_PUBLISH = "prueba-publish";
    private MessageMapper messageMapper = new MessageMapper();

    private static final Logger LOGGER = LoggerFactory.getLogger(UserRouter.class);


    public void start() {

        Router router = Router.router(vertx);
        router.get("/users")
                .handler(routingContext -> {
                    UserMessage userMessage = new UserMessage("findAll", "");
                    publishMessage(userMessage);
                    sendMessage(userMessage, routingContext);
                });

        router.get("/users/:username").handler(routingContext -> {
            String username = routingContext.request().getParam(PATH_PARAM);
            UserMessage userMessage = new UserMessage("findUser", username);
            //Añadimos como prueba la publicacion de mensajes
            publishMessage(userMessage);
            sendMessage(userMessage, routingContext);
        });
        router.post("/users").handler(routingContext -> {
            String user = routingContext.getBodyAsString();
            UserMessage userMessage = new UserMessage("addUser", user);
            sendMessage(userMessage, routingContext);
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
    private void sendMessage(UserMessage userMessage, RoutingContext routingContext) {
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


}