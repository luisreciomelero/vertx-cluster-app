# vertx-cluster-app

Para lanzar el cluster, en 2 terminales distintos:

Terminal 1:

    cd service-verticle
    sh run_cluster_element_1.sh
  
Terminal 2:

    cd vertx-rest-api-example
    sh run_cluster_element_1.sh
    

Una vez levantado el cluster, desde postman:

    GET http://localhost:8080/users

    GET http://localhost:8080/users/:username

Usuarios creados por defecto:

    "[{\"username\":\"admin\",\"password\":\"superSecret\"},{\"username\":\"root\",\"password\":\"superExtraSecret\"},        {\"username\":\"lrecio\",\"password\":\"notSecret\"}]"


Si levantamos un segunda instancia de service-verticle, en una nueva terminal del mismo modo que la primera, y realizamos peticiones de forma consecutiva podemos comprobar como se distribuyen entre ambos verticle mediante round-robin.
