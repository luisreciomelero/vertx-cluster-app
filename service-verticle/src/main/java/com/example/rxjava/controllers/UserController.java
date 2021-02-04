package com.example.rxjava.controllers;

import com.example.rxjava.mappers.UserMapper;
import com.example.rxjava.messages.UserMessage;
import com.example.rxjava.models.User;
import com.example.rxjava.services.UserService;
import io.reactivex.Observable;
import io.reactivex.disposables.Disposable;
import io.vertx.core.impl.logging.Logger;
import io.vertx.core.impl.logging.LoggerFactory;
import io.vertx.core.json.Json;
import io.vertx.reactivex.core.AbstractVerticle;
import io.vertx.reactivex.core.eventbus.EventBus;
import io.vertx.reactivex.core.eventbus.Message;
import io.vertx.reactivex.core.eventbus.MessageConsumer;

import java.util.Optional;

public class UserController extends AbstractVerticle {

    private static final String ADDRESS = "prueba-address";
    private static final String ADDRESS_PUBLISH = "prueba-publish";
    private static final Logger LOGGER = LoggerFactory.getLogger(UserController.class);
    private UserMapper userMapper = new UserMapper();
    private UserService service = new UserService();


    public void start() {
        consumerPublishMessage();
        consumerMessage();
    }
    /*
     * La unica función de este metodo es demostrar que cuando levanto varias instancias de este verticle
     * los mensajes que se publican en esta dirección le llegan a todos los consumidores.
     */
    private void consumerPublishMessage(){
        final EventBus eventBus = vertx.eventBus();
        eventBus.consumer(ADDRESS_PUBLISH, msg ->{
            LOGGER.info("HA LLEGADO EL MENSAJE: " + msg.body());
        });
    }

    private Disposable consumerMessage() {
        final EventBus eventBus = vertx.eventBus();
        MessageConsumer<String> consumer = eventBus.<String>consumer(ADDRESS);
        Observable<Message<String>> observable = consumer.toObservable();

        return observable.subscribe(msg -> {
            // Got message

            LOGGER.info(msg.body().toString());
            String body = "";
            UserMessage userMessage = Json.decodeValue(msg.body(), UserMessage.class);
            switch (userMessage.getAction()) {
                case "findAll":
                    body = Json.encode(service.findAll());
                    break;
                case "findUser":
                    Optional<User> opUser = service.findByUsername(userMessage.getUser());
                    if (opUser.isPresent()) {
                        body = Json.encode(opUser.get());
                    }
                    break;
                case "addUser":
                    User user = userMapper.JsonToUser(userMessage.getUser());
                    service.create(user);
                    body = userMessage.getUser();
                    break;
            }
            msg.reply(body);
        });

    }
}
