package com.example.rxjava.controllers;

import com.example.rxjava.messages.UserMessage;
import com.example.rxjava.models.User;
import io.reactivex.Observable;
import io.reactivex.disposables.Disposable;
import io.vertx.core.impl.logging.Logger;
import io.vertx.core.impl.logging.LoggerFactory;
import io.vertx.core.json.Json;
import io.vertx.reactivex.core.AbstractVerticle;
import io.vertx.reactivex.core.eventbus.EventBus;
import io.vertx.reactivex.core.eventbus.Message;
import io.vertx.reactivex.core.eventbus.MessageConsumer;
import io.vertx.reactivex.core.shareddata.LocalMap;
import io.vertx.reactivex.core.shareddata.SharedData;

import java.util.Optional;

public class ReaderSharedData extends AbstractVerticle {

    private static final Logger LOGGER = LoggerFactory.getLogger(ReaderSharedData.class);


    private static final String ADDRESS_SHARED_DATA_LOCAL_READ = "prueba-get-sdl";
    private static final String LOCAL_MAP_NAME = "myLocalMap";


    public void start() {
        readSDl();
    }

    private Disposable readSDl() {
        final EventBus eventBus = vertx.eventBus();
        MessageConsumer<String> consumer = eventBus.<String>consumer(ADDRESS_SHARED_DATA_LOCAL_READ);
        Observable<Message<String>> observable = consumer.toObservable();

        return observable.subscribe(msg -> {
            // Got message

            LOGGER.info("KEY: "+ msg.body());
            final SharedData sd = vertx.sharedData();
            final LocalMap<String, String> sharedData = sd.getLocalMap(LOCAL_MAP_NAME);
            String value = sharedData.get(msg.body());
            LOGGER.info("VALUE: "+ value);
            msg.reply(value);
        });

    }


}
