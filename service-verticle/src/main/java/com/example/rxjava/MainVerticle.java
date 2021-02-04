package com.example.rxjava;

import com.example.rxjava.controllers.UserController;
import io.reactivex.Single;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Verticle;
import io.vertx.core.impl.logging.Logger;
import io.vertx.core.impl.logging.LoggerFactory;
import io.vertx.reactivex.core.AbstractVerticle;
import io.vertx.reactivex.core.RxHelper;
import io.vertx.reactivex.core.Vertx;

public class MainVerticle extends AbstractVerticle {

    private static final Logger LOGGER = LoggerFactory.getLogger(MainVerticle.class);

    //private static Vertx vertx = io.vertx.reactivex.core.Vertx.vertx();

    public  void start(){
        deployVerticle(new UserController());

    }
    private void deployVerticle(Verticle verticle) {
        DeploymentOptions deploymentOptions = new DeploymentOptions();
        deploymentOptions.setConfig(config());
        Single<String> deployRouter = RxHelper.deployVerticle(vertx, verticle, deploymentOptions);
        deployRouter.subscribe(id -> {
            System.out.println("OK");
            System.out.println(id);
        }, err -> {
            // Could not deploy
            System.out.println(err);
            System.out.println("NO OK");
        });
    }


}
