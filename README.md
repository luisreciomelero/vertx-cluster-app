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

-----------------------------------------------------------------------------------------------

Se ha añadido una prueba de datos compartidos entre distintos verticle de una misma instancia de Vertx. Para ello, nuestra API recibe una petición:

                                    POST http://localhost:8080/sharedData/local/:key 

Una vez recibe la petición, envía la clave a la segunda instancia de Vertx que se encargará de almacenar en el Local Shared Map del event bus, la key. 
Asociada a esta key estará un value, generado de forma aleatoria.

Para comprobar el acceso a este Local Map de Vertx, se ha creado un verticle que se encargará de desplegar la segunda instancia: **ReaderSharedData** 
Este verticle será el encargado de leer el Local Map y devolver el valor asociado a nuestro API. Para ello podemos realizar la petición:

                                    GET http://localhost:8080/sharedData/local/:key 
                                    
                                    
