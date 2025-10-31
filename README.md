# Ejercicio técnico de Java 21 + spring boot

* La configuración está en YAML, separada en prod y dev
* Dockerizado y soporte de docker-compose
* H2 en memoria así puedo simular una BBDD relacional
* CustomUserDetailsService para spring-security
* Makefile para pruebas
* scripts para dev y prod
* OpenAPI (Swagger) en /swagger-ui
* JavaDOC sobre métodos publicos
* BookController en estilo mas OOP JAVA
* UserController más estilo funcional
* Los services me baso en lambdas para los métodos
* Soporte JWT con rutas protegidas
* CORS con "*" para dev y con variables de sistema para prod
* Argon2id para password hash
* Tests unitarios de los services
* Tests de integración sobre repositorios
* Tasks nuevas en build.gradle usando groovy
* Uso de BaseEntity para añadir campos en los dominios (created_at, updated_at)