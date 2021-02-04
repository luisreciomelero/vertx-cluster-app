# vertx-cluster-app

Para lanzar el cluster, en 2 terminales disntintos:

Terminal 1:
  1. cd service-verticle
  2. sh run_cluster_element_1.sh
  
Terminal 2:
  1. cd vertx-rest-api-example
  2. sh run_cluster_element_1.sh

Una vez levantado el cluster, desde postman:

    GET http://localhost:8080/users

    GET http://localhost:8080/users/:username

Usuarios creados por defecto:

    "[{\"username\":\"admin\",\"password\":\"superSecret\"},{\"username\":\"root\",\"password\":\"superExtraSecret\"},        {\"username\":\"lrecio\",\"password\":\"notSecret\"}]"
