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
    private MessageMapper messageMapper = new MessageMapper();

    private static final Logger LOGGER = LoggerFactory.getLogger(UserRouter.class);


    public void start() {

        Router router = Router.router(vertx);
        router.get("/users")
                .handler(routingContext -> {
                    UserMessage userMessage = new UserMessage("findAll", "");
                    sendMessage(userMessage, routingContext);
                });

        router.get("/users/:username").handler(routingContext -> {
            String username = routingContext.request().getParam(PATH_PARAM);
            UserMessage userMessage = new UserMessage("findUser", username);
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


}