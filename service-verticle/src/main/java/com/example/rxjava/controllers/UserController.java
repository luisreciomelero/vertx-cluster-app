package com.example.rxjava.controllers;

import com.example.rxjava.mappers.UserMapper;
import com.example.rxjava.messages.UserMessage;
import com.example.rxjava.models.User;
import com.example.rxjava.services.UserService;
import io.reactivex.Observable;
import io.reactivex.SingleSource;
import io.reactivex.disposables.Disposable;
import io.vertx.core.Handler;
import io.vertx.core.impl.logging.Logger;
import io.vertx.core.impl.logging.LoggerFactory;
import io.vertx.core.json.Json;
import io.vertx.reactivex.core.AbstractVerticle;
import io.vertx.reactivex.core.eventbus.EventBus;
import io.vertx.reactivex.core.eventbus.Message;
import io.vertx.reactivex.core.eventbus.MessageConsumer;
import io.vertx.reactivex.core.shareddata.AsyncMap;
import io.vertx.reactivex.core.shareddata.LocalMap;
import io.vertx.reactivex.core.shareddata.SharedData;

import java.util.Optional;

public class UserController extends AbstractVerticle {

    private static final String ADDRESS = "prueba-address";
    private static final String ADDRESS_PUBLISH = "prueba-publish";
    private static final Logger LOGGER = LoggerFactory.getLogger(UserController.class);
    private UserMapper userMapper = new UserMapper();
    private UserService service = new UserService();
    private static final String ADDRESS_SHARED_DATA_LOCAL = "prueba-sdl";
    private static final String LOCAL_MAP_NAME = "myLocalMap";
    private static final String ADDRESS_SHARED_DATA_GLOBAL = "prueba-sdg";
    private static final String GLOBAL_MAP_NAME = "myGlobalMap";




    public void start() {
        consumerPublishMessage();
        consumerMessage();
        consumerSDLMessage();
        consumerSDGMessage();
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
            msg.replyAndRequest(body);
        });

    }

    private void consumerSDLMessage(){
        final EventBus eventBus = vertx.eventBus();
        MessageConsumer<String> consumer = eventBus.<String>consumer(ADDRESS_SHARED_DATA_LOCAL);
        Observable<Message<String>> observable = consumer.toObservable();

        observable.subscribe(msg ->{
            LOGGER.info("Mensaje: " + msg.body());
            writeDataSL(msg.body());
        },err ->{
            LOGGER.error("Se ha producido un error: " + err);
        });
    }

    private void writeDataSL(String message){
        final SharedData sd = vertx.sharedData();
        final LocalMap<String, String> sharedData = sd.getLocalMap(LOCAL_MAP_NAME);
        final String key = message;
        final String  value = Math.random()+"";

        sharedData.put(key,value);

    }

    private void consumerSDGMessage(){
        final EventBus eventBus = vertx.eventBus();
        MessageConsumer<String> consumer = eventBus.<String>consumer(ADDRESS_SHARED_DATA_GLOBAL);
        Observable<Message<String>> observable = consumer.toObservable();

        observable.subscribe(msg ->{
            LOGGER.info("Mensaje: " + msg.body());

            writeDataGlobal(msg);

            //msg.replyAndRequest("OK");
        },err ->{
            LOGGER.error("Se ha producido un error: " + err);
        });
    }

    private void writeDataGlobal(Message<String> msg){
        final String key = msg.body();
        final String  value = Math.random()+"";
        final SharedData sharedData = vertx.sharedData();
        LOGGER.info("writeDataGlobal");
        sharedData.<String, String>getAsyncMap(GLOBAL_MAP_NAME, res ->{
            if(res.succeeded()){
                AsyncMap<String, String> myAsyncMap = res.result();
                LOGGER.info("myAsyncMap.put");
                myAsyncMap.put(key, value, ar->{
                    if(ar.succeeded()) {
                        msg.replyAndRequest("OK");
                    }
                });
                /*
                myAsyncMap.put(key, value, ar->{
                    if(ar.succeeded()){
                        myAsyncMap.get(key, val->{
                            if(val.succeeded()){
                                LOGGER.info("RESULTADO VALUE: "+ val.result());
                            }
                        });

                    }
                });*/


                //msg.replyAndRequest("OK");
            }else{
                LOGGER.error("Failed to get map!");
            }

        });

    }
}
